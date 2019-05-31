package v
import scalajs.js

@js.annotation.JSExportAll
final case class State(
  wait_duration: Int,
  talking_shit: Boolean = false,
  probs: Map[String, Map[String, Prob]] = Map.empty,
)