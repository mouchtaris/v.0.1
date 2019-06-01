package v
package app

import scalajs.js

@js.annotation.JSExportAll
final case class State(
  wait_duration: Int = 0,
  talking_shit: Boolean = false,
  overrides: Map[Int, Map[Int, Rational[Int]]] = Map.empty,
  group_overrides: Map[Int, Rational[Int]] = Map.empty,
  cats: Group[Group[Prob]] = Group("cats", Rational.one[Int]),
  instructions: Vector[(Int, Int)] = Vector.empty,
  selected_j: Int = 0,
  selected_i: Int = 0,
) {
  def prob_mod(j: Int, i: Int, mod: Prob ⇒ Prob): State =
    cats.get(j)
      .flatMap { cat: Group[Prob] ⇒
        cat.get(i)
          .map(mod)
          .map { prob ⇒ cats.updated(j, cat.updated(i, prob)) }
          .map { cats2 ⇒ copy(cats = cats2) }
      }
      .getOrElse(this)

  def cat_mod(j: Int, mod: Group[Prob] ⇒ Group[Prob]): State =
    cats.get(j)
      .map(mod)
      .map { cat ⇒ cats.updated(j, cat) }
      .map { cats2 ⇒ copy(cats = cats2) }
      .getOrElse(this)

  def add_override(j: Int, i: Int, prob: Rational[Int]): State = {
    val cat = overrides.getOrElse(j, Map.empty)
    val over = cat.getOrElse(i, Rational.zero[Int])
    val cat2 = cat.updated(i, prob)
    val over2 = overrides.updated(j, cat2)
    val state2 = copy(overrides = over2)
    state2
  }

  def add_group_override(j: Int, prob: Rational[Int]): State = {
    val over2 = group_overrides.updated(j, prob)
    copy(group_overrides = over2)
  }

  def normalize: State = {
    val cats2 = overrides
      .map {
        case (oj, vals) ⇒
          cats
            .get(oj)
            .map { cat ⇒
              val sum = vals.values.sum.norm
              val rem = (Rational.one[Int] + -sum).norm
              val part = (Rational(1, cat.size - vals.size) * rem).norm
              val lcm = (vals.values.toVector :+ part).foldLeft(Rational.one[Int])(_ lcmrat _)
              println(s"sum=$sum lcm=$lcm rem=$rem part=$part")
              val clean_probs = cat.map { _ copy (prob = part) }
              val probs2 = vals.foldLeft(clean_probs) {
                case (probs, (oi, or)) ⇒
                  val prev = probs(oi)
                  val prob2 = prev.copy(prob = or.modlcm(lcm.o))
                  probs.updated(oi, prob2)
              }
              val cat2 = cat.updated(probs2)
              (oj, cat2)
            }
      }
      .foldLeft(cats) { (cats, catopt) ⇒
        catopt
          .map { case (oj, cat2) ⇒ cats.updated(oj, cat2) }
          .getOrElse(cats)
      }

    copy(cats = cats2)
  }

  def normalize_groups: State = {
    import Rational.one
    val sum = group_overrides.values.sum.norm
    val rem = (one[Int] + -sum).norm
    val part = (Rational(1, cats.size - group_overrides.size) * rem).norm
    val lcm = (group_overrides.values.toVector :+ part).foldLeft(one[Int])(_ lcmrat _)
    println(s"sum=$sum lcm=$lcm rem=$rem part=$part")
    val clean_probs = cats.map { _ copy (prob = part) }
    val probs2 = group_overrides.foldLeft(clean_probs) {
      case (probs, (oj, or)) ⇒
        val prev = probs.apply(oj)
        val prob2 = prev.copy(prob = or.modlcm(lcm.o))
        probs.updated(oj, prob2)
    }
    val cats2 = cats.updated(probs2)
    copy(cats = cats2)
  }
}
