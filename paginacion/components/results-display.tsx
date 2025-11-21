"use client"

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"

interface ResultsDisplayProps {
  results: {
    fallos: number
    pasos: string
    tabla: number[][]
  }
}

export default function ResultsDisplay({ results }: ResultsDisplayProps) {
  return (
    <div className="space-y-6">
      <Card className="border border-border">
        <CardHeader>
          <CardTitle className="text-foreground">Resultados</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="bg-muted/30 rounded-md p-4 border border-border">
            <p className="text-sm text-muted-foreground">Número de Fallos</p>
            <p className="text-3xl font-bold text-foreground">{results.fallos}</p>
          </div>

          <div className="space-y-2">
            <p className="text-sm font-medium text-foreground">Pasos de Ejecución</p>
            <div className="bg-muted/20 rounded-md p-4 border border-border text-sm text-foreground whitespace-pre-wrap font-mono">
              {results.pasos}
            </div>
          </div>
        </CardContent>
      </Card>

      <Card className="border border-border">
        <CardHeader>
          <CardTitle className="text-foreground">Tabla de Marcos</CardTitle>
          <CardDescription>Estado de la memoria en cada paso</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="overflow-x-auto">
            <table className="w-full border-collapse text-sm">
              <tbody>
                {results.tabla.map((row, idx) => (
                  <tr key={idx} className="border-b border-border">
                    {row.map((cell, cidx) => (
                      <td
                        key={cidx}
                        className="border-r border-border px-4 py-2 text-center font-mono last:border-r-0 bg-muted/20 text-foreground"
                      >
                        {cell !== -1 ? cell : "-"}
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
