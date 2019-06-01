package v
package reducers

import scalajs.js

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
  overrides: Vector[(Int, Int, Rational[Int])] = Vector.empty,
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

  final case class set_wait(value: Int) extends Action {
    override def apply(state: State): State =
      state.copy(wait_duration = value)
  }

  final case class override_prob(cati: Int, probi: Int, prob: (Int, Int)) extends Action {
    override def apply(state: State): State =
      state.copy(
        overrides = state.overrides :+ (cati, probi, Rational(prob._1, prob._2))
      )
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

      val cati = mane.getRandomInt(state.cats.size)
      val probi = mane.getRandomInt(state.cats(cati).probs.size)

      val mod = if (state.wait_duration == 0) {
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

}
