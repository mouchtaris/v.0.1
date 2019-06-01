package v
package app

import scalajs.js

@js.annotation.JSExportAll
final case class State(
  wait_duration: Int = 0,
  talking_shit: Boolean = false,
  overrides: Map[Int, Map[Int, Rational[Int]]] = Map.empty,
  cats: Group[Group[Prob]] = Group("cats", Rational.one[Int]),
  instructions: Vector[(Int, Int)] = Vector.empty,
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

  def add_override(j: Int, i: Int, prob: Rational[Int]): State = {
    val cat = overrides.getOrElse(j, Map.empty)
    val over = cat.getOrElse(i, Rational.zero[Int])
    val cat2 = cat.updated(i, prob)
    val over2 = overrides.updated(j, cat2)
    val state2 = copy(overrides = over2)
    state2
  }

  def normalize: State = {
    val cats2 = overrides
      .map {
        case (oj, vals) ⇒
          cats
            .get(oj)
            .map { cat ⇒
              val sum = vals.values.sum
              val rem = Rational(1, 1) + -sum
              val part = (Rational(1, cat.size - vals.size) * rem).norm
              println(s"sum=$sum rem=$rem part=$part")
              val clean_probs = cat.map { _ copy (prob = part) }
              val probs2 = vals.foldLeft(clean_probs) {
                case (probs, (oi, or)) ⇒
                  val prev = probs(oi)
                  val prob2 = prev.copy(prob = or)
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
}
