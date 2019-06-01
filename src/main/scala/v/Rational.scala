package v

object Rational {
  import Integral.Implicits.infixIntegralOps
  import Ordering.Implicits.infixOrderingOps

  def zero[T: Integral]: Rational[T] = numeric[T].zero
  def one[T: Integral]: Rational[T] = numeric[T].one
  def int[T: Integral]: Integral[T] = implicitly

  @scala.annotation.tailrec
  def gcd[T: Integral](a: T, b: T): T = {
    val zero = int[T].zero
    val one = int[T].one

    if (a < zero)
      gcd(-a, b)
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
  }

  def lcm[T: Integral](a: T, b: T): T =
    (a * b) / gcd(a, b)

  final class RationalNumeric[T: Integral] extends scala.math.Numeric[Rational[T]] {
    private[this] def int: Integral[T] = implicitly

    override def plus(x: Rational[T], y: Rational[T]): Rational[T] = x + y

    override def minus(x: Rational[T], y: Rational[T]): Rational[T] = x + -y

    override def times(x: Rational[T], y: Rational[T]): Rational[T] = x * y

    override def negate(x: Rational[T]): Rational[T] = -x

    override def fromInt(x: Int): Rational[T] = Rational(int.fromInt(x), int.one)

    override def toInt(x: Rational[T]): Int = int.toInt(x.quot)

    override def toLong(x: Rational[T]): Long = int.toLong(x.quot)

    override def toFloat(x: Rational[T]): Float = x.toDouble.toFloat

    override def toDouble(x: Rational[T]): Double = x.toDouble

    override def compare(x: Rational[T], y: Rational[T]): Int = x cmp y
  }

  implicit def numeric[T: Integral]: RationalNumeric[T] = new RationalNumeric[T]()

  def apply[T: Integral](p: (T, T)): Rational[T] =
    new Rational[T](p._1, p._2)
}

final case class Rational[T: Integral] private (u: T, o: T) {
  import Integral.Implicits.infixIntegralOps
  private[this] val int: Integral[T] = implicitly
  import int.one

  override def toString: String =
    s"$u/$o"

  def norm: Rational[T] = {
    val g = Rational.gcd(u, o)
    Rational(u / g, o / g)
  }

  def lcm(other: Rational[T]): T =
    Rational.lcm(o, other.o)

  def lcmrat(other: Rational[T]): Rational[T] =
    Rational(one, lcm(other))


  def norm(other: Rational[T]): (Rational[T], Rational[T]) = {
    val lcm = this.lcm(other)
    (
      this modlcm lcm,
      other modlcm lcm,
    )
  }

  def cmp(other: Rational[T]): Int = {
    val (a, b) = norm(other)
    Ordering[T].compare(a.u, b.u)
  }

  def <(other: Rational[T]): Boolean =
    cmp(other) < 0

  def unary_- : Rational[T] =
    Rational(-u, o)

  def +(other: Rational[T]): Rational[T] = {
    val (a, b) = norm(other)
    Rational(a.u + b.u, a.o)
  }

  def *(other: Rational[T]): Rational[T] =
    Rational(u * other.u, o * other.o)

  def modlcm(lcm: T): Rational[T] = {
    val mod = lcm / o
    Rational(mod * u, mod * o)
  }

  def quot: T =
    u / o

  def toDouble: Double =
    u.toDouble() / o.toDouble()
}

