export const cats = new Map([
  ['illusion', [
    'but what',
    'if the point',
    'of this life',
    'is just to be',
    'and experience all that is',
  ]],
  ["direction", [
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
  ]],
  ["duration", [
    "forever",
    "slong",
    "long",
    "normal",
    "short",
    "very short",
    "dot",
    "zero",
  ]],
  ["level",[
    "fly",
    "high",
    "standing",
    "medium",
    "low",
    "all fours",
    "flat",
  ]],
  ["polarity", [
    "positive",
    "negative",
    "up",
    "down",
    "left",
    "right",
  ]],
  ["shape", [
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
  ]],
  ["size", [
    "xs",
    "s",
    "m",
    "l",
    "xl",
  ]],
  ["speed", [
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
  ]],
  ["texture", [
    "thick",
    "sliding",
    "hitting",
    "minimal",
    "condenced",
    "stretched",
    "noisy",
  ]],
])

export default cats

const { stdout } = process
export function to_scala(cts: typeof cats)
{
  stdout.write('Map(\n')
  for (const [cat, vals] of cts) {
    stdout.write(`  "${cat}" -> Vector(\n`)
    for (const val of vals)
      stdout.write(`    "${val}",\n`)
    stdout.write('  ),\n')
  }
  stdout.write(')\n')
}