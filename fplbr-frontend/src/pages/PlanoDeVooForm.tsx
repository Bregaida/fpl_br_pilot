import React from 'react'
import RadioGroup from '../components/RadioGroup'
import CheckboxGroup from '../components/CheckboxGroup'
import NivelAssist from '../components/NivelAssist'
import { PlanoDeVooDTO, ModoPlano } from '../types/PlanoDeVooDTO'
import { nowUtcISO, toUtcIsoFromLocalDateTimeLocal, todayDdmmaaUtc, parseHHmmToMinutes, isFutureAtLeastMinutes } from '../utils/time'
import { getSunInfo, isNoturnoPorJanela } from '../services/SunService'
import { vooNoturnoAutorizado } from '../services/AerodromoService'
import { api } from '../services/api'

const EQUIP_OPCOES = ['S','R','G','O','D','N','V','I']
const VIG_OPCOES = ['A','C','S','X','B1']
const OUTRAS_KEYS = ['STS','PBN','NAV','COM','DAT','SUR','DEP','DEST','REG','EET','SEL','TYP','CODE','DLE','OPR','ORGN','PER','ALTN','RALT','TALT','RIF','RMK','FROM','DOF'] as const

export default function PlanoDeVooForm() {
  const [data, setData] = React.useState<PlanoDeVooDTO>({
    identificacaoDaAeronave: 'PTOSP',
    indicativoDeChamada: false,
    regraDeVooEnum: 'V',
    tipoDeVooEnum: 'G',
    numeroDeAeronaves: 1,
    tipoDeAeronave: 'BL8',
    categoriaEsteiraTurbulenciaEnum: 'L',
    equipamentoCapacidadeDaAeronave: ['S'],
    vigilancia: ['A'],
    aerodromoDePartida: 'SBMT',
    horaPartida: nowUtcISO(),
    aerodromoDeDestino: 'SBMT',
    tempoDeVooPrevisto: '0030',
    aerodromoDeAlternativa: 'SBJD',
    velocidadeDeCruzeiro: 'N0100',
    nivelDeVoo: 'VFR',
    rota: 'DCT',
    outrasInformacoes: {
      opr: 'EDUARDO BREGAIDA',
      from: 'SBMT',
      dof: todayDdmmaaUtc(),
      rmk: ''
    },
    informacaoSuplementar: {
      autonomia: '0100',
      corEMarcaAeronave: 'BRANCA/VERMELHA COM ESTRELAS BRANCAS',
      pilotoEmComando: 'BREGAIDA',
      anacPrimeiroPiloto: '123456',
      telefone: '11985586633'
    },
    modo: 'PVS'
  })

  const [rumo, setRumo] = React.useState<number | undefined>(undefined)
  const [perfilConsulta, setPerfilConsulta] = React.useState<'VFR'|'IFR'>('VFR')
  const [horaLocal, setHoraLocal] = React.useState<string>('') // HH:mm
  const [errors, setErrors] = React.useState<Record<string,string>>({})
  const [alertaNoturno, setAlertaNoturno] = React.useState<string>('')
  const [submitting, setSubmitting] = React.useState(false)
  const [outrasSelecionadas, setOutrasSelecionadas] = React.useState<string[]>(['OPR','RMK','FROM','DOF'])

  React.useEffect(() => {
    if (data.modo === 'PVC') {
      setOutrasSelecionadas(['EET','OPR','PER','RMK','FROM','DOF'])
    } else {
      setOutrasSelecionadas(['OPR','RMK','FROM','DOF'])
    }
  }, [data.modo])

  function set<K extends keyof PlanoDeVooDTO>(key: K, value: PlanoDeVooDTO[K]) {
    setData(prev => ({ ...prev, [key]: value }))
  }

  function setOutras<K extends keyof PlanoDeVooDTO['outrasInformacoes']>(key: K, value: PlanoDeVooDTO['outrasInformacoes'][K]) {
    setData(prev => ({ ...prev, outrasInformacoes: { ...prev.outrasInformacoes, [key]: value } }))
  }

  function setInfoSup<K extends keyof PlanoDeVooDTO['informacaoSuplementar']>(key: K, value: PlanoDeVooDTO['informacaoSuplementar'][K]) {
    setData(prev => ({ ...prev, informacaoSuplementar: { ...prev.informacaoSuplementar, [key]: value } }))
  }

  function uppercaseField(e: React.ChangeEvent<HTMLInputElement>) {
    e.target.value = e.target.value.toUpperCase()
  }

  function buildIsoFromDofAndHora(): string | null {
    const dof = data.outrasInformacoes.dof
    const m = /^(\d{2})(\d{2})(\d{2})$/.exec(dof || '')
    const t = /^(\d{2}):(\d{2})$/.exec(horaLocal || '')
    if (!m || !t) return null
    const dd = parseInt(m[1],10)
    const MM = parseInt(m[2],10) - 1
    const yy = 2000 + parseInt(m[3],10)
    const HH = parseInt(t[1],10)
    const mm = parseInt(t[2],10)
    const d = new Date(Date.UTC(yy, MM, dd, HH, mm, 0))
    return d.toISOString()
  }

  function validate(): boolean {
    const e: Record<string,string> = {}

    if (!/^[A-Z0-9]{3,7}$/.test(data.identificacaoDaAeronave)) {
      e.identificacaoDaAeronave = 'Informe a identificação da aeronave (ex.: PTOSP)'
    }

    if (!data.regraDeVooEnum) e.regraDeVooEnum = 'Selecione a regra de voo'
    if (!data.tipoDeVooEnum) e.tipoDeVooEnum = 'Selecione o tipo de voo'

    if (data.indicativoDeChamada) {
      if (!data.numeroDeAeronaves || data.numeroDeAeronaves < 1) e.numeroDeAeronaves = 'Informe o número de aeronaves (>0)'
      if (!/^[A-Z0-9]{2,4}$/.test(data.tipoDeAeronave)) e.tipoDeAeronave = 'Tipo de aeronave inválido (ex.: BL8, P28A)'
    } else {
      if (data.numeroDeAeronaves !== 1) set('numeroDeAeronaves', 1)
    }

    if (!/^[A-Z0-9]{2,4}$/.test(data.tipoDeAeronave)) e.tipoDeAeronave = 'Tipo de aeronave inválido (ex.: BL8, P28A)'

    if (!data.categoriaEsteiraTurbulenciaEnum) e.categoriaEsteiraTurbulenciaEnum = 'Selecione a categoria de esteira de turbulência'

    if (!data.equipamentoCapacidadeDaAeronave?.length) e.equipamentoCapacidadeDaAeronave = 'Selecione pelo menos um equipamento'
    if (!data.vigilancia?.length) e.vigilancia = 'Selecione pelo menos um item de vigilância'

    if (!/^[A-Z]{4}$/.test(data.aerodromoDePartida)) e.aerodromoDePartida = 'Informe o aeródromo de partida (ICAO 4 letras, ex.: SBMT)'
    if (!/^[A-Z]{4}$/.test(data.aerodromoDeDestino)) e.aerodromoDeDestino = 'Informe o aeródromo de destino (ICAO 4 letras)'
    if (!/^[A-Z]{4}$/.test(data.aerodromoDeAlternativa)) e.aerodromoDeAlternativa = 'Informe o aeródromo de alternativa (ICAO 4 letras)'
    if (data.aerodromoDeAlternativaSegundo && !/^[A-Z]{4}$/.test(data.aerodromoDeAlternativaSegundo)) e.aerodromoDeAlternativaSegundo = 'ICAO inválido'

    if (!/^[NKM][0-9]{4}$/.test(data.velocidadeDeCruzeiro)) e.velocidadeDeCruzeiro = 'Informe velocidade de cruzeiro (ex.: N0100)'
    if (!data.nivelDeVoo) e.nivelDeVoo = 'Informe o nível de voo (ex.: VFR, A045, F055)'

    if (!data.tempoDeVooPrevisto || isNaN(parseHHmmToMinutes(data.tempoDeVooPrevisto))) e.tempoDeVooPrevisto = 'Informe EET (HHmm)'
    if (!data.informacaoSuplementar.autonomia || isNaN(parseHHmmToMinutes(data.informacaoSuplementar.autonomia))) e.autonomia = 'Informe autonomia'
    const eetMin = parseHHmmToMinutes(data.tempoDeVooPrevisto)
    const autMin = parseHHmmToMinutes(data.informacaoSuplementar.autonomia)
    if (!isNaN(eetMin) && !isNaN(autMin) && eetMin > autMin) e.tempoDeVooPrevisto = 'EET não pode exceder a autonomia informada'

    // Hora partida conforme modo
    const minAhead = data.modo === 'PVC' ? 45 : 15
    const iso = buildIsoFromDofAndHora()
    if (!iso || !isFutureAtLeastMinutes(iso, minAhead)) {
      e.horaPartida = `Hora de partida deve ser ≥ agora UTC + ${minAhead} min`
    }

    // OutrasInformações
    if (!data.outrasInformacoes.opr) e.opr = 'OPR/ é obrigatório'
    if (!/^[A-Z]{4}$/.test(data.outrasInformacoes.from)) e.from = 'FROM/ deve ser ICAO 4 letras'
    if (!/^\d{6}$/.test(data.outrasInformacoes.dof)) e.dof = 'DOF/ deve ser ddmmaa'
    if (data.modo === 'PVC' && (!data.outrasInformacoes.per || data.outrasInformacoes.per.length === 0)) e.per = 'PER/ é obrigatório no PVC'
    if (/\bREA\b/i.test(data.rota) && !/\bREA\b/.test(data.outrasInformacoes.rmk || '')) e.rmk = 'RMK/ deve conter REA e corredores quando rota tiver REA'

    // Informação Suplementar
    if (!data.informacaoSuplementar.corEMarcaAeronave) e.corEMarcaAeronave = 'Cor e Marca da Aeronave é obrigatório'
    if (!data.informacaoSuplementar.pilotoEmComando) e.pilotoEmComando = 'Piloto em Comando é obrigatório'
    if (!data.informacaoSuplementar.anacPrimeiroPiloto) e.anacPrimeiroPiloto = 'Cód. ANAC 1º Piloto é obrigatório'
    if (!data.informacaoSuplementar.anacSegundoPiloto) e.anacSegundoPiloto = 'Cód. ANAC 2º Piloto é obrigatório'
    if (!/^\d{10,11}$/.test(data.informacaoSuplementar.telefone)) e.telefone = 'Informe um telefone válido (somente números)'
    if (!data.informacaoSuplementar.radioEmergencia || (data.informacaoSuplementar.radioEmergencia.length === 0)) e.radioEmergencia = 'Selecione ao menos um equipamento de rádio de emergência'
    if (data.informacaoSuplementar.n && !(data.informacaoSuplementar.observacoes && data.informacaoSuplementar.observacoes.trim().length>0)) e.observacoes = 'Observações tornam-se obrigatórias quando N = sim'

    setErrors(e)
    return Object.keys(e).length === 0
  }

  async function verificarNoturno() {
    setAlertaNoturno('')
    try {
      const sun = await getSunInfo(data.aerodromoDeAlternativa, data.outrasInformacoes.dof)
      const noturno = isNoturnoPorJanela(data.horaPartida, data.tempoDeVooPrevisto, sun)
      if (noturno) {
        const autorizado = await vooNoturnoAutorizado(data.aerodromoDeAlternativa)
        if (!autorizado) {
          setAlertaNoturno('Aeródromo sem condições para voo noturno; a administração deve ser notificada')
          // sugestão de placeholder
          if (data.aerodromoDeAlternativa === 'SBJD') set('aerodromoDeAlternativa', 'SBSJ')
        }
      }
    } catch {
      // manter silencioso (dados provisórios)
    }
  }

  React.useEffect(() => { verificarNoturno() }, [data.aerodromoDeAlternativa, data.horaPartida, data.tempoDeVooPrevisto, data.outrasInformacoes.dof])

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!validate()) return
    setSubmitting(true)
    try {
      const iso = buildIsoFromDofAndHora()
      if (iso) data.horaPartida = iso
      await api.post('/api/v1/fpl', data)
      alert('FPL enviado com sucesso')
    } catch (err: any) {
      alert('Erro ao enviar FPL: ' + (err?.response?.data?.message || err?.message || 'desconhecido'))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="grid gap-6 p-4">
      <header className="space-y-1">
        <h1 className="text-3xl font-semibold tracking-tight">Plano de Voo</h1>
        <p className="text-slate-600">Preencha todos os campos conforme as regras. Campos em ICAO/Tipo são uppercase.</p>
      </header>

      {Object.keys(errors).length > 0 && (
        <div className="p-3 rounded border border-red-300 bg-red-50 text-red-800 text-sm">
          Corrija os erros destacados nos campos.
        </div>
      )}

      {alertaNoturno && (
        <div className="p-3 rounded border border-amber-300 bg-amber-50 text-amber-800 text-sm">{alertaNoturno}</div>
      )}

      <form onSubmit={onSubmit} className="grid gap-6">
        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-modo">Modo</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Plano (PVC/PVS)</label>
              <div className="flex items-center gap-3">
                <label className="inline-flex items-center gap-2"><input type="radio" className="accent-indigo-600" checked={data.modo==='PVS'} onChange={()=>set('modo','PVS')} />PVS</label>
                <label className="inline-flex items-center gap-2"><input type="radio" className="accent-indigo-600" checked={data.modo==='PVC'} onChange={()=>set('modo','PVC')} />PVC</label>
              </div>
            </div>
          </div>
        </div>
        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-identificacao">Campo 7 - Identificação da Aeronave</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Identificação da Aeronave*</label>
              <input className="input" value={data.identificacaoDaAeronave}
                onInput={uppercaseField}
                onChange={e => set('identificacaoDaAeronave', e.target.value.toUpperCase())}
                placeholder="PTOSP" />
              {errors.identificacaoDaAeronave && <p className="text-red-600 text-sm">{errors.identificacaoDaAeronave}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Indicativo</label>
              <label className="inline-flex items-center gap-2">
                <input type="checkbox" className="accent-indigo-600" checked={data.indicativoDeChamada} onChange={e => set('indicativoDeChamada', e.target.checked)} />
                <span>{data.indicativoDeChamada ? 'Não' : 'Sim'}</span>
              </label>
            </div>
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-regras">Campo 8 - Regras de Voo e Tipo de Voo</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <RadioGroup name="regraDeVooEnum" label={<><span>Regra de Voo*</span></>} options={[
              {value:'I',label:'I',tooltip:'IFR'},
              {value:'V',label:'V',tooltip:'VFR'},
              {value:'Y',label:'Y',tooltip:'Decola VFR e entra IFR'},
              {value:'Z',label:'Z',tooltip:'Decola IFR e entra VFR'}
            ]} value={data.regraDeVooEnum} onChange={v => set('regraDeVooEnum', v as any)} error={errors.regraDeVooEnum} />
            <RadioGroup name="tipoDeVooEnum" label="Tipo de Voo*" options={[
              {value:'G',label:'G',tooltip:'Geral'},
              {value:'S',label:'S',tooltip:'Especiais'},
              {value:'N',label:'N',tooltip:'Militar'},
              {value:'M',label:'M',tooltip:'Misto'},
              {value:'X',label:'X',tooltip:'Outro'}
            ]} value={data.tipoDeVooEnum} onChange={v => set('tipoDeVooEnum', v as any)} error={errors.tipoDeVooEnum} />
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-aeronave">Campo 9 - Informações da Aeronave</h2>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Número de Aeronaves{data.indicativoDeChamada ? '*' : ''}</label>
              <input type="number" min={1} className="input" value={data.numeroDeAeronaves}
                onChange={e => set('numeroDeAeronaves', Math.max(1, parseInt(e.target.value || '1', 10)))} disabled={!data.indicativoDeChamada} />
              {errors.numeroDeAeronaves && <p className="text-red-600 text-sm">{errors.numeroDeAeronaves}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Tipo de Aeronave*</label>
              <input className="input" value={data.tipoDeAeronave}
                onInput={uppercaseField}
                onChange={e => set('tipoDeAeronave', e.target.value.toUpperCase())}
                placeholder="BL8, P28A" />
              {errors.tipoDeAeronave && <p className="text-red-600 text-sm">{errors.tipoDeAeronave}</p>}
            </div>
            <RadioGroup name="categoriaEsteiraTurbulenciaEnum" label={<><span>Esteira de Turbulância*</span></>} options={[
              {value:'L',label:'L',tooltip:'Leve'},
              {value:'M',label:'M',tooltip:'Média'},
              {value:'H',label:'H',tooltip:'Pesada'},
              {value:'J',label:'J',tooltip:'Super'}
            ]} value={data.categoriaEsteiraTurbulenciaEnum} onChange={v => set('categoriaEsteiraTurbulenciaEnum', v as any)} error={errors.categoriaEsteiraTurbulenciaEnum} />
            <div />
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-equip-vig">Campo 10 - Equipamentos e Vigilância</h2>
          <CheckboxGroup name="equip" label={<><span>A*</span><sup title="Rádiocomunicações, Auxílio à Navegação e à Aproximação" className="text-slate-500 ml-1 font-bold">¹</sup></>} options={EQUIP_OPCOES.map(v => ({
            value:v,
            label:v,
            tooltip: (
              v==='S'?'Transponder':
              v==='R'?'GNSS':
              v==='G'?'GNSS (RNP)':
              v==='O'?'VOR/ILS':
              v==='D'?'DME':
              v==='N'?'Sem equipamento':
              v==='V'?'VHF Voice':
              v==='I'?'Inercial':
              undefined)
          }))} values={data.equipamentoCapacidadeDaAeronave} onChange={vals => set('equipamentoCapacidadeDaAeronave', vals)} error={errors.equipamentoCapacidadeDaAeronave} />
          <CheckboxGroup name="vigilancia" label={<><span>B*</span><sup title="Vigilância" className="text-slate-500 ml-1 font-bold">¹</sup></>} options={VIG_OPCOES.map(v => ({value:v,label:v,tooltip:(v==='A'?'ADS-B':v==='S'?'SSR':v==='C'?'MLAT':v==='B1'?'RVSM':v==='X'?'Sem vigilância':undefined)}))} values={data.vigilancia} onChange={vals => set('vigilancia', vals)} error={errors.vigilancia} />
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-partida">Campo 13 - Informações de Partida</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Aeródromo de Partida*</label>
              <input className="input" value={data.aerodromoDePartida} onInput={uppercaseField} onChange={e => set('aerodromoDePartida', e.target.value.toUpperCase())} placeholder="ex.: SBMT" />
              {errors.aerodromoDePartida && <p className="text-red-600 text-sm">{errors.aerodromoDePartida}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Hora de Partida (UTC)*</label>
              <input type="time" className="input" value={horaLocal} onChange={e => setHoraLocal(e.target.value)} placeholder="HH:mm" />
              {errors.horaPartida && <p className="text-red-600 text-sm">{errors.horaPartida}</p>}
            </div>
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-destino">Campo 16 - Destino e Alternativas</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Aeródromo de Destino*</label>
              <input className="input" value={data.aerodromoDeDestino} onInput={uppercaseField} onChange={e => set('aerodromoDeDestino', e.target.value.toUpperCase())} placeholder="ex.: SBMT" />
              {errors.aerodromoDeDestino && <p className="text-red-600 text-sm">{errors.aerodromoDeDestino}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">EET Total*</label>
              <input className="input" value={data.tempoDeVooPrevisto} onChange={e => set('tempoDeVooPrevisto', e.target.value)} placeholder="ex.: 0030" />
              {errors.tempoDeVooPrevisto && <p className="text-red-600 text-sm">{errors.tempoDeVooPrevisto}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Aeródromo Alternativo*</label>
              <input className="input" value={data.aerodromoDeAlternativa} onInput={uppercaseField} onChange={e => set('aerodromoDeAlternativa', e.target.value.toUpperCase())} placeholder="ex.: SBJD (se noturno, sugerir SBSJ)" />
              {errors.aerodromoDeAlternativa && <p className="text-red-600 text-sm">{errors.aerodromoDeAlternativa}</p>}
            </div>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">2º Aeródromo Alternativo (opcional)</label>
              <input className="input" value={data.aerodromoDeAlternativaSegundo || ''} onInput={uppercaseField} onChange={e => set('aerodromoDeAlternativaSegundo', e.target.value ? e.target.value.toUpperCase() : undefined)} placeholder="ex.: SBxx (opcional)" />
              {errors.aerodromoDeAlternativaSegundo && <p className="text-red-600 text-sm">{errors.aerodromoDeAlternativaSegundo}</p>}
            </div>
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-infos-rota">Campo 15 - Informações de Rota</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Velocidade de Cruzeiro*</label>
              <input className="input" value={data.velocidadeDeCruzeiro} onChange={e => set('velocidadeDeCruzeiro', e.target.value.toUpperCase())} placeholder="ex.: N0100" />
              {errors.velocidadeDeCruzeiro && <p className="text-red-600 text-sm">{errors.velocidadeDeCruzeiro}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Nível de Voo*</label>
              <input className="input" value={data.nivelDeVoo} onChange={e => set('nivelDeVoo', e.target.value.toUpperCase())} placeholder="ex.: VFR, A045, F055" />
              {errors.nivelDeVoo && <p className="text-red-600 text-sm">{errors.nivelDeVoo}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Rota*</label>
              <textarea className="input" rows={3} value={data.rota} onChange={e => set('rota', e.target.value)} placeholder={perfilConsulta==='VFR' ? 'REA DELTA ECHO LIMA ALT MAX REA' : 'DCT PNG/N0250F070 IFR DCT CGH'} />
              {errors.rota && <p className="text-red-600 text-sm">{errors.rota}</p>}
            </div>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Perfil de Consulta</label>
              <div className="flex items-center gap-3">
                <label className="inline-flex items-center gap-2"><input type="radio" className="accent-indigo-600" checked={perfilConsulta==='VFR'} onChange={()=>setPerfilConsulta('VFR')} />VFR</label>
                <label className="inline-flex items-center gap-2"><input type="radio" className="accent-indigo-600" checked={perfilConsulta==='IFR'} onChange={()=>setPerfilConsulta('IFR')} />IFR</label>
              </div>
              <label className="font-medium">Rumo Magnético</label>
              <input type="number" min={0} max={359} className="input" value={rumo ?? ''} onChange={e => setRumo(e.target.value === '' ? undefined : Math.max(0, Math.min(359, parseInt(e.target.value, 10))))} placeholder="0 a 359" />
            </div>
            <div className="md:col-span-2">
              <NivelAssist perfil={perfilConsulta} rumoMagnetico={rumo} />
            </div>
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-outros">Campo 18 - Outras Informações</h2>
          <CheckboxGroup
            name="outras-chaves"
            label={<><span>Outras Informações*</span></>}
            options={OUTRAS_KEYS.map(k => ({ value: k, label: k }))}
            values={outrasSelecionadas}
            onChange={(vals) => setOutrasSelecionadas(vals as string[])}
          />

          {/* Campos dinâmicos por chave */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {outrasSelecionadas.includes('EET') && (
              <div className="grid gap-1">
                <label className="font-medium">EET/ (FIR + tempo)</label>
                <input className="input" value={data.outrasInformacoes.eet || ''} onChange={e => setOutras('eet', e.target.value)} placeholder="SBC0005 SBBS0150" />
              </div>
            )}
            {outrasSelecionadas.includes('OPR') && (
              <div className="grid gap-1">
                <label className="font-medium">OPR/*</label>
                <input className="input" value={data.outrasInformacoes.opr} onChange={e => setOutras('opr', e.target.value)} placeholder="Eduardo Bregaida" />
                {errors.opr && <p className="text-red-600 text-sm">{errors.opr}</p>}
              </div>
            )}
            {outrasSelecionadas.includes('FROM') && (
              <div className="grid gap-1">
                <label className="font-medium">FROM/*</label>
                <input className="input" value={data.outrasInformacoes.from} onInput={uppercaseField} onChange={e => setOutras('from', e.target.value.toUpperCase())} placeholder="SBMT" />
                {errors.from && <p className="text-red-600 text-sm">{errors.from}</p>}
              </div>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {outrasSelecionadas.includes('PER') && (
              <div className="grid gap-1">
                <label className="font-medium">PER/ (PVC)</label>
                <div className="flex flex-wrap gap-3">
                  {['A','B','C','D','E','H'].map(p => (
                    <label key={p} className="inline-flex items-center gap-2">
                      <input type="checkbox" className="accent-indigo-600" checked={(data.outrasInformacoes.per||[]).includes(p)} onChange={() => {
                        const set = new Set(data.outrasInformacoes.per||[]); if(set.has(p)) set.delete(p); else set.add(p); setOutras('per', Array.from(set))
                      }} />
                      <span>{p}<sup
                        title={
                          p==='A' ? 'Categoria A: Inclui aeronaves com velocidade aerodinâmica indicada menor que \(91\\text{kt}\).' :
                          p==='B' ? 'Categoria B: Abrange aeronaves com velocidade aerodinâmica indicada igual ou maior que \(91\\text{kt}\) e menor que \(121\\text{kt}\).' :
                          p==='C' ? 'Categoria C: Engloba aeronaves com velocidade aerodinâmica indicada igual ou maior que \(121\\text{kt}\) e menor que \(141\\text{kt}\).' :
                          p==='D' ? 'Categoria D: Corresponde a aeronaves com velocidade aerodinâmica indicada igual ou maior que \(141\\text{kt}\) e menor que \(166\\text{kt}\).' :
                          p==='E' ? 'Categoria E: Refere-se a aeronaves com velocidade aerodinâmica indicada igual ou maior que \(166\\text{kt}\) e menor que \(211\\text{kt}\).' :
                          'Helicópteros'
                        }
                        className="text-slate-500 ml-1"
                      >¹</sup></span>
                    </label>
                  ))}
                </div>
                {errors.per && <p className="text-red-600 text-sm">{errors.per}</p>}
              </div>
            )}
            {outrasSelecionadas.includes('RMK') && (
              <div className="grid gap-1">
                <label className="font-medium">RMK/*</label>
                <input className="input" value={data.outrasInformacoes.rmk || ''} onChange={e => setOutras('rmk', e.target.value)} placeholder="REA DELTA ECHO LIMA ALT MAX REA" />
                {errors.rmk && <p className="text-red-600 text-sm">{errors.rmk}</p>}
              </div>
            )}
            {outrasSelecionadas.includes('DOF') && (
              <div className="grid gap-1">
                <label className="font-medium">DOF/* (ddmmaa)</label>
                <input className="input" value={data.outrasInformacoes.dof} onChange={e => setOutras('dof', e.target.value)} placeholder="ddmmaa" />
                {errors.dof && <p className="text-red-600 text-sm">{errors.dof}</p>}
              </div>
            )}
          </div>

          {/* Campos genéricos restantes */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {['STS','PBN','NAV','COM','DAT','SUR','DEP','DEST','REG','SEL','TYP','CODE','DLE','ORGN','ALTN','RALT','TALT','RIF'].map(k => (
              outrasSelecionadas.includes(k) ? (
                <div key={k} className="grid gap-1">
                  <label className="font-medium">{k}/</label>
                  <input className="input" value={(data.outrasInformacoes as any)[k.toLowerCase()] || ''}
                    onChange={e => setOutras(k.toLowerCase() as any, e.target.value)} />
                </div>
              ) : null
            ))}
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-suplementar">Campo 19 - Informações Suplementares</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Autonomia*</label>
              <input className="input" value={data.informacaoSuplementar.autonomia} onChange={e => setInfoSup('autonomia', e.target.value)} placeholder="0100" />
              {errors.autonomia && <p className="text-red-600 text-sm">{errors.autonomia}</p>}
            </div>
            <CheckboxGroup name="radioEmg" label={<><span>Equipamento Rádio Emergência*</span></>} options={[{value:'U',label:'U',tooltip:'UHF'},{value:'V',label:'V',tooltip:'VHF'},{value:'E',label:'E',tooltip:'ELT'}]} values={data.informacaoSuplementar.radioEmergencia || []} onChange={vals => setInfoSup('radioEmergencia', vals)} />
            <div className="grid gap-1">
              <label className="font-medium">Equipamento de Sobrevivência*</label>
              <label className="inline-flex items-center gap-2">
                <input type="checkbox" className="accent-indigo-600" checked={(data.informacaoSuplementar.sobrevivencia||[]).length>0} onChange={e => setInfoSup('sobrevivencia', e.target.checked ? ['S'] : [])} />
                <span>Sim</span>
              </label>
              {(data.informacaoSuplementar.sobrevivencia||[]).length>0 && (
                <CheckboxGroup name="sobrevExtra" label={<><span>Opções</span></>} options={[
                  {value:'S',label:'S',tooltip:'Equipamentos de sobrevivência'},
                  {value:'P',label:'P',tooltip:'Polar'},
                  {value:'D',label:'D',tooltip:'Deserto'},
                  {value:'M',label:'M',tooltip:'Marítimo'},
                  {value:'J',label:'J',tooltip:'Selva'}
                ]} values={data.informacaoSuplementar.sobrevivencia || []} onChange={vals => setInfoSup('sobrevivencia', vals)} />
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Coletes*</label>
              <label className="inline-flex items-center gap-2">
                <input type="checkbox" className="accent-indigo-600" checked={(data.informacaoSuplementar.coletes||[]).includes('J')} onChange={e => {
                  const on = e.target.checked; const rest = (data.informacaoSuplementar.coletes||[]).filter(x=>x!=='J'); setInfoSup('coletes', on ? ['J', ...rest] : rest)
                }} />
                <span>Sim</span>
              </label>
              {(data.informacaoSuplementar.coletes||[]).includes('J') && (
                <CheckboxGroup name="coletesExtras" label={<><span>Opções</span></>} options={[{value:'L',label:'L',tooltip:'Luz'},{value:'F',label:'F',tooltip:'Fluorescente'},{value:'U',label:'U',tooltip:'UHF'},{value:'V',label:'V',tooltip:'VHF'}]} values={(data.informacaoSuplementar.coletes||[]).filter(v=>v!=='J')} onChange={vals => setInfoSup('coletes', ['J', ...vals])} />
              )}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Botes*<sup title="Informar número, capacidade, abrigo (C) e cor se Sim" className="text-slate-500 ml-1">¹</sup></label>
              <label className="inline-flex items-center gap-2">
                <input type="checkbox" className="accent-indigo-600" checked={!!data.informacaoSuplementar.botes?.possui} onChange={e => setInfoSup('botes', { ...(data.informacaoSuplementar.botes||{possui:false}), possui: e.target.checked })} />
                <span>Sim</span>
              </label>
            </div>
          </div>
          {data.informacaoSuplementar.botes?.possui && (
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="grid gap-1">
                <label className="font-medium">Botes — Número</label>
                <input type="number" min={1} className="input" value={data.informacaoSuplementar.botes?.numero || 1} onChange={e => setInfoSup('botes', { ...(data.informacaoSuplementar.botes||{possui:true}), numero: Math.max(1, parseInt(e.target.value||'1',10)) })} />
              </div>
              <div className="grid gap-1">
                <label className="font-medium">Botes — Capacidade</label>
                <input type="number" min={1} className="input" value={data.informacaoSuplementar.botes?.capacidade || 1} onChange={e => setInfoSup('botes', { ...(data.informacaoSuplementar.botes||{possui:true}), capacidade: Math.max(1, parseInt(e.target.value||'1',10)) })} />
              </div>
              <div className="grid gap-1">
                <label className="font-medium">Botes — Abrigo (C)</label>
                <select className="input" value={data.informacaoSuplementar.botes?.c ? 'true' : 'false'} onChange={e => setInfoSup('botes', { ...(data.informacaoSuplementar.botes||{possui:true}), c: e.target.value === 'true' })}>
                  <option value="false">Não</option>
                  <option value="true">Sim</option>
                </select>
              </div>
              <div className="grid gap-1">
                <label className="font-medium">Botes — Cor</label>
                <input className="input" value={data.informacaoSuplementar.botes?.cor || ''} onChange={e => setInfoSup('botes', { ...(data.informacaoSuplementar.botes||{possui:true}), cor: e.target.value })} placeholder="cor" />
              </div>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Cor e Marca da Aeronave*</label>
              <input className="input" value={data.informacaoSuplementar.corEMarcaAeronave} onChange={e => setInfoSup('corEMarcaAeronave', e.target.value)} />
              {errors.corEMarcaAeronave && <p className="text-red-600 text-sm">{errors.corEMarcaAeronave}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">N<sup title="Se selecionado, Observações tornam-se obrigatórias" className="text-slate-500 ml-1">¹</sup></label>
              <div className="flex items-center gap-6">
                <label className="inline-flex items-center gap-2">
                  <input type="checkbox" className="accent-indigo-600" checked={!!data.informacaoSuplementar.n} onChange={e => setInfoSup('n', e.target.checked)} />
                  <span>Sim</span>
                </label>
              </div>
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Observações</label>
              <input className="input" value={data.informacaoSuplementar.observacoes || ''} onChange={e => setInfoSup('observacoes', e.target.value)} />
              {errors.observacoes && <p className="text-red-600 text-sm">{errors.observacoes}</p>}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Piloto em Comando*</label>
              <input className="input" value={data.informacaoSuplementar.pilotoEmComando} onChange={e => setInfoSup('pilotoEmComando', e.target.value)} placeholder="Bregaida" />
              {errors.pilotoEmComando && <p className="text-red-600 text-sm">{errors.pilotoEmComando}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Cód. ANAC 1º Piloto*</label>
              <input className="input" value={data.informacaoSuplementar.anacPrimeiroPiloto} onChange={e => setInfoSup('anacPrimeiroPiloto', e.target.value)} />
              {errors.anacPrimeiroPiloto && <p className="text-red-600 text-sm">{errors.anacPrimeiroPiloto}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Cód. ANAC 2º Piloto*</label>
              <input className="input" value={data.informacaoSuplementar.anacSegundoPiloto || ''} onChange={e => setInfoSup('anacSegundoPiloto', e.target.value)} />
              {errors.anacSegundoPiloto && <p className="text-red-600 text-sm">{errors.anacSegundoPiloto}</p>}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Telefone*</label>
              <input className="input" value={data.informacaoSuplementar.telefone} onChange={e => setInfoSup('telefone', e.target.value.replace(/\D/g, ''))} placeholder="11985586633" />
              {errors.telefone && <p className="text-red-600 text-sm">{errors.telefone}</p>}
            </div>
            <div />
            <div />
          </div>
        </div>

        <div className="flex items-center gap-3">
          <button type="submit" className="px-4 py-2 rounded-xl bg-indigo-600 text-white" disabled={submitting}>{submitting ? 'Salvando...' : 'Salvar'}</button>
        </div>
      </form>
    </section>
  )
}


