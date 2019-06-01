package v

object Rational {
  import Integral.Implicits.infixIntegralOps
  import Ordering.Implicits.infixOrderingOps

  def zero[T: Integral]: T = implicitly[Integral[T]].zero
  def one[T: Integral]: T = implicitly[Integral[T]].fromInt(1)

  @scala.annotation.tailrec
  def gcd[T: Integral](a: T, b: T): T =
    if (a < zero)
      gcd(-a , b)
    else if (b < zero)
      gcd(a, -b)
    else if (a.equiv(one) || b.equiv(one))
      one
    else if (b > a)
      gcd(b, a)
    else if (b equiv zero)
      a
    else
      gcd(b, a % b)

  def lcm[T: Integral](a: T, b: T): T =
    (a * b) / gcd(a, b)
}

final case class Rational[T: Integral] private (u: T, o: T) {
  import Integral.Implicits.infixIntegralOps

  def unary_- : Rational[T] =
    Rational(-u, o)

  def +(other: Rational[T]): Rational[T] = {
    val lcm = Rational.lcm(o, other.o)
    Rational(
      (lcm / o) * u + (lcm / other.o) * other.u,
      lcm
    )
  }

  def *(other: Rational[T]): Rational[T] =
    Rational(u * other.u, o * other.o)

  def norm: Rational[T] = {
    val g = Rational.gcd(u, o)
    Rational(u / g, o / g)
  }
}

