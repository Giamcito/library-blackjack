// Simple port selection script for Next.js production server.
// Tries ports from FRONTEND_PORTS (env) or default list and starts `next start` on the first free one.
// Exposes chosen port via stdout and sets PORT env var for downstream logging.
import { createServer } from 'node:http'
import { spawn } from 'node:child_process'

const candidates = (process.env.FRONTEND_PORTS || '3000,3001,3002')
  .split(',')
  .map(p => Number(p.trim()))
  .filter(n => Number.isInteger(n) && n > 0)

if (!candidates.length) {
  console.error('No hay puertos candidatos válidos. Define FRONTEND_PORTS.')
  process.exit(1)
}

function checkPort(port) {
  return new Promise(resolve => {
    const server = createServer()
    server.once('error', err => {
      server.close(() => resolve(false))
    })
    server.listen(port, '0.0.0.0', () => {
      server.close(() => resolve(true))
    })
  })
}

async function choosePort() {
  for (const p of candidates) {
    // eslint-disable-next-line no-await-in-loop
    const free = await checkPort(p)
    if (free) return p
  }
  return null
}

async function main() {
  const port = await choosePort()
  if (port == null) {
    console.error('Ningún puerto disponible de la lista:', candidates.join(','))
    process.exit(2)
  }
  process.env.PORT = String(port)
  console.log(`Iniciando Next.js en puerto libre ${port}`)
  const child = spawn('node', ['node_modules/next/dist/bin/next', 'start', '-p', String(port)], {
    stdio: 'inherit',
  })
  child.on('exit', code => process.exit(code ?? 0))
}

main().catch(err => {
  console.error('Fallo al iniciar con fallback de puertos:', err)
  process.exit(3)
})
