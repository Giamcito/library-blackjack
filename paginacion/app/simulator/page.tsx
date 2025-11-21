"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import AlgorithmForm from "@/components/algorithm-form"
import ResultsDisplay from "@/components/results-display"
import Link from "next/link"
import { Button } from "@/components/ui/button"
import { ChevronLeft } from "lucide-react"

export default function Simulator() {
  const [results, setResults] = useState<any>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (data: {
    referencias: string
    marcos: number
    algoritmo: "fifo" | "lru" | "optimo"
  }) => {
    setLoading(true)
    setError(null)
    setResults(null)

    try {
      const response = await fetch(`/api/${data.algoritmo}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          referencias: data.referencias,
          marcos: data.marcos,
        }),
      })

      if (!response.ok) throw new Error("Error en la solicitud")

      const result = await response.json()
      setResults(result)
    } catch (err) {
      setError(err instanceof Error ? err.message : "Error desconocido")
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="min-h-screen bg-background p-4">
      <div className="w-full max-w-4xl mx-auto">
        {/* Back Button */}
        <Link href="/">
          <Button variant="ghost" className="mb-8 gap-2 cursor-pointer">
            <ChevronLeft className="w-4 h-4" />
            Volver a inicio
          </Button>
        </Link>

        <div className="mb-12 text-center">
          <h1 className="text-4xl font-bold text-foreground mb-2">Simulador de Algoritmos</h1>
          <p className="text-muted-foreground text-lg">Simula algoritmos de reemplazo de páginas en memoria</p>
        </div>

        <div className="grid md:grid-cols-2 gap-8">
          <Card className="border border-border">
            <CardHeader>
              <CardTitle className="text-foreground">Entrada</CardTitle>
              <CardDescription>Configura los parámetros de simulación</CardDescription>
            </CardHeader>
            <CardContent>
              <AlgorithmForm onSubmit={handleSubmit} loading={loading} />
            </CardContent>
          </Card>

          <div>
            {error && (
              <Card className="border border-destructive bg-destructive/5">
                <CardContent className="pt-6">
                  <p className="text-destructive text-sm">{error}</p>
                </CardContent>
              </Card>
            )}

            {results && <ResultsDisplay results={results} />}

            {!results && !error && (
              <Card className="border border-border bg-muted/30 h-full flex items-center justify-center min-h-96">
                <CardContent className="text-center">
                  <p className="text-muted-foreground">Los resultados aparecerán aquí</p>
                </CardContent>
              </Card>
            )}
          </div>
        </div>
      </div>
    </main>
  )
}
