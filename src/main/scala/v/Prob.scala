package v

final case class Prob(
  over: Int,
  name: String,
  instructed: Boolean = false,
) {
  override def toString: String = s"1/$over"
}

object Prob {
  final case class Group(
    name: String,
    probs: Vector[Prob],
    instructed: Boolean = false,
  )
}
