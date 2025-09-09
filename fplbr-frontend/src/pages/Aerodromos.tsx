import React from 'react'
import AerodromePanel from '../components/AerodromePanel'
import MeteoPanel from '../components/MeteoPanel'
import NotamPanel from '../components/NotamPanel'
import { api, AerodromosAPI } from '../services/api'

type AerodromoDTO = {
  icao: string
  nomeOficial?: string
  municipio?: string
  uf?: string
  cartas?: Array<{ tipo: 'VAC' | 'IAC' | 'SID' | 'STAR' | 'ROTA'; titulo: string; versao?: string; pdf: string }>
  pistas?: Array<{ designacao: string; comprimentoMetros?: number; larguraMetros?: number; superficie?: string; ils?: boolean; categoriaIls?: string; papi?: string }>
}

export default function AerodromosPage() {
  const [query, setQuery] = React.useState('')
  const [selecionado, setSelecionado] = React.useState<string>('')
  const [detalhe, setDetalhe] = React.useState<AerodromoDTO | null>(null)
  const [meteo, setMeteo] = React.useState<any>({})
  const [notams, setNotams] = React.useState<any>({})
  const [loading, setLoading] = React.useState(false)

  async function buscar() {
    if (!query.trim()) return
    setLoading(true)
    try {
      const q = query.trim().toUpperCase()
      const res = await api.get(`/api/v1/aerodromos/${q}`)
      setSelecionado(q)
      setDetalhe(res.data)
      // Carregar MET e NOTAMs
      try { setMeteo({ [q]: (await api.get('/api/briefing', { params: { icao: q }})).data }) } catch {}
      try { setNotams({ [q]: (await api.get('/api/notams', { params: { icao: q }})).data }) } catch {}
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="grid gap-6 p-4">
      <header className="space-y-1">
        <h1 className="text-3xl font-semibold tracking-tight">Aeródromos</h1>
        <p className="text-slate-600">Consulte ROTAER: pistas, cartas e NOTAMs.</p>
      </header>

      <div className="flex gap-3">
        <input className="input flex-1" placeholder="Digite o ICAO (ex.: SBSP)" value={query} onChange={e=>setQuery(e.target.value)} onKeyDown={e=>{ if(e.key==='Enter') buscar() }} />
        <button className="px-4 py-2 rounded-xl bg-indigo-600 text-white" onClick={buscar}>Buscar</button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="space-y-6">
          <AerodromePanel aerodrome={detalhe ? {
            icao: detalhe.icao,
            nome: detalhe.nomeOficial,
            cartas: detalhe.cartas || [],
            coord: undefined,
            elev: detalhe?.pistas && detalhe.pistas.length ? Math.round((detalhe.pistas[0].comprimentoMetros||0)/0.3048) : undefined,
            sun: undefined
          } : null} title="Cartas e Info" isLoading={loading} />

          <div className="bg-white rounded-lg shadow p-4">
            <h3 className="text-lg font-semibold mb-3">Pistas</h3>
            {loading ? 'Carregando...' : (
              <div className="divide-y">
                {(detalhe?.pistas||[]).map((p, idx) => (
                  <div key={idx} className="py-2 grid grid-cols-2 md:grid-cols-4 gap-3 text-sm">
                    <div><span className="text-slate-500">Designação</span><div className="font-medium">{p.designacao}</div></div>
                    <div><span className="text-slate-500">Dimensões</span><div className="font-medium">{p.comprimentoMetros||'-'} x {p.larguraMetros||'-'} m</div></div>
                    <div><span className="text-slate-500">Superfície</span><div className="font-medium">{p.superficie||'-'}</div></div>
                    <div><span className="text-slate-500">ILS/PAPI</span><div className="font-medium">{p.ils ? (p.categoriaIls||'ILS') : '—'} {p.papi ? `• PAPI ${p.papi}` : ''}</div></div>
                  </div>
                ))}
                {(!detalhe?.pistas || detalhe.pistas.length===0) && (
                  <div className="text-slate-500 text-sm">Nenhuma pista encontrada.</div>
                )}
              </div>
            )}
          </div>
        </div>

        <div className="space-y-6">
          <MeteoPanel meteoData={meteo} aerodromes={selecionado ? [{ icao: selecionado }] : []} isLoading={loading} />
          <NotamPanel notams={notams} aerodromes={selecionado ? [{ icao: selecionado }] : []} isLoading={loading} />
        </div>
      </div>
    </section>
  )
}


