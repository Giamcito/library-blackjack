import { type NextRequest, NextResponse } from "next/server"
import { fifo } from "@/lib/algorithms"
import { callRemote } from "@/lib/remote"

const USE_LOCAL = process.env.LOCAL_ALGOS === "true"
// Backend está arrancando en el puerto 8081 según logs, ajustar default
const MICROSERVICE_URL = process.env.MICROSERVICE_URL || "http://algoritmos-service:8081/api/algoritmos/pagereplacement"

function mapRemote(data: any, referencias: string, marcos: number) {
  // Intentamos detectar nombres flexibles en la respuesta remota
  const tabla = data?.tabla || data?.table || data?.pageTable || data?.frames || data?.matrix || []
  let pasos: string[] | undefined = data?.pasos || data?.steps
  // Si la respuesta remota incluye eventos detallados, intentar normalizar a strings
  if (pasos && pasos.length && typeof pasos[0] === "object") {
    pasos = pasos.map((p: any) => p?.evento || p?.event || p?.estado || JSON.stringify(p))
  }
  // Generar pasos si no vienen y tenemos tabla y referencias
  if (!pasos && Array.isArray(tabla) && tabla.length) {
    const refsArray = referencias.replace(/\s+/g, "").split("")
    pasos = tabla.map((fila: any, idx: number) => {
      const valor = refsArray[idx]
      const hit = Array.isArray(fila) ? fila.includes(Number(valor)) || fila.includes(valor) : false
      return hit ? `HIT ${valor}` : `FALLO ${valor}`
    })
  }
  let fallos = data?.fallos ?? data?.faults ?? data?.misses ?? data?.pageFaults
  if (fallos == null && pasos) {
    fallos = pasos.filter(p => p.startsWith("FALLO")).length
  }
  return { referencias, marcos, fallos, tabla, pasos }
}

export async function POST(request: NextRequest) {
  try {
    const { referencias, marcos } = await request.json()
    if (!referencias || typeof referencias !== "string") {
      return NextResponse.json({ error: "'referencias' inválidas" }, { status: 400 })
    }
    const clean = referencias.replace(/\s+/g, "")
    if (!/^\d+$/.test(clean)) {
      return NextResponse.json({ error: "Las referencias deben ser sólo dígitos" }, { status: 400 })
    }
    if (!marcos || typeof marcos !== "number" || marcos < 1) {
      return NextResponse.json({ error: "'marcos' debe ser >= 1" }, { status: 400 })
    }
    if (USE_LOCAL) {
      const data = fifo(referencias, marcos)
      return NextResponse.json(data)
    }
    const remote = await callRemote("fifo", referencias, marcos)
    return NextResponse.json(remote)
  } catch (error) {
    console.error("Error FIFO:", error)
    return NextResponse.json({ error: "Error procesando la solicitud" }, { status: 500 })
  }
}
