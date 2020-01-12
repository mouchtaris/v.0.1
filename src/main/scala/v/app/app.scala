package v
package app

import scalajs.js

@js.annotation.JSExportAll
final class app(mane: Main) {

  val store =
    redux.Store[Action, State](
      State(),
      Seq(redux.reduce_commands()),
    )

  import store.dispatch

  final val JOKER = "FUCK YOU"

  def get_random_wait(state: State): Int =
    state.min_wait + mane.getRandomInt(state.wait_span)

  case object init extends Action {
    override def apply(state: State): State = {
      val cats2 = cats
        .zipWithIndex
        .map {
          case ((cat_name, probs_names), cati) ⇒
            val probs = (probs_names /* :+ JOKER */).zipWithIndex.map {
              case (prob_name, probi) ⇒
                Prob(name = prob_name, prob = Rational(1, probs_names.size))
            }
            Group(
              name = cat_name,
              prob = Rational(1, cats.size),
              items_ = probs
            )
        }
        .toVector
      state
        .copy(cats = Group("cats", Rational(1, 1), cats2))
        .normalize
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
        .add_override(cati, probi, Rational(prob))
        .prob_mod(cati, probi, _.copy(overriden = true))
        .normalize
  }

  final case class deoverride_prob(cati: Int, probi: Int) extends Action {
    override def apply(state: State): State =
      state
        .remove_override(cati, probi)
        .prob_mod(cati, probi, _.copy(overriden = false))
        .normalize
  }

  final case class override_group_prob(cati: Int, prob: (Int, Int)) extends Action {
    override def apply(state: State): State =
      state
        .add_group_override(cati, Rational(prob))
        .cat_mod(cati, _.copy(overriden = true))
        .normalize_groups
  }

  final case class deoverride_group_prob(cati: Int) extends Action {
    override def apply(state: State): State =
      state
        .remove_group_override(cati)
        .cat_mod(cati, _.copy(overriden = false))
        .normalize_groups
  }

  final case class set_instructed(cati: Int, probi: Int) extends Action {
    override def apply(state: State): State = {
      val s1 = state

      val s2 = state.instructions.lastOption
        .map { case (j, i) ⇒ s1.prob_mod(j, i, _.copy(instructed = false)) }
        .getOrElse(s1)

      val s3 = s2.prob_mod(cati, probi, _.copy(instructed = true))

      s3.copy(instructions = s2.instructions :+ (cati, probi))
    }
  }

  final case class say(phrase: String) extends Action {
    override def apply(state: State): State = {
      mane.say(phrase)
      state
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
        val mod0 = set_wait(get_random_wait(state)).apply _
        val (cati, cat) = state.cats.rand(mane.getRandomInt)
        val (probi, _) = cat.rand(mane.getRandomInt)

        (set_wait(get_random_wait(state)).apply _)
          .andThen { select(cati, probi).apply _ }
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

  case class set_min_sleep(value: Int) extends Action {
    override def apply(state: State): State =
      state.copy(min_wait = value)
  }

  case class set_sleep_span(value: Int) extends Action {
    override def apply(state: State): State =
      state.copy(wait_span = value)
  }

  case class select(cati: Int, probi: Int) extends Action {
    override def apply(state: State): State =
      (set_instructed(cati, probi).apply _)
        .andThen { _ copy (selected_j = cati, selected_i = probi) }
        .apply(state)
  }

  case object select_random extends Action {
    override def apply(state: State): State = {
      val (cati, cat) = state.cats.rand(mane.getRandomInt)
      val (probi, _) = cat.rand(mane.getRandomInt)
      select(cati, probi)(state)
    }
  }
}
