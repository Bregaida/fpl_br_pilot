import React from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { FlightplanAPI } from '../services/api'

export default function PlanoDeVooView() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = React.useState(true)
  const [erro, setErro] = React.useState<string>('')
  const [data, setData] = React.useState<any>(null)

  React.useEffect(() => {
    let mounted = true
    ;(async () => {
      try {
        const res = await FlightplanAPI.getSubmissionById(id as string)
        if (!mounted) return
        setData(res.data)
      } catch (e: any) {
        setErro(e?.response?.data?.message || 'Não foi possível carregar o FPL')
      } finally {
        setLoading(false)
      }
    })()
    return () => { mounted = false }
  }, [id])

  if (loading) return <div className="p-6">Carregando...</div>
  if (erro) return (
    <div className="p-6">
      <p className="text-red-600 mb-4">{erro}</p>
      <button className="px-4 py-2 rounded-xl bg-indigo-600 text-white" onClick={() => navigate('/flightplan/novo')}>Novo FPL</button>
    </div>
  )
  const payload = data?.payload || {}

  const Row = ({label, value}:{label:string, value:any}) => (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-2 py-1">
      <div className="text-slate-500">{label}</div>
      <div className="md:col-span-2 font-medium break-words">{String(value ?? '')}</div>
    </div>
  )

  function toHHmmFromIsoZ(iso?: string): string {
    try {
      if (!iso) return ''
      const d = new Date(iso)
      const hh = String(d.getUTCHours()).padStart(2,'0')
      const mm = String(d.getUTCMinutes()).padStart(2,'0')
      return `${hh}${mm}`
    } catch {
      return ''
    }
  }

  return (
    <section className="grid gap-6 p-4">
      <header className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Plano de Voo</h1>
          <p className="text-slate-600">Visualização</p>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/flightplan/novo" className="px-4 py-2 rounded-xl bg-indigo-600 text-white">Novo</Link>
          <Link to="/flightplan/listar" className="px-4 py-2 rounded-xl bg-slate-200">Listar</Link>
        </div>
      </header>
      <div className="p-3 rounded border border-emerald-300 bg-emerald-50 text-emerald-900">
        Coloque exatamente essas informações no seu FPL BR e submeta, bom voo comandante 🧑‍✈️✈️
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Modo</h2>
        <Row label="Tipo do Plano" value={payload.modo} />
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Campo 7 - Identificação da Aeronave</h2>
        <Row label="Identificação da Aeronave" value={payload.identificacaoDaAeronave} />
        <Row label="Indicativo de Chamada" value={payload.indicativoDeChamada ? 'Sim' : 'Não'} />
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Campo 8 - Regras de Voo e Tipo de Voo</h2>
        <Row label="Regra de Voo" value={payload.regraDeVooEnum} />
        <Row label="Tipo de Voo" value={payload.tipoDeVooEnum} />
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Campo 9 - Informações da Aeronave</h2>
        <Row label="Número de Aeronaves" value={payload.numeroDeAeronaves} />
        <Row label="Tipo de Aeronave" value={payload.tipoDeAeronave} />
        <Row label="Esteira de Turbulância" value={payload.categoriaEsteiraTurbulenciaEnum} />
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Campo 10 - Equipamentos e Vigilância</h2>
        <Row label="Equipamentos (10A)" value={(payload.equipamentoCapacidadeDaAeronave||[]).join(', ')} />
        <Row label="Vigilância (10B)" value={(payload.vigilancia||[]).join(', ')} />
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Campo 13 - Informações de Partida</h2>
        <Row label="Aeródromo de Partida" value={payload.aerodromoDePartida} />
        <Row label="Hora de Partida (ZULU)" value={toHHmmFromIsoZ(payload.horaPartida)} />
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Campo 15 - Informações de Rota</h2>
        <Row label="Velocidade de Cruzeiro" value={payload.velocidadeDeCruzeiro} />
        <Row label="Nível de Voo" value={payload.nivelDeVoo} />
        <Row label="Rota" value={payload.rota} />
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Campo 16 - Destino e Alternativas</h2>
        <Row label="Aeródromo de Destino" value={payload.aerodromoDeDestino} />
        <Row label="EET Total" value={payload.tempoDeVooPrevisto} />
        <Row label="Aeródromo Alternativo" value={payload.aerodromoDeAlternativa} />
        <Row label="2º Aeródromo Alternativo" value={payload.aerodromoDeAlternativaSegundo} />
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Campo 18 - Outras Informações</h2>
        <Row label="OPR/" value={payload.outrasInformacoes?.opr} />
        <Row label="FROM/" value={payload.outrasInformacoes?.from} />
        <Row label="DOF/" value={payload.outrasInformacoes?.dof} />
        <Row label="RMK/" value={payload.outrasInformacoes?.rmk} />
        {(payload.outrasInformacoes?.per||[]).length ? (
          <Row label="PER/" value={(payload.outrasInformacoes?.per||[]).join(', ')} />
        ) : null}
        {((payload.outrasInformacoes?.eet || '').trim()) ? (
          <Row label="EET/ (FIRs)" value={payload.outrasInformacoes?.eet} />
        ) : null}
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Campo 19 - Informações Suplementares</h2>
        <Row label="Autonomia" value={payload.informacaoSuplementar?.autonomia} />
        <Row label="Equipamento Rádio Emergência" value={(payload.informacaoSuplementar?.radioEmergencia||[]).join(', ')} />
        <Row label="Equipamento de Sobrevivência" value={(payload.informacaoSuplementar?.sobrevivencia||[]).length ? (payload.informacaoSuplementar?.sobrevivencia||[]).join(', ') : 'S'} />
        <Row label="Coletes" value={(payload.informacaoSuplementar?.coletes||[]).length ? (payload.informacaoSuplementar?.coletes||[]).join(', ') : 'J'} />
        {payload.informacaoSuplementar?.botes?.possui ? (
          <>
            {payload.informacaoSuplementar?.botes?.numero !== undefined && (
              <Row label="Botes — Número" value={payload.informacaoSuplementar?.botes?.numero} />
            )}
            {payload.informacaoSuplementar?.botes?.capacidade !== undefined && (
              <Row label="Botes — Capacidade" value={payload.informacaoSuplementar?.botes?.capacidade} />
            )}
            {payload.informacaoSuplementar?.botes?.c !== undefined && (
              <Row label="Botes — Abrigo (C)" value={payload.informacaoSuplementar?.botes?.c ? 'Sim' : 'Não'} />
            )}
            {payload.informacaoSuplementar?.botes?.cor && (
              <Row label="Botes — Cor" value={payload.informacaoSuplementar?.botes?.cor} />
            )}
          </>
        ) : (
          <Row label="Botes" value="D" />
        )}
        <Row label="Cor e Marca da Aeronave" value={payload.informacaoSuplementar?.corEMarcaAeronave} />
        <Row label="N" value={payload.informacaoSuplementar?.n ? 'Sim' : 'Não'} />
        <Row label="Observações" value={payload.informacaoSuplementar?.observacoes} />
        <Row label="Piloto em Comando" value={payload.informacaoSuplementar?.pilotoEmComando} />
        <Row label="Cód. ANAC 1º Piloto" value={payload.informacaoSuplementar?.anacPrimeiroPiloto} />
        <Row label="Cód. ANAC 2º Piloto" value={payload.informacaoSuplementar?.anacSegundoPiloto} />
        <Row label="Telefone" value={payload.informacaoSuplementar?.telefone} />
      </div>
    </section>
  )
}


