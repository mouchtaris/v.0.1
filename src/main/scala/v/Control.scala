package v

import scalajs.js
import js.JSConverters._

import reducers.State

@js.annotation.JSExportAll
final case class Control(mane: Main) {

  def states: js.Array[State] = mane.app.store.states.toJSArray

}


