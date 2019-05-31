package v

object cats extends Map[String, Vector[String]] {

  private val store = Map(
    "illusion" -> Vector(
      "but what",
      "if the point",
      "of this life",
      "is just to be",
      "and experience all that is",
    ),
    "direction" -> Vector(
      "front",
      "back",
      "down",
      "up",
      "left",
      "right",
      "diagonal",
      "rotating",
      "reverse",
      "undefined",
      "chaotic",
      "zero",
      "give 'n' take",
      "contra",
    ),
    "duration" -> Vector(
      "forever",
      "slong",
      "long",
      "normal",
      "short",
      "very short",
      "dot",
      "zero",
    ),
    "level" -> Vector(
      "fly",
      "high",
      "standing",
      "medium",
      "low",
      "all fours",
      "flat",
    ),
    "polarity" -> Vector(
      "positive",
      "negative",
      "up",
      "down",
      "left",
      "right",
    ),
    "shape" -> Vector(
      "circular",
      "cross",
      "cubic",
      "curvy",
      "edgy",
      "linear",
      "spiral",
      "tilt",
      "twisted",
      "wave",
      "gesture",
      "point",
      "zero",
    ),
    "size" -> Vector(
      "xs",
      "s",
      "m",
      "l",
      "xl",
    ),
    "speed" -> Vector(
      "a.f.a.p",
      "cockain",
      "fast",
      "hurry",
      "normal",
      "chill",
      "slow",
      "stoned",
      "booto",
      "death",
    ),
    "texture" -> Vector(
      "thick",
      "sliding",
      "hitting",
      "minimal",
      "condenced",
      "stretched",
      "noisy",
    ),
  )

  override def +[V1 >: Vector[String]](kv: (String, V1)): Map[String, V1] =
    store + kv

  override def get(key: String): Option[Vector[String]] =
    store get key

  override def iterator: Iterator[(String, Vector[String])] =
    store.iterator

  override def -(key: String): Map[String, Vector[String]] =
    store - key
}
