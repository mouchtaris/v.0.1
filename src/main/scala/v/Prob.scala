package v

final case class Prob(
  over: Int,
  instructed: Boolean = false,
) {
  override def toString: String = s"1/$over"
}

