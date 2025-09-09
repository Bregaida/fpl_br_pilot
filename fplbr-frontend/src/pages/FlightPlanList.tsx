import React from 'react'
import { useNavigate } from 'react-router-dom'
import { FlightplanAPI } from '../services/api'

type SubmissionRow = {
  id: number
  aerodromoDePartida?: string | null
  aerodromoDeDestino?: string | null
  modo?: string | null
  createdAt?: string
  tipoDeVooEnum?: 'G' | 'S' | 'N' | 'M' | 'X' | string | null
  regraDeVooEnum?: 'I' | 'V' | 'Y' | 'Z' | string | null
}

export function FlightPlanList() {
  const navigate = useNavigate()
  const [rows, setRows] = React.useState<SubmissionRow[]>([])
  const [loading, setLoading] = React.useState(true)

  React.useEffect(() => {
    ;(async () => {
      try {
        const res = await FlightplanAPI.listSubmissions({ limit: 200 })
        const list: SubmissionRow[] = res.data || []
        // Agrupar por Origem+Destino mantendo o mais recente (assumindo lista já ordenada por createdAt desc)
        const seen = new Set<string>()
        const grouped: SubmissionRow[] = []
        for (const item of list) {
          const key = `${item.aerodromoDePartida || ''}|${item.aerodromoDeDestino || ''}`
          if (!seen.has(key)) {
            seen.add(key)
            grouped.push(item)
          }
        }
        setRows(grouped)
      } finally {
        setLoading(false)
      }
    })()
  }, [])

  return (
    <section className="grid gap-6">
      <header className="space-y-1">
        <h1 className="text-3xl font-semibold tracking-tight">Meus Planos de Voo</h1>
        <p className="text-slate-600">Aqui você encontra todos os Planos de Voo cadastrados.</p>
      </header>

      <div className="overflow-x-auto rounded-2xl border border-slate-200 bg-white shadow-sm">
        <table className="min-w-full text-sm">
          <thead className="bg-slate-50 text-slate-700">
            <tr>
              <th className="text-left py-3 px-4">Partida</th>
              <th className="text-left py-3 px-4">Destino</th>
              <th className="text-left py-3 px-4">Regra de Voo</th>
              <th className="text-left py-3 px-4">Tipo do Plano de Voo</th>
              <th className="text-left py-3 px-4">Detalhes</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td className="py-4 px-4" colSpan={4}>Carregando...</td></tr>
            ) : rows.length === 0 ? (
              <tr><td className="py-4 px-4" colSpan={4}>Nenhum plano encontrado.</td></tr>
            ) : rows.map(r => (
              <tr key={r.id} className="border-t border-slate-200">
                <td className="py-3 px-4">{r.aerodromoDePartida || '-'}</td>
                <td className="py-3 px-4">{r.aerodromoDeDestino || '-'}</td>
                <td className="py-3 px-4">{mapRegraVoo(r.regraDeVooEnum)}</td>
                <td className="py-3 px-4">{r.modo || '-'}</td>
                <td className="py-3 px-4">
                  <button className="px-3 py-1 rounded-lg bg-indigo-600 text-white" onClick={() => navigate(`/flightplan/${r.id}`)}>Detalhes</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}

function mapRegraVoo(sigla?: string | null): string {
  const map: Record<string, string> = {
    I: 'I - IFR',
    V: 'V - VFR',
    Y: 'Y - VFR/IFR',
    Z: 'Z - IFR/VFR',
  }
  if (!sigla) return '-'
  return map[sigla] || sigla
}
