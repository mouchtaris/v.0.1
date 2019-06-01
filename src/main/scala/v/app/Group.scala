package v
package app

final case class Group[T <: Probable](
  name: String,
  override val prob: Rational[Int],
  items_ : Vector[T] = Vector.empty,
  overriden: Boolean = false,
)
  extends AnyRef
  with Probable
{
  def get(i: Int): Option[T] =
    items_.lift(i)

  def size: Int =
    items_.size

  def updated(i: Int, obj: T): Group[T] =
    copy(items_ = items_.updated(i, obj))

  def updated(items: Vector[T]): Group[T] =
    copy(items_ = items)

  def foreach(f: ((T, Int)) ⇒ Unit): Unit =
    items_.zipWithIndex foreach f

  def map[U](f: T ⇒ U): Vector[U] =
    items_ map f

  def lcm: Int =
    items_
      .foldLeft(Rational.one[Int]) { (lcmr, item) ⇒ lcmr.lcmrat(item.prob) }
      .o

  def rand(rand: Int ⇒ Int): (Int, T) = {
    val lcm = this.lcm
    val r = rand(lcm)
    items_
      .zipWithIndex
      .foldLeft(0, None: Option[(Int, T)]) {
        case ((acc_prob, result), (item, index)) ⇒
          if (result.isDefined)
            (acc_prob, result)
          else {
            val acc_prob2 = acc_prob + item.prob.modlcm(lcm).u
            if (r < acc_prob2)
              (0, Some((index, item)))
            else
              (acc_prob2, None)
          }
      }
      ._2
      .get
  }
}
