package v

import scalajs.js
import scala.collection.mutable
import dom.SpeechSynthesisUtterance
import dom.speechSynthesis
import dom.Date
import dom.jsg

@js.annotation.JSExportAll
class Main extends AnyRef
  with Drawing
{
  // import js.{ Dynamic ⇒ jsdyn }
  // import jsdyn.{ global ⇒ dglobal, newInstance ⇒ jsnew }

  val Math = js.Math
  val random = Math.random _
  val floor = Math.floor _

  val display = dom("display").get
  val logstash_display = dom("logstash").get
  val sleeping_display = dom("countdown").get
  val sleeping_min_display = dom("sleep_min").get
  val sleeping_max_display = dom("sleep_max").get
  val probabilities_display = dom("probabilities").get
  val talking_shit_display = dom("talking_shit_display").get
  val selection_j_display = dom("j").get
  val selection_i_display = dom("i").get

  val app = new v.app.app(this)

  val logstash = mutable.Buffer.empty[LogEntry]

  def getRandomInt(max: Int): Int =
    floor(random() * floor(max)).toInt

  def say(text: String): Unit = {
    val utterance = new SpeechSynthesisUtterance(text)
    speechSynthesis.speak(utterance)
  }

  def log_entry(entry: LogEntry): Unit = {
    logstash.append(entry)
    logstash_display.appendChild(dom.pre()(_.innerText = entry.toString))
  }

  def update_sleeping(sec: Int): Unit = {
    sleeping_display.innerText = sec.toString
  }

  def get_random_category(): (Int, (String, Vector[String])) = {
    val i = getRandomInt(cats.size)
    val pair = cats.toIndexedSeq(i)
    (i, pair)
  }

  def schedule_shit(at: Int): Unit = {
    jsg.setTimeout(start_talking_shit, at)
  }

  val start_talking_shit: () ⇒ Unit = () ⇒ {
    //val (cati, (cat, vals)) = get_random_category()
    //val vali = getRandomInt(vals.size)
    //val value = vals(vali)
    //val wait = get_random_wait()
    //val ts = Date.now().toLong
    //val entry = LogEntry(ts, cat, value)
    //log_entry(entry)
    //say(s"$cat: $value")
    //update_sleeping(wait)
  }
}


object Main {
  def main(args: Array[String]): Unit = {
    dom.window.onload = _ ⇒
      try {
        val mane = new Main()
        Exports.ctrl_ = Control(mane)
        ;{
          import mane.app._
          import store.dispatch

          dispatch(init)
          dispatch(tick)
        }
      }
      catch {
        case ex: java.util.NoSuchElementException ⇒
          ex.printStackTrace(System.out)
      }
  }
}