package v

object Trace {

  var depth: Int = 0

  def apply[R](name: String)(f: ⇒ R): R = {
    //println(s"${". " * depth} $name")
    //depth += 1
    val result = f
    //depth -= 1
    //println(s"${". " * depth} /// $name")
    result
  }

}
