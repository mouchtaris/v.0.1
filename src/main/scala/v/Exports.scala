package v
import scalajs.js

import reducers.State

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
  def toggle() = {
    dispatch(app.toggle_talking)
    dispatch(app.tick_if_talking)
  }

  def state: String = ss.last.toString

  def props[T](f: State ⇒ T): Unit =
    println {
      ss.map(f).map(_.toString).mkString(",")
    }
  def talks: Unit = props(_.talking_shit)
  def waits: Unit = props(_.wait_duration)
  def instructs: Unit = println( ss.last.cats.map(_.probs.count(_.instructed)).sum )
  def overs: Unit = println(
    ss.map(_.overrides).mkString("\n")
  )

  def set(cati: Int, probi: Int, prob: js.Tuple2[Int, Int]): Unit =
    dispatch(app.override_prob(cati, probi, prob))
}
