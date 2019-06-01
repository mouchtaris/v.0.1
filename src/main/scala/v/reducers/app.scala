package v
package reducers

import scalajs.js
import ModDeco.ModDecoration

final case class Prob(
  name: String,
  prob: Rational[Int],
  instructed: Boolean = false,
  overriden: Boolean = false,
)

final case class Group(
  name: String,
  probs: Vector[Prob],
)

@js.annotation.JSExportAll
final case class State(
  wait_duration: Int = 0,
  talking_shit: Boolean = false,
  overrides: Map[Int, Map[Int, Rational[Int]]] = Map.empty,
  cats: Vector[Group] = Vector.empty,
  instructions: Vector[(Int, Int)] = Vector.empty,
) {
  def prob_mod(j: Int, i: Int, mod: Prob ⇒ Prob): State =
    cats.lift(j)
      .flatMap { cat: Group ⇒
        cat.probs.lift(i)
          .map(mod)
          .map {
            cat.probs.updated(i, _)
          }
          .map { probs ⇒ cat.copy(probs = probs) }
          .map { cat2 ⇒ cats.updated(j, cat2) }
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
            .lift(oj)
            .map { cat ⇒
              val sum = vals.values.sum
              val rem = Rational(1, 1) + -sum
              val part = (Rational(1, cat.probs.size - vals.size) * rem).norm
              println(s"sum=$sum rem=$rem part=$part")
              val clean_probs = cat.probs.map { _ copy (prob = part) }
              val probs2 = vals.foldLeft(clean_probs) {
                case (probs, (oi, or)) ⇒
                  val prev = probs(oi)
                  val prob2 = prev.copy(prob = or)
                  probs.updated(oi, prob2)
              }
              val cat2 = cat.copy(probs = probs2)
              (oj, cat2)
            }
      }
      .foldLeft(cats) { (cats, catopt: Option[(Int, Group)]) ⇒
        catopt
          .map { case (oj, cat2) ⇒ cats.updated(oj, cat2) }
          .getOrElse(cats)
      }

    copy(cats = cats2)
  }
}

trait Action extends redux.Command[State] {
  def apply(state: State): State
  def toString: String
}

@js.annotation.JSExportAll
final class app(mane: Main) {

  val store =
    redux.Store[Action, State](
      State(),
      Seq(redux.reduce_commands()),
    )

  import store.dispatch

  case object init extends Action {
    override def apply(state: State): State = {
      val cats2 = cats
        .zipWithIndex.map {
          case ((cat_name, probs_names), cati) ⇒
            val probs = probs_names.zipWithIndex.map {
              case (prob_name, probi) ⇒
                Prob(name = prob_name, prob = Rational(1, probs_names.size))
            }
            Group(
              name = cat_name,
              probs = probs
            )
        }
        .toVector
      state.copy(cats = cats2)
    }
  }

  final case class set_talking_shit(value: Boolean) extends Action {
    override def apply(state: State): State =
      state.copy(talking_shit = value)
  }

  final case object toggle_talking extends Action {
    override def apply(state: State): State =
      set_talking_shit(!state.talking_shit)(state)
  }

  final case class set_wait(value: Int) extends Action {
    override def apply(state: State): State =
      state.copy(wait_duration = value)
  }

  final case class override_prob(cati: Int, probi: Int, prob: (Int, Int)) extends Action {
    override def apply(state: State): State =
      state
        .add_override(cati, probi, Rational(prob._1, prob._2))
        .prob_mod(cati, probi, _.copy(overriden = true))
        .normalize
  }

  final case class set_instructed(cati: Int, probi: Int) extends Action {
    override def apply(state: State): State = {
      val s1 = state.prob_mod(cati, probi, _.copy(instructed = true))
      val s2 = state.instructions.lastOption
        .map { case (j, i) ⇒ s1.prob_mod(j, i, _.copy(instructed = false)) }
        .getOrElse(s1)
      s2.copy(instructions = s2.instructions :+ (cati, probi))
    }
  }

  case object tick extends Action {
    override def apply(state: State): State = {
      if (state.talking_shit)
        dom.jsg.setTimeout(
          () ⇒ dispatch(tick),
          timeout = 1000
        )

      val mod = if (state.wait_duration == 0) {
        val cati = mane.getRandomInt(state.cats.size)
        val probi = mane.getRandomInt(state.cats(cati).probs.size)
        val cat = state.cats(cati)
        val prob = cat.probs(probi)

        (set_wait(mane.get_random_wait()).apply _)
          .andThen { set_instructed(cati, probi).apply }
      }
      else
        set_wait(state.wait_duration - 1).apply _

      val result = mod(state)
      mane.draw(result)
      result
    }
  }

  case object tick_if_talking extends Action {
    override def apply(state: State): State =
      if (state.talking_shit)
        tick(state)
      else
        state
  }

}
