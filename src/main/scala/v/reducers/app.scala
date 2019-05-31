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

      val mod = if (state.wait_duration == 0) {
        val wait = mane.get_random_wait()
        set_wait(wait)
      }
      else
        set_wait(state.wait_duration - 1)

      val result = mod(state)
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
}
