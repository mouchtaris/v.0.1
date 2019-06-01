package v

object ModDeco {

  final implicit class ModDecoration[T](val self: T) extends AnyVal {

    def mod(f: T ⇒ Unit): T = {
      f(self)
      self
    }

  }

}
