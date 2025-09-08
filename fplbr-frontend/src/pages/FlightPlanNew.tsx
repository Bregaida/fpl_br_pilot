import React from 'react'
import { FormField } from '../components/FormField'

export function FlightPlanNew() {
  const [submitting, setSubmitting] = React.useState(false)

  async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault()
    setSubmitting(true)
    const form = new FormData(e.currentTarget)
    const payload = Object.fromEntries(form.entries())
    console.log('[FPL] payload', payload)
    await new Promise(r => setTimeout(r, 600))
    alert('FPL preparado (payload no console). Substitua pelo POST real quando a API estiver pronta.')
    setSubmitting(false)
  }

  return (
    <section className="grid gap-6">
      <header className="space-y-1">
        <h1 className="text-3xl font-semibold tracking-tight">Novo Plano de Voo (FPL)</h1>
        <p className="text-slate-600">Preencha os campos essenciais. Este é um esqueleto para você estender (PVC/PVS).</p>
      </header>

      <form onSubmit={onSubmit} className="grid gap-6">
        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold">Campo 7 — Identificação</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField name="aircraftId" label="Identificação da Aeronave*" placeholder="ex: PT-ABC" required />
            <FormField name="callsign" label="Indicativo de chamada" placeholder="opcional" />
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold">Campo 8 — Regras e Tipo de Voo</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField name="flightRules" label="Regras de voo" placeholder="I / V / Y / Z" required />
            <FormField name="flightType" label="Tipo de voo*" placeholder="G / S / N / M / X" required />
            <FormField name="personsOnBoard" label="Pessoas a bordo*" type="number" min={1} placeholder="1" required />
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold">Campo 9 — Aeronave</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField name="aircraftNumber" label="Número" placeholder="ex: 1" />
            <FormField name="aircraftType" label="Tipo de Aeronave*" placeholder="ex: DECATHLON" required />
            <FormField name="wakeCat" label="Cat. Turbulência*" placeholder="L / M / H / J" required />
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold">Partida, Rota e Destino</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField name="depAd" label="Aeródromo de Partida*" placeholder="ex: SBSP" required />
            <FormField name="depTime" label="Hora (UTC)*" placeholder="ex: 1230" required />
            <FormField name="cruiseSpeed" label="Velocidade de Cruzeiro*" placeholder="ex: N0120" required />
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField name="level" label="Nível*" placeholder="ex: A050 ou F050" required />
            <FormField name="route" label="Rota*" placeholder="ex: DCT PONTO1 DCT" required />
            <FormField name="destAd" label="Aeródromo de Destino*" placeholder="ex: SBJD" required />
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField name="eetTotal" label="EET Total*" placeholder="ex: 0030" required />
            <FormField name="alt1" label="Aeródromo Alterno" placeholder="ex: SBMT" />
            <FormField name="alt2" label="2º Aeródromo Alterno" placeholder="opcional" />
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold">Campo 18 — Outras Informações</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField name="dof" label="DOF/ (Data do voo)" placeholder="ex: 250908" />
            <FormField name="rmk" label="RMK/ (Observações)" placeholder="texto livre" />
            <FormField name="pic" label="Piloto em comando" placeholder="Nome completo" />
          </div>
        </div>

        <div className="flex items-center gap-3">
          <button type="submit" disabled={submitting} className="px-5 py-2 rounded-2xl shadow bg-indigo-600 text-white disabled:opacity-60">
            {submitting ? 'Enviando…' : 'Preparar FPL'}
          </button>
          <span className="text-slate-500 text-sm">Este submit apenas imprime o payload no console.</span>
        </div>
      </form>
    </section>
  )
}
