"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

interface AlgorithmFormProps {
  onSubmit: (data: {
    referencias: string
    marcos: number
    algoritmo: "fifo" | "lru" | "optimo"
  }) => void
  loading: boolean
}

export default function AlgorithmForm({ onSubmit, loading }: AlgorithmFormProps) {
  const [referencias, setReferencias] = useState("")
  const [marcos, setMarcos] = useState("3")
  const [algoritmo, setAlgoritmo] = useState<"fifo" | "lru" | "optimo">("fifo")

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()

    const cleanRefs = referencias.replace(/\s+/g, "")
    if (!cleanRefs) {
      alert("Por favor ingresa la cadena de referencias")
      return
    }
    if (!/^\d+$/.test(cleanRefs)) {
      alert("La cadena de referencias solo puede contener dígitos (0-9)")
      return
    }
    if (isNaN(Number(marcos)) || Number(marcos) < 1) {
      alert("El número de marcos debe ser mayor a 0")
      return
    }

    onSubmit({
      referencias: referencias.replace(/\s+/g, ""),
      marcos: Number(marcos),
      algoritmo,
    })
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      <div className="space-y-2">
        <label htmlFor="referencias" className="block text-sm font-medium text-foreground">
          Cadena de Referencias
        </label>
        <textarea
          id="referencias"
          value={referencias}
          onChange={(e) => setReferencias(e.target.value)}
          placeholder="'7012030423032123' o '7 0 1 2 0 3 0 4'"
          className="w-full px-3 py-2 border border-input rounded-md bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring disabled:opacity-50"
          rows={4}
          disabled={loading}
        />
  <p className="text-xs text-muted-foreground">Sólo dígitos (0-9). Puedes separar con espacios.</p>
      </div>

      <div className="space-y-2">
        <label htmlFor="marcos" className="block text-sm font-medium text-foreground">
          Número de Marcos
        </label>
        <input
          id="marcos"
          type="number"
          min="1"
          value={marcos}
          onChange={(e) => setMarcos(e.target.value)}
          className="w-full px-3 py-2 border border-input rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-ring disabled:opacity-50"
          disabled={loading}
        />
      </div>

      <div className="space-y-2">
        <label htmlFor="algoritmo" className="block text-sm font-medium text-foreground">
          Algoritmo
        </label>
        <Select value={algoritmo} onValueChange={(value: any) => setAlgoritmo(value)} disabled={loading}>
          <SelectTrigger id="algoritmo" className="border-input">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="fifo">FIFO (First In, First Out)</SelectItem>
            <SelectItem value="lru">LRU (Least Recently Used)</SelectItem>
            <SelectItem value="optimo">Óptimo</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <Button
        type="submit"
        disabled={loading}
        className="w-full bg-primary text-primary-foreground hover:bg-primary/90"
      >
        {loading ? "Simulando..." : "Simular"}
      </Button>
    </form>
  )
}
