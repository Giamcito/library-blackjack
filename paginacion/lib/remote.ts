/**
 * Remote client helper to call page replacement algorithms on the microservice
 * with dynamic port fallback. Tries given MICROSERVICE_URL first; if connection
 * fails (network error or non-2xx), it will attempt alternative common ports
 * (8081, 8080, 8082) by replacing only the port portion, preserving the path.
 */
import { fifo, lru, optimo } from "@/lib/algorithms"

const DEFAULT_BASE = process.env.MICROSERVICE_URL || "http://algoritmos-service:8081/api/algoritmos/pagereplacement"

// Ports to attempt if the initial one falla.
const CANDIDATE_PORTS = [8081, 8080, 8082]

function buildCandidates(base: string): string[] {
  try {
    const url = new URL(base)
    const originalPort = url.port ? Number(url.port) : 80
    const uniquePorts = Array.from(new Set([originalPort, ...CANDIDATE_PORTS]))
    return uniquePorts.map(p => {
      url.port = String(p)
      return url.toString().replace(/\/$/, "")
    })
  } catch {
    // If parsing fails, just return base and appended defaults
    return [base, ...CANDIDATE_PORTS.map(p => base.replace(/:\d+/, ":" + p))]
  }
}

async function fetchWithTimeout(url: string, body: any, ms = 1500): Promise<Response> {
  const controller = new AbortController()
  const t = setTimeout(() => controller.abort(), ms)
  try {
    const resp = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
      signal: controller.signal,
    })
    return resp
  } finally {
    clearTimeout(t)
  }
}

export async function callRemote(algo: "fifo" | "lru" | "optimo", referencias: string, marcos: number) {
  const clean = referencias.replace(/\s+/g, "")
  const bases = buildCandidates(DEFAULT_BASE)
  let lastError: any
  for (const base of bases) {
    try {
      const resp = await fetchWithTimeout(`${base}/${algo}`, { referencias: clean, marcos })
      if (!resp.ok) {
        lastError = new Error(`Status ${resp.status} en ${base}`)
        continue
      }
      const data = await resp.json()
      return mapRemote(data, clean, marcos)
    } catch (e) {
      lastError = e
      continue
    }
  }
  // Fallback local if todas las opciones remotas fallan
  console.warn(`Remote ${algo} falló en todos los puertos. Usando cálculo local. Último error:`, lastError)
  switch (algo) {
    case "fifo": return fifo(referencias, marcos)
    case "lru": return lru(referencias, marcos)
    case "optimo": return optimo(referencias, marcos)
  }
}

// Mapping util reutilizado de las rutas
export function mapRemote(data: any, referencias: string, marcos: number) {
  const tabla = data?.tabla || data?.table || data?.pageTable || data?.frames || data?.matrix || []
  let pasos: string[] | undefined = data?.pasos || data?.steps
  if (pasos && pasos.length && typeof pasos[0] === "object") {
    pasos = pasos.map((p: any) => p?.evento || p?.event || p?.estado || JSON.stringify(p))
  }
  if (!pasos && Array.isArray(tabla) && tabla.length) {
    const refsArray = referencias.replace(/\s+/g, "").split("")
    pasos = tabla.map((fila: any, idx: number) => {
      const valor = refsArray[idx]
      const hit = Array.isArray(fila) ? fila.includes(Number(valor)) || fila.includes(valor) : false
      return hit ? `HIT ${valor}` : `FALLO ${valor}`
    })
  }
  let fallos = data?.fallos ?? data?.faults ?? data?.misses ?? data?.pageFaults
  if (fallos == null && pasos) fallos = pasos.filter(p => p.startsWith("FALLO")).length
  return { referencias, marcos, fallos, tabla, pasos }
}
