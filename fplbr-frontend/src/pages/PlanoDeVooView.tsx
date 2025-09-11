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
        // Verificar se é um ID temporário
        if (id?.startsWith('temp-')) {
          const tempData = localStorage.getItem(`flightplan-${id}`)
          if (tempData) {
            const parsedData = JSON.parse(tempData)
            if (!mounted) return
            setData({ payload: parsedData })
            setLoading(false)
            return
          }
        }
        
        // Buscar dados do backend
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
  console.log('Payload na página de detalhes:', payload)
  console.log('equipamentoCapacidadeDaAeronave:', payload.equipamentoCapacidadeDaAeronave)
  console.log('vigilancia:', payload.vigilancia)

  const Row = ({label, value}:{label:string, value:any}) => (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-2 py-1">
      <div className="text-slate-500">{label}</div>
      <div className="md:col-span-2 font-medium break-words">{String(value ?? '')}</div>
    </div>
  )

  function formatTimeWithLocal(iso?: string): string {
    try {
      if (!iso) return ''
      const d = new Date(iso)
      // Hora Zulu (UTC)
      const zuluHH = String(d.getUTCHours()).padStart(2,'0')
      const zuluMM = String(d.getUTCMinutes()).padStart(2,'0')
      // Hora Local (UTC-3)
      const localTime = new Date(d.getTime() - (3 * 60 * 60 * 1000))
      const localHH = String(localTime.getUTCHours()).padStart(2,'0')
      const localMM = String(localTime.getUTCMinutes()).padStart(2,'0')
      return `${zuluHH}:${zuluMM} (Hora Local ${localHH}:${localMM})`
    } catch {
      return ''
    }
  }

  return (
    <section className="grid gap-6 p-4">
      <header className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Detalhe do Plano de Voo - {payload.modo === 'PVC' ? 'Plano de Voo Completo' : 'Plano de Voo Simplificado'}</h1>
          <p className="text-slate-600">Preencha exatamente esses dados no seu FPL BR e bom voo comandante ✈️🧑‍✈️</p>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/flightplan/novo" className="px-4 py-2 rounded-xl bg-indigo-600 text-white">Novo</Link>
          <Link to="/flightplan/listar" className="px-4 py-2 rounded-xl bg-slate-200">Listar</Link>
        </div>
      </header>

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
        <div className="grid grid-cols-1 md:grid-cols-3 gap-2 py-1">
          <div className="text-slate-500">
            <div className="font-medium">A</div>
            <div className="text-xs text-slate-400">Rádio Comunicação, Auxílios à Navegação e à Aproximação</div>
          </div>
          <div className="md:col-span-2 font-medium break-words">
            {(payload.equipamentoCapacidadeDaAeronave||[]).join(', ')}
          </div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-2 py-1">
          <div className="text-slate-500">
            <div className="font-medium">B</div>
            <div className="text-xs text-slate-400">Vigilância</div>
          </div>
          <div className="md:col-span-2 font-medium break-words">
            {(payload.vigilancia||[]).join(', ')}
          </div>
        </div>
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Campo 13 - Informações de Partida</h2>
        <Row label="Aeródromo de Partida" value={payload.aerodromoDePartida} />
        <Row label="Hora de Partida (ZULU)" value={formatTimeWithLocal(payload.horaPartida)} />
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
        {payload.outrasInformacoes?.sts && <Row label="STS/" value={payload.outrasInformacoes.sts} />}
        {payload.outrasInformacoes?.pbn && <Row label="PBN/" value={payload.outrasInformacoes.pbn} />}
        {payload.outrasInformacoes?.nav && <Row label="NAV/" value={payload.outrasInformacoes.nav} />}
        {payload.outrasInformacoes?.com && <Row label="COM/" value={payload.outrasInformacoes.com} />}
        {payload.outrasInformacoes?.dat && <Row label="DAT/" value={payload.outrasInformacoes.dat} />}
        {payload.outrasInformacoes?.sur && <Row label="SUR/" value={payload.outrasInformacoes.sur} />}
        {payload.outrasInformacoes?.dep && <Row label="DEP/" value={payload.outrasInformacoes.dep} />}
        {payload.outrasInformacoes?.dest && <Row label="DEST/" value={payload.outrasInformacoes.dest} />}
        {payload.outrasInformacoes?.reg && <Row label="REG/" value={payload.outrasInformacoes.reg} />}
        {payload.outrasInformacoes?.eet && <Row label="EET/" value={payload.outrasInformacoes.eet} />}
        {payload.outrasInformacoes?.sel && <Row label="SEL/" value={payload.outrasInformacoes.sel} />}
        {payload.outrasInformacoes?.typ && <Row label="TYP/" value={payload.outrasInformacoes.typ} />}
        {payload.outrasInformacoes?.code && <Row label="CODE/" value={payload.outrasInformacoes.code} />}
        {payload.outrasInformacoes?.dle && <Row label="DLE/" value={payload.outrasInformacoes.dle} />}
        {payload.outrasInformacoes?.opr && <Row label="OPR/" value={payload.outrasInformacoes.opr} />}
        {payload.outrasInformacoes?.orgn && <Row label="ORGN/" value={payload.outrasInformacoes.orgn} />}
        {(payload.outrasInformacoes?.per||[]).length ? (
          <Row label="PER/" value={(payload.outrasInformacoes?.per||[]).join(', ')} />
        ) : null}
        {payload.outrasInformacoes?.altn && <Row label="ALTN/" value={payload.outrasInformacoes.altn} />}
        {payload.outrasInformacoes?.ralt && <Row label="RALT/" value={payload.outrasInformacoes.ralt} />}
        {payload.outrasInformacoes?.talt && <Row label="TALT/" value={payload.outrasInformacoes.talt} />}
        {payload.outrasInformacoes?.rif && <Row label="RIF/" value={payload.outrasInformacoes.rif} />}
        {payload.outrasInformacoes?.rmk && <Row label="RMK/" value={payload.outrasInformacoes.rmk} />}
        {payload.outrasInformacoes?.from && <Row label="FROM/" value={payload.outrasInformacoes.from} />}
        {payload.outrasInformacoes?.dof && <Row label="DOF/" value={payload.outrasInformacoes.dof} />}
      </div>

      <div className="p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
        <h2 className="text-lg font-semibold mb-3">Campo 19 - Informações Suplementares</h2>
        <Row label="Autonomia" value={payload.informacaoSuplementar?.autonomia} />
        <Row label="Pessoas a bordo" value={payload.informacaoSuplementar?.pob} />
        <Row label="Equipamento Rádio Emergência" value={(payload.informacaoSuplementar?.radioEmergencia||[]).join(', ')} />
        <Row label="Equipamento de Sobrevivência" value={(payload.informacaoSuplementar?.sobrevivencia||[]).length ? (payload.informacaoSuplementar?.sobrevivencia||[]).join(', ') : 'Não'} />
        <Row label="Coletes" value={(payload.informacaoSuplementar?.coletes||[]).length ? (payload.informacaoSuplementar?.coletes||[]).join(', ') : 'Não'} />
        {payload.informacaoSuplementar?.botes?.possui ? (
          <>
            <Row label="Botes" value="D" />
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
          <Row label="Botes" value="Não" />
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


