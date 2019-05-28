import cats from './cats/index.js'
const synth = window.speechSynthesis;
const text = "Hello, V.0.1. Fuck you. If you are wondering what the hell, is going on, check this shit out."

class Option<T>
{
  constructor(public value?: T | null){}
  map<U>(f: (obj: T) => U): Option<U>
  {
    if (this.value)
      return new Option(f(this.value))
    return new Option()
  }
}

function get_display() {
  return new Option(document.getElementById("display"))
}

function debug(text: string) {
  const child = document.createElement("pre")
  child.innerText = text
  get_display().map(disp => disp.appendChild(child))
}

function say(text: string) {
  debug(text)
  const utter = new SpeechSynthesisUtterance(text)
  synth.speak(utter)
}

window.resizeTo(800, 600)
window.onload = () => {
  say(text)
}

function getRandomInt(max: number) {
  return Math.floor(Math.random() * Math.floor(max));
}

export function start_talking_shit() {
  const cati = getRandomInt(cats.size)
  const cat = Array.from(cats.keys())[cati]
  const vals = cats.get(cat)
  if (vals === undefined) {
    debug(`No such cat, nigga: ${cat}`)
    return
  }

  const vali = getRandomInt(vals.length)
  const val = vals[vali]
  const wait = (5 + getRandomInt(10))

  say(`${cat}: ${val}`)
  debug(`sleeping ${wait} seconds...`)

  setTimeout(start_talking_shit, wait * 1000)
}