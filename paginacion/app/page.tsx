"use client"

import { Button } from "@/components/ui/button"
import Link from "next/link"
import { ChevronRight } from "lucide-react"

export default function Home() {
  // Futuro: agregar estado o lógica de navegación para Blackjack educativo.

  return (
    <main className="min-h-screen bg-gradient-to-br from-background via-background to-muted flex flex-col items-center justify-center px-4 py-12">
      <div className="w-full max-w-3xl text-center space-y-12">
        {/* Header */}
        <div className="space-y-6">

          <h1 className="text-5xl md:text-6xl font-bold text-foreground text-balance">
            Simulador de Algoritmos y Blackjack educativo
          </h1>

          <p className="text-lg md:text-xl text-muted-foreground text-balance max-w-2xl mx-auto leading-relaxed">
            Visualiza y comprende cómo funcionan los algoritmos FIFO, LRU y Óptimo en la gestión de memoria.
          </p>
          <Link href="/simulator">
            <Button size="lg" className="text-base font-semibold px-8 gap-2 group cursor-pointer">
              Ir al Simulador
              <ChevronRight className="w-5 h-5 group-hover:translate-x-1 transition-transform" />
            </Button>
          </Link>
          <p className="mt-8 text-lg md:text-xl text-muted-foreground text-balance max-w-2xl mx-auto leading-relaxed">
            Aprende los fundamentos del Blackjack mientras practicas con nuestro juego interactivo.
          </p>
          <Button
            variant="outline"
            size="lg"
            className="text-base font-semibold px-8 bg-transparent hover:bg-green-400"
            onClick={() => {
              const url = "http://18.221.196.116:3000/"
              // Abrir en nueva pestaña para no perder el simulador local
              window.open(url, "_blank", "noopener,noreferrer")
            }}
          >
            Blackjack Educativo
          </Button>
        </div>

        {/* El bloque de input personalizado ha sido retirado según solicitud. */}
      </div>
    </main>
  )
}
