package v

object Drawing {
  final val INSTRUCTED = "instructed"
  final val OVERRIDEN = "overriden"
  final val PROBABILITY = "probability"
}

trait Drawing {
  this: Main ⇒
  import Drawing.INSTRUCTED
  import Drawing.OVERRIDEN
  import Drawing.PROBABILITY
  import v.app.State

  def draw_categories(state: State): Unit = {
    while (probabilities_display.firstChild != null)
      probabilities_display.removeChild(probabilities_display.firstChild)

    state.cats.foreach {
      case (cat, cati) ⇒
        probabilities_display.appendChild {
          dom.div("category") { cont ⇒
            val name = dom.div("name") { name ⇒
              name.innerText = s"$cati :: ${cat.name}"
              name appendChild dom.span(PROBABILITY)(_.innerText = s"${cat.prob}")
              if (cat.overriden)
                name.classList.add(OVERRIDEN)
            }
            cont appendChild name

            cont appendChild dom.div("values") { cont ⇒
              cat.foreach {
                case (prob, probi) ⇒
                  val value_elem = dom.div("value") { value_elem ⇒
                    value_elem.innerText = s"$probi :: ${prob.name}"
                    value_elem appendChild dom.span(PROBABILITY)(_.innerText = s"${prob.prob}")
                  }
                  if (prob.instructed) {
                    name.classList.add(INSTRUCTED)
                    value_elem.classList.add(INSTRUCTED)
                    if (state.wait_duration == 0)
                      say(s"${cat.name}: ${prob.name}")
                  }
                  if (prob.overriden) {
                    value_elem.classList.add(OVERRIDEN)
                  }
                  cont.appendChild(value_elem)
              }
            }
          }
        }
    }
  }

  def draw_talking_shit_display(state: State): Unit = {
    val on = "on"
    val off = "off"
    val (add, remove) =
      if (state.talking_shit) (on, off)
      else (off, on)

    import talking_shit_display.classList
    classList.remove(remove)
    classList.add(add)
  }

  def draw_selections(state: State): Unit = {
    selection_i_display.innerText = state.selected_i.toString
    selection_j_display.innerText = state.selected_j.toString
  }

  def draw_sleeping_display(state: State): Unit = {
    sleeping_display.innerText = state.wait_duration.toString
    sleeping_min_display.innerText = state.min_wait.toString
    sleeping_max_display.innerText = (state.min_wait + state.wait_span).toString
  }

  def draw(state: State): Unit = {
    display.innerText = s"Hello, V.0.1. Fuck you. Today's lucky number is: ${getRandomInt(10)}"
    draw_sleeping_display(state)
    draw_categories(state)
    draw_talking_shit_display(state)
    draw_selections(state)
  }
}

