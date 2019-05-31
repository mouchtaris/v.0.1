package v
package reducers

import scalajs.js

trait Action extends redux.Command[State] {
  def apply(state: State): State
  def toString: String
}

@js.annotation.JSExportAll
final class app(mane: Main) {

  val store =
    redux.Store[Action, State](
      mane.make_state(),
      Seq(redux.reduce_commands()),
    )

  import store.dispatch

  case object init extends Action {
    override def apply(state: State): State =
      state.copy(wait_duration = mane.get_random_wait())
  }

  case object tick extends Action {
    override def apply(state: State): State = {
      if (state.talking_shit)
        dom.jsg.setTimeout(
          () ⇒ dispatch(tick),
          timeout = 1000
        )

      def probs2 = {
        val selected_cat = mane.getRandomInt(state.probs.size)
        val cat = state.probs(selected_cat)
        val selected_prob = mane.getRandomInt(cat.probs.size)

        state.probs.zipWithIndex.map {
          case (cat, cati) ⇒
            cat.copy(
              instructed = cati == selected_cat,
              probs = cat.probs.zipWithIndex.map {
                case (prob, probi) ⇒
                  prob.copy(
                    instructed = cati == selected_cat && probi == selected_prob
                  )
              })
        }
      }

      val result = if (state.wait_duration == 0) {
        (set_wait(mane.get_random_wait()).apply _)
          .andThen { _.copy(probs = probs2) }
          .apply(state)
      }
      else
        set_wait(state.wait_duration - 1)(state)

      mane.draw(result)
      result
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

  final case class mod_prob(cati: Int, probi: Int, mod: Prob ⇒ Prob) extends Action {
    override def apply(state: State): State = {
      val cat = state.probs(cati)
      val prev = cat.probs(probi)
      state.copy(probs =
        state.probs.updated(
          cati,
          cat.copy(probs =
            cat.probs.updated(
              probi,
              mod(prev)
            )
          )
        )
      )
    }
  }
}
