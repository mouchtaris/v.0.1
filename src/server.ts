import fs from 'fs'
import util from 'util'

import koa from 'koa'

const readFile = util.promisify(fs.readFile)
const app = new koa()

app.use(async (ctx) => {
  const content = await readFile('jndex.html')
  ctx.response.set('Access-Control-Allow-Origin', '*')
  ctx.response.set('Content-type', 'text/html')
  ctx.response.set('Charset-encoding', 'utf-8')

  ctx.response.body = content
})

app.listen(9000)