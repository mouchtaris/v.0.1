package v
import scalajs.js

import app.State
import app.Action

@js.annotation.JSExportTopLevel("v")
@js.annotation.JSExportAll
object Exports {
  private[v] var ctrl_ : Control = _
  def ctrl: Control = ctrl_

  def ss = ctrl.states
  def app = ctrl_.mane.app
  def dispatch(action: Action) = {
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
  def overs: Unit = println(
    ss.map(_.overrides).mkString("\n")
  )

  def set(cati: Int, probi: Int, probu: Int, probo: Int): Unit =
    dispatch(app.override_prob(cati, probi, (probu, probo)))

  def unset(cati: Int, probi: Int): Unit =
    dispatch(app.deoverride_prob(cati, probi))

  def setg(cati: Int, probu: Int, probo: Int): Unit =
    dispatch(app.override_group_prob(cati, (probu, probo)))

  def unsetg(cati: Int): Unit =
    dispatch(app.deoverride_group_prob(cati))

  def say(phrase: String): Unit =
    dispatch(app.say(phrase))

  def sleep_min(value: Int): Unit =
    dispatch(app.set_min_sleep(value))

  def sleep_span(value: Int): Unit =
    dispatch(app.set_sleep_span(value))
}
