package v

import scala.language.implicitConversions
import org.scalajs.{dom ⇒ jsdom}
import ModDeco.{ModDecoration ⇒ ModDecoration}
import jsdom.{Window, document, html}

import scala.scalajs.js
import scala.scalajs.js.Dynamic

object dom {

  type Element = jsdom.Element

  def window: Window = jsdom.window
  def body: Element = document.body
  def dyn: Dynamic.type = scalajs.js.Dynamic
  val jsnew: Dynamic ⇒ Seq[js.Any] ⇒ js.Object with Dynamic = dyn.newInstance _

  def apply(id: String): Option[Element] = Option {
    jsdom.document.getElementById(id)
  }

  @js.native
  @js.annotation.JSGlobal("SpeechSynthesisUtterance")
  class SpeechSynthesisUtterance(text: String) extends js.Any

  @js.native
  @js.annotation.JSGlobal("speechSynthesis")
  object speechSynthesis extends js.Any {
    def speak(utterance: SpeechSynthesisUtterance): Unit = js.native
  }

  @js.native
  @js.annotation.JSGlobal("Date")
  object Date extends js.Any {
    def now(): Double = js.native
  }

  @js.native
  @js.annotation.JSGlobalScope
  object jsg extends js.Any {
    // type TimerHandler = string | Function;
    // declare function setTimeout(handler: TimerHandler, timeout?: number, ...arguments: any[]): number;
    def setTimeout(handler: js.Function0[Unit], timeout: Int): Int = js.native
  }

  trait Name[T] extends Any { def apply(): String }

  object Name {
    def apply[T: Name]: String = implicitly[Name[T]].apply()
    implicit def stringToName[T](name: String): Name[T] = () ⇒ name
    implicit val div: Name[html.Div] = "div"
    implicit val pre: Name[html.Pre] = "pre"
  }

  //noinspection SpellCheckingInspection
  def nomod[T]: T ⇒ Unit = _ ⇒ ()

  implicit class Creator[T <: dom.Element](val self: Unit) extends AnyVal {
    def apply(classes: Seq[String])(mod: T ⇒ Unit)(implicit name: Name[T]): T =
      document
        .createElement(name())
        .asInstanceOf[T]
        .mod(mod)
        .mod { el ⇒
          val classList = el.classList
          classes.foreach(classList.add)
        }

    def apply(_class: String)(mod: T ⇒ Unit = nomod)(implicit name: Name[T]): T = apply(Seq(_class))(mod)

    def apply()(mod: T ⇒ Unit)(implicit name: Name[T]): T = apply(Seq.empty)(mod)
  }

  def div: Creator[jsdom.html.Div] = ()
  def pre: Creator[jsdom.html.Pre] = ()
}


