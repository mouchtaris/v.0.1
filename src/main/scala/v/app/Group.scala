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

  def rand(rand: Int ⇒ Int): (Int, T) = {
    (0, items_.head)
  }
}
