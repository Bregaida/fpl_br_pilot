import { Metadata } from "next"
import Link from "next/link"

export const metadata: Metadata = {
  title: "FPL-BR - Plano de Voo",
  description: "Gere e gerencie seus planos de voo",
}

export default function Home() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col items-center justify-center py-12 text-center">
        <h1 className="text-4xl font-bold tracking-tight sm:text-5xl">
          FPL-BR
        </h1>
        <p className="mt-3 max-w-prose text-lg text-muted-foreground">
          Sistema de gerenciamento de planos de voo
        </p>
      </div>
      
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <Link
          href="/fpl"
          className="rounded-lg border bg-card p-6 shadow-sm transition-colors hover:bg-accent/50"
        >
          <h2 className="text-xl font-semibold">Plano de Voo</h2>
          <p className="mt-2 text-sm text-muted-foreground">
            Crie e visualize planos de voo detalhados
          </p>
        </Link>
        
        <Link
          href="/meteorologia"
          className="rounded-lg border bg-card p-6 shadow-sm transition-colors hover:bg-accent/50"
        >
          <h2 className="text-xl font-semibold">Meteorologia</h2>
          <p className="mt-2 text-sm text-muted-foreground">
            Consulte condições meteorológicas atuais
          </p>
        </Link>
        
        <Link
          href="/aerodromos"
          className="rounded-lg border bg-card p-6 shadow-sm transition-colors hover:bg-accent/50"
        >
          <h2 className="text-xl font-semibold">Aeródromos</h2>
          <p className="mt-2 text-sm text-muted-foreground">
            Informações sobre aeródromos e cartas
          </p>
        </Link>
      </div>
    </div>
  )
}
