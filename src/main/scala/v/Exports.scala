package v
import scalajs.js

@js.annotation.JSExportTopLevel("v")
@js.annotation.JSExportAll
object Exports {
  private[v] var ctrl_ : Control = _
  def ctrl: Control = ctrl_

  def ss = ctrl.states
  def app = ctrl_.mane.app
  def dispatch(action: reducers.Action) = {
    Trace("kai omos dispatching") {
      app.store.dispatch(action)
    }
  }
  def init = dispatch(app.init)
  def tick = dispatch(app.tick)
  def talk(v: Boolean) = dispatch(app.set_talking_shit(v))
  def wait(v: Int) = dispatch(app.set_wait(v))

  def state: String = ss.last.toString

  def props[T](f: State ⇒ T): Unit =
    println {
      ss.map(f).map(_.toString).mkString(",")
    }
  def talks: Unit = props(_.talking_shit)
  def waits: Unit = props(_.wait_duration)
}