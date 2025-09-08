import React from 'react'

type FakeFPL = {
  id: string
  depAd: string
  destAd: string
  depTime: string
  status: 'rascunho' | 'enviado' | 'rejeitado'
}

const MOCK: FakeFPL[] = [
  { id: 'FPL-0001', depAd: 'SBSP', destAd: 'SBJD', depTime: '1230', status: 'rascunho' },
  { id: 'FPL-0002', depAd: 'SBMT', destAd: 'SBJD', depTime: '1400', status: 'enviado' },
]

export function FlightPlanList() {
  const [rows] = React.useState<FakeFPL[]>(MOCK)
  return (
    <section className="grid gap-6">
      <header className="space-y-1">
        <h1 className="text-3xl font-semibold tracking-tight">Meus Planos de Voo</h1>
        <p className="text-slate-600">Placeholder de listagem para integrar com a API.</p>
      </header>

      <div className="overflow-x-auto rounded-2xl border border-slate-200 bg-white shadow-sm">
        <table className="min-w-full text-sm">
          <thead className="bg-slate-50 text-slate-700">
            <tr>
              <th className="text-left py-3 px-4">ID</th>
              <th className="text-left py-3 px-4">Partida</th>
              <th className="text-left py-3 px-4">Destino</th>
              <th className="text-left py-3 px-4">Hora (UTC)</th>
              <th className="text-left py-3 px-4">Status</th>
            </tr>
          </thead>
          <tbody>
            {rows.map(r => (
              <tr key={r.id} className="border-t border-slate-200">
                <td className="py-3 px-4 font-medium">{r.id}</td>
                <td className="py-3 px-4">{r.depAd}</td>
                <td className="py-3 px-4">{r.destAd}</td>
                <td className="py-3 px-4">{r.depTime}</td>
                <td className="py-3 px-4">
                  <span className={`px-2 py-1 rounded-xl text-xs ${
                    r.status === 'enviado' ? 'bg-green-100 text-green-700' :
                    r.status === 'rejeitado' ? 'bg-red-100 text-red-700' :
                    'bg-slate-100 text-slate-700'
                  }`}>{r.status}</span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}
