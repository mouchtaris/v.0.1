package v
package app

final case class Prob(
  name: String,
  override val prob: Rational[Int],
  instructed: Boolean = false,
  overriden: Boolean = false,
)
  extends AnyRef
  with Probable
