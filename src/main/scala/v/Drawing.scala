package v

trait Drawing {
  this: Main ⇒

  def draw_categories(): Unit = {
    the_state.probs.zipWithIndex.foreach {
      case ((cat, vals), cati) ⇒
        probabilities_display.appendChild {
          dom.div("category") { cont ⇒
            cont appendChild dom.div("name")(_.innerText = s"$cati:$cat")
            cont appendChild dom.div("values") { cont ⇒
              vals.zipWithIndex.foreach {
                case ((val_, prob), vali) ⇒
                  val value_elem = dom.div("value")(_.innerText = s"[$vali] $val_ $prob")
                  if (prob.instructed)
                    value_elem.classList.add("instructed")
                  else
                    value_elem.classList.remove("instructed")
                  cont.appendChild(value_elem)
              }
            }
          }
        }
    }
  }

  def draw_talking_shit_display(): Unit = {
    val on = "on"
    val off = "off"
    val (add, remove) =
      if (the_state.talking_shit) (on, off)
      else (off, on)

    import talking_shit_display.classList
    classList.remove(remove)
    classList.add(add)
  }

  def draw(state: State): Unit = {
    display.innerText = s"Hello, V.0.1. Fuck you. Today's lucky number is: ${getRandomInt(10)}"
    sleeping_display.innerText = the_state.wait_duration.toString
    draw_categories()
    draw_talking_shit_display()
  }
}

