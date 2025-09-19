import React from 'react'
import RadioGroup from '../components/RadioGroup'
import CheckboxGroup from '../components/CheckboxGroup'
import NivelAssist from '../components/NivelAssist'
import { PlanoDeVooDTO } from '../types/PlanoDeVooDTO'
import { nowUtcISO, todayDdmmaaUtc, parseHHmmToMinutes, isFutureAtLeastMinutes } from '../utils/time'
import { getSunInfo, isNoturnoPorJanela } from '../services/SunService'
// import { vooNoturnoAutorizado } from '../services/AerodromoService' // Removido - usando apenas AISWEB
import { api } from '../services/api'
import { useNavigate } from 'react-router-dom'

const EQUIP_OPCOES = ['S','R','G','O','D','N','V','I']
const VIG_OPCOES = ['A','C','S','X','B1']
const OUTRAS_KEYS = ['STS','PBN','NAV','COM','DAT','SUR','DEP','DEST','REG','EET','SEL','TYP','CODE','DLE','OPR','ORGN','PER','ALTN','RALT','TALT','RIF','RMK','FROM','DOF'] as const

export default function PlanoDeVooForm() {
  const navigate = useNavigate()
  const [data, setData] = React.useState<PlanoDeVooDTO>({
    identificacaoDaAeronave: '',
    indicativoDeChamada: false,
    regraDeVooEnum: 'V',
    tipoDeVooEnum: 'G',
    numeroDeAeronaves: 1,
    tipoDeAeronave: '',
    categoriaEsteiraTurbulenciaEnum: 'L',
    equipamentoCapacidadeDaAeronave: ['S'],
    vigilancia: ['A'],
    aerodromoDePartida: '',
    horaPartida: nowUtcISO(),
    aerodromoDeDestino: '',
    tempoDeVooPrevisto: '',
    aerodromoDeAlternativa: '',
    velocidadeDeCruzeiro: '',
    nivelDeVoo: '',
    rota: '',
    outrasInformacoes: {
      opr: '',
      from: '',
      dof: todayDdmmaaUtc(),
      rmk: ''
    },
    informacaoSuplementar: {
      autonomia: '',
      pob: 1,
      corEMarcaAeronave: '',
      pilotoEmComando: '',
      anacPrimeiroPiloto: '',
      telefone: ''
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

  function ymdToday(): string {
    const d = new Date()
    const yyyy = d.getUTCFullYear()
    const mm = String(d.getUTCMonth()+1).padStart(2,'0')
    const dd = String(d.getUTCDate()).padStart(2,'0')
    return `${yyyy}-${mm}-${dd}`
  }

  function ddmmaaToYmd(ddmmaa?: string): string {
    const m = /^([0-3]\d)([0-1]\d)(\d{2})$/.exec(ddmmaa||'')
    if (!m) return ymdToday()
    const dd = m[1], MM = m[2], yy = m[3]
    return `20${yy}-${MM}-${dd}`
  }

  function ymdToDdmmaa(ymd: string): string {
    const parts = ymd.split('-')
    if (parts.length !== 3) return todayDdmmaaUtc()
    const [yyyy, MM, dd] = parts
    return `${dd}${MM}${yyyy.slice(2)}`
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
    
    // CORREÇÃO: Se o horário for antes das 6h, considerar como do dia seguinte
    let targetDay = dd
    let targetMonth = MM
    let targetYear = yy
    
    if (HH < 6) {
      // Horário noturno - considerar como do dia seguinte
      const nextDay = new Date(yy, MM, dd + 1)
      targetDay = nextDay.getDate()
      targetMonth = nextDay.getMonth()
      targetYear = nextDay.getFullYear()
    }
    
    // Criar data no horário local
    const d = new Date(targetYear, targetMonth, targetDay, HH, mm, 0)
    
    // Converter para UTC (considerando o fuso horário local)
    const utcTime = new Date(d.getTime() - (d.getTimezoneOffset() * 60000))
    
    console.log('Debug horário:')
    console.log('- Horário original:', horaLocal)
    console.log('- DOF original:', data.outrasInformacoes.dof)
    console.log('- Data calculada (local):', d.toString())
    console.log('- Data UTC:', utcTime.toString())
    console.log('- ISO String:', utcTime.toISOString())
    console.log('- Timezone offset:', d.getTimezoneOffset())
    console.log('- É horário noturno?', HH < 6)
    
    return utcTime.toISOString()
  }

  function validate(): boolean {
    console.log('🔍 Starting validation...')
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
    if (!data.informacaoSuplementar.pob || data.informacaoSuplementar.pob < 1 || data.informacaoSuplementar.pob > 999) e.pob = 'Informe pessoas a bordo (1-999)'
    const eetMin = parseHHmmToMinutes(data.tempoDeVooPrevisto)
    const autMin = parseHHmmToMinutes(data.informacaoSuplementar.autonomia)
    if (!isNaN(eetMin) && !isNaN(autMin) && eetMin > autMin) e.tempoDeVooPrevisto = 'EET não pode exceder a autonomia informada'

    // Hora partida conforme modo
    const minAhead = data.modo === 'PVC' ? 45 : 15
    console.log('🕐 Validating departure time...')
    console.log('- Mode:', data.modo, 'Min ahead:', minAhead)
    console.log('- horaLocal:', horaLocal)
    console.log('- DOF:', data.outrasInformacoes.dof)
    
    const iso = buildIsoFromDofAndHora()
    console.log('- ISO generated:', iso)
    
    if (!iso) {
      console.log('❌ No ISO generated')
      e.horaPartida = 'Data/hora inválida'
    } else {
      const isFuture = isFutureAtLeastMinutes(iso, minAhead)
      console.log('- Is future?', isFuture)
      if (!isFuture) {
        e.horaPartida = `Hora de partida deve ser maior ou igual hora atual (ZULU) + ${minAhead} min`
      }
    }

    // OutrasInformações
    if (!data.outrasInformacoes.opr) e.opr = 'OPR/ é obrigatório'
    if (!/^[A-Z]{4}$/.test(data.outrasInformacoes.from)) e.from = 'FROM/ deve ser ICAO 4 letras'
    if (!/^\d{6}$/.test(data.outrasInformacoes.dof)) e.dof = 'DOF/ deve ser ddmmaa'
    if (data.modo === 'PVC' && (!data.outrasInformacoes.per || data.outrasInformacoes.per.length === 0)) e.per = 'PER/ é obrigatório no PVC'
    const rmkVal = (data.outrasInformacoes.rmk || '').trim()
    if (!rmkVal) e.rmk = 'RMK/ é obrigatório'
    else if (/\bREA\b/i.test(data.rota) && !/\bREA\b/.test(rmkVal)) e.rmk = 'RMK/ deve conter REA e corredores quando rota tiver REA'

    // Informação Suplementar
    if (!data.informacaoSuplementar.corEMarcaAeronave) e.corEMarcaAeronave = 'Cor e Marca da Aeronave é obrigatório'
    if (!data.informacaoSuplementar.pilotoEmComando) e.pilotoEmComando = 'Piloto em Comando é obrigatório'
    if (!data.informacaoSuplementar.anacPrimeiroPiloto) e.anacPrimeiroPiloto = 'Cód. ANAC 1º Piloto é obrigatório'
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
        // Verificação de voo noturno removida - usando apenas dados AISWEB em tempo real
        setAlertaNoturno('Voo noturno detectado - verifique condições no AISWEB')
      }
    } catch {
      // manter silencioso (dados provisórios)
    }
  }

  React.useEffect(() => { verificarNoturno() }, [data.aerodromoDeAlternativa, data.horaPartida, data.tempoDeVooPrevisto, data.outrasInformacoes.dof])

  // Função para transformar FplForm em PlanoDeVooDTO
  function transformToPlanoDeVooDTO(formData: any) {
    // Converter equipamentos para array de strings
    let equipamentos = []
    if (Array.isArray(formData.equipamentoCapacidadeDaAeronave)) {
      equipamentos = formData.equipamentoCapacidadeDaAeronave
    } else if (formData.equipamentoCapacidadeDaAeronave) {
      for (const [key, value] of Object.entries(formData.equipamentoCapacidadeDaAeronave)) {
        if (value) equipamentos.push(key)
      }
    }
    
    // Converter vigilância para array de strings
    let vigilancia = []
    if (Array.isArray(formData.vigilancia)) {
      vigilancia = formData.vigilancia
    } else if (formData.vigilancia) {
      for (const [key, value] of Object.entries(formData.vigilancia)) {
        if (value) vigilancia.push(key)
      }
    }
    
    return {
      identificacaoDaAeronave: formData.identificacaoDaAeronave,
      indicativoDeChamada: formData.indicativoDeChamada,
      regraDeVooEnum: formData.regraDeVooEnum,
      tipoDeVooEnum: formData.tipoDeVooEnum,
      numeroDeAeronaves: formData.numeroDeAeronaves || 1,
      tipoDeAeronave: formData.tipoDeAeronave,
      categoriaEsteiraTurbulenciaEnum: formData.categoriaEsteiraTurbulenciaEnum,
      equipamentoCapacidadeDaAeronave: equipamentos,
      vigilancia: vigilancia,
      aerodromoDePartida: formData.aerodromoDePartida,
      horaPartida: formData.horaPartida,
      aerodromoDeDestino: formData.aerodromoDeDestino,
      tempoDeVooPrevisto: formData.tempoDeVooPrevisto,
      aerodromoDeAlternativa: formData.aerodromoDeAlternativa,
      aerodromoDeAlternativaSegundo: formData.aerodromoDeAlternativaSegundo,
      velocidadeDeCruzeiro: formData.velocidadeDeCruzeiro,
      nivelDeVoo: formData.nivelDeVoo,
      rota: formData.rota,
      outrasInformacoes: {
        sts: formData.outrasInformacoes?.sts || '',
        pbn: formData.outrasInformacoes?.pbn || '',
        nav: formData.outrasInformacoes?.nav || '',
        com: formData.outrasInformacoes?.com || '',
        dat: formData.outrasInformacoes?.dat || '',
        sur: formData.outrasInformacoes?.sur || '',
        dep: formData.outrasInformacoes?.dep || '',
        dest: formData.outrasInformacoes?.dest || '',
        reg: formData.outrasInformacoes?.reg || '',
        eet: formData.outrasInformacoes?.eet || '',
        sel: formData.outrasInformacoes?.sel || '',
        typ: formData.outrasInformacoes?.typ || '',
        code: formData.outrasInformacoes?.code || '',
        dle: formData.outrasInformacoes?.dle || '',
        opr: formData.outrasInformacoes?.opr || '',
        orgn: formData.outrasInformacoes?.orgn || '',
        per: formData.outrasInformacoes?.per || [],
        altn: formData.outrasInformacoes?.altn || '',
        ralt: formData.outrasInformacoes?.ralt || '',
        talt: formData.outrasInformacoes?.talt || '',
        rif: formData.outrasInformacoes?.rif || '',
        rmk: formData.outrasInformacoes?.rmk || '',
        from: formData.outrasInformacoes?.from || '',
        dof: formData.outrasInformacoes?.dof || ''
      },
      informacaoSuplementar: {
        autonomia: formData.informacaoSuplementar?.autonomia || '0000',
        pob: formData.informacaoSuplementar?.pob || 1,
        radioEmergencia: Array.isArray(formData.informacaoSuplementar?.radioEmergencia) ? 
          formData.informacaoSuplementar.radioEmergencia : 
          (formData.informacaoSuplementar?.radioEmergencia ? 
            Object.keys(formData.informacaoSuplementar.radioEmergencia).filter(k => formData.informacaoSuplementar.radioEmergencia[k]) : []),
        sobrevivencia: Array.isArray(formData.informacaoSuplementar?.sobrevivencia) ? 
          formData.informacaoSuplementar.sobrevivencia : 
          (formData.informacaoSuplementar?.sobrevivencia ? 
            Object.keys(formData.informacaoSuplementar.sobrevivencia).filter(k => formData.informacaoSuplementar.sobrevivencia[k]) : []),
        coletes: Array.isArray(formData.informacaoSuplementar?.coletes) ? 
          formData.informacaoSuplementar.coletes : 
          (formData.informacaoSuplementar?.coletes ? 
            Object.keys(formData.informacaoSuplementar.coletes).filter(k => formData.informacaoSuplementar.coletes[k]) : []),
        botes: formData.informacaoSuplementar?.botes || null,
        corEMarcaAeronave: formData.informacaoSuplementar?.corEMarcaAeronave || formData.informacaoSuplementar?.infoAdicionais?.corMarcaANV || '',
        observacoes: formData.informacaoSuplementar?.observacoes || formData.informacaoSuplementar?.infoAdicionais?.observacoes || '',
        pilotoEmComando: formData.informacaoSuplementar?.pilotoEmComando || formData.informacaoSuplementar?.infoAdicionais?.pilotoEmComando || '',
        anacPrimeiroPiloto: formData.informacaoSuplementar?.anacPrimeiroPiloto || formData.informacaoSuplementar?.infoAdicionais?.codAnac1 || '',
        anacSegundoPiloto: formData.informacaoSuplementar?.anacSegundoPiloto || formData.informacaoSuplementar?.infoAdicionais?.codAnac2 || '',
        telefone: formData.informacaoSuplementar?.telefone || formData.informacaoSuplementar?.infoAdicionais?.telefone || '',
        n: formData.informacaoSuplementar?.n || formData.informacaoSuplementar?.infoAdicionais?.n || false
      },
      modo: formData.modo
    }
  }

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault()
    console.log('=== FORM SUBMISSION DEBUG ===')
    console.log('Form submitted, validating...')
    console.log('Current form data:', data)
    console.log('Current horaLocal:', horaLocal)
    console.log('Current DOF:', data.outrasInformacoes.dof)
    
    const isValid = validate()
    console.log('Validation result:', isValid)
    console.log('Current errors:', errors)
    
    if (!isValid) {
      console.log('❌ Validation failed, not submitting')
      console.log('Errors found:', errors)
      return
    }
    
    console.log('✅ Validation passed, submitting...')
    setSubmitting(true)
    try {
      const iso = buildIsoFromDofAndHora()
      if (iso) {
        data.horaPartida = iso
      } else {
        console.warn('Não foi possível construir ISO date, usando data atual')
        data.horaPartida = new Date().toISOString()
      }
      // Garantir DOF em ddMMaa no envio
      const dofVal = data.outrasInformacoes.dof || ''
      if (dofVal.includes('-')) {
        setOutras('dof', ymdToDdmmaa(dofVal))
      }
      // Transformar dados para o formato esperado pelo backend
      console.log('Dados do formulário antes da transformação:', data)
      const transformedData = transformToPlanoDeVooDTO(data)
      console.log('Dados transformados para envio:', transformedData)
      const res = await api.post('/api/v1/flightplans', transformedData)
      const id = res?.data?.id
      if (id) {
        navigate(`/flightplan/${id}`)
      } else {
        // Se não há ID (persistência falhou), redireciona para detalhes com dados temporários
        alert('FPL enviado com sucesso! Redirecionando para detalhes...')
        // Criar um ID temporário baseado no timestamp
        const tempId = `temp-${Date.now()}`
        // Salvar dados temporariamente no localStorage para exibição
        localStorage.setItem(`flightplan-${tempId}`, JSON.stringify(transformedData))
        navigate(`/flightplan/${tempId}`)
      }
    } catch (err: any) {
      console.error('Erro ao enviar FPL:', err)
      console.error('Response data:', err?.response?.data)
      console.error('Response status:', err?.response?.status)
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
              <label className="font-medium">Tipo do Plano</label>
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
                placeholder="ex.: PTOSP" />
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
                placeholder="ex.: BL8" />
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
              <label className="font-medium">Hora de Partida (ZULU)*</label>
              <input type="time" className="input" value={horaLocal} onChange={e => setHoraLocal(e.target.value)} placeholder="ex.: HH:mm" />
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
              <label className="font-medium">2º Aeródromo Alternativo</label>
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
              <textarea className="input" rows={3} value={data.rota} onChange={e => set('rota', e.target.value)} placeholder={perfilConsulta==='VFR' ? 'ex.: REA' : 'ex.: 2239S04620W/N0100A060 DCT 2235S04718W/N0100A060 REA 2243S04734W/N0100A020 DCT 2255S04748W/N0100A020'} />
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
              <input type="number" min={0} max={359} className="input" value={rumo ?? ''} onChange={e => setRumo(e.target.value === '' ? undefined : Math.max(0, Math.min(359, parseInt(e.target.value, 10))))} placeholder="ex.: 000 a 359" />
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
            {outrasSelecionadas.includes('STS') && (
              <div key="STS" className="grid gap-1">
                <label className="font-medium">STS/</label>
                <input className="input" value={data.outrasInformacoes.sts || ''} onChange={e => setOutras('sts', e.target.value)} placeholder="ex.: ALTRV" />
              </div>
            )}
            {outrasSelecionadas.includes('PBN') && (
              <div key="PBN" className="grid gap-1">
                <label className="font-medium">PBN/</label>
                <input className="input" value={data.outrasInformacoes.pbn || ''} onChange={e => setOutras('pbn', e.target.value)} placeholder="ex.: A1B1C1D1L1O1S1T1" />
              </div>
            )}
            {outrasSelecionadas.includes('NAV') && (
              <div key="NAV" className="grid gap-1">
                <label className="font-medium">NAV/</label>
                <input className="input" value={data.outrasInformacoes.nav || ''} onChange={e => setOutras('nav', e.target.value)} placeholder="ex.: GNSS" />
              </div>
            )}
            {outrasSelecionadas.includes('COM') && (
              <div key="COM" className="grid gap-1">
                <label className="font-medium">COM/</label>
                <input className="input" value={data.outrasInformacoes.com || ''} onChange={e => setOutras('com', e.target.value)} placeholder="ex.: VHF" />
              </div>
            )}
            {outrasSelecionadas.includes('DAT') && (
              <div key="DAT" className="grid gap-1">
                <label className="font-medium">DAT/</label>
                <input className="input" value={data.outrasInformacoes.dat || ''} onChange={e => setOutras('dat', e.target.value)} placeholder="ex.: CPDLC" />
              </div>
            )}
            {outrasSelecionadas.includes('SUR') && (
              <div key="SUR" className="grid gap-1">
                <label className="font-medium">SUR/</label>
                <input className="input" value={data.outrasInformacoes.sur || ''} onChange={e => setOutras('sur', e.target.value)} placeholder="ex.: 400B" />
              </div>
            )}
            {outrasSelecionadas.includes('DEP') && (
              <div key="DEP" className="grid gap-1">
                <label className="font-medium">DEP/</label>
                <input className="input" value={data.outrasInformacoes.dep || ''} onChange={e => setOutras('dep', e.target.value)} placeholder="ex.: SBMT" />
              </div>
            )}
            {outrasSelecionadas.includes('DEST') && (
              <div key="DEST" className="grid gap-1">
                <label className="font-medium">DEST/</label>
                <input className="input" value={data.outrasInformacoes.dest || ''} onChange={e => setOutras('dest', e.target.value)} placeholder="ex.: SBMT" />
              </div>
            )}
            {outrasSelecionadas.includes('REG') && (
              <div key="REG" className="grid gap-1">
                <label className="font-medium">REG/</label>
                <input className="input" value={data.outrasInformacoes.reg || ''} onChange={e => setOutras('reg', e.target.value)} placeholder="ex.: PT-OSP" />
              </div>
            )}
            {outrasSelecionadas.includes('EET') && (
              <div key="EET" className="grid gap-1">
                <label className="font-medium">EET/ (FIR + tempo)</label>
                <input className="input" value={data.outrasInformacoes.eet || ''} onChange={e => setOutras('eet', e.target.value)} placeholder="ex.: SBC0005 SBBS0150" />
              </div>
            )}
            {outrasSelecionadas.includes('SEL') && (
              <div key="SEL" className="grid gap-1">
                <label className="font-medium">SEL/</label>
                <input className="input" value={data.outrasInformacoes.sel || ''} onChange={e => setOutras('sel', e.target.value)} placeholder="ex.: SELCAL" />
              </div>
            )}
            {outrasSelecionadas.includes('TYP') && (
              <div key="TYP" className="grid gap-1">
                <label className="font-medium">TYP/</label>
                <input className="input" value={data.outrasInformacoes.typ || ''} onChange={e => setOutras('typ', e.target.value)} placeholder="ex.: BL8" />
              </div>
            )}
            {outrasSelecionadas.includes('CODE') && (
              <div key="CODE" className="grid gap-1">
                <label className="font-medium">CODE/</label>
                <input className="input" value={data.outrasInformacoes.code || ''} onChange={e => setOutras('code', e.target.value)} placeholder="ex.: 1234" />
              </div>
            )}
            {outrasSelecionadas.includes('DLE') && (
              <div key="DLE" className="grid gap-1">
                <label className="font-medium">DLE/</label>
                <input className="input" value={data.outrasInformacoes.dle || ''} onChange={e => setOutras('dle', e.target.value)} placeholder="ex.: SBMT0030" />
              </div>
            )}
            {outrasSelecionadas.includes('OPR') && (
              <div key="OPR" className="grid gap-1">
                <label className="font-medium">OPR/*</label>
                <input className="input" value={data.outrasInformacoes.opr} onChange={e => setOutras('opr', e.target.value)} placeholder="ex.: Eduardo Bregaida" />
                {errors.opr && <p className="text-red-600 text-sm">{errors.opr}</p>}
              </div>
            )}
            {outrasSelecionadas.includes('ORGN') && (
              <div key="ORGN" className="grid gap-1">
                <label className="font-medium">ORGN/</label>
                <input className="input" value={data.outrasInformacoes.orgn || ''} onChange={e => setOutras('orgn', e.target.value)} placeholder="ex.: SBMT" />
              </div>
            )}
            {outrasSelecionadas.includes('FROM') && (
              <div key="FROM" className="grid gap-1">
                <label className="font-medium">FROM/*</label>
                <input className="input" value={data.outrasInformacoes.from} onInput={uppercaseField} onChange={e => setOutras('from', e.target.value.toUpperCase())} placeholder="ex.: SBMT" />
                {errors.from && <p className="text-red-600 text-sm">{errors.from}</p>}
              </div>
            )}
            {outrasSelecionadas.includes('ALTN') && (
              <div key="ALTN" className="grid gap-1">
                <label className="font-medium">ALTN/</label>
                <input className="input" value={data.outrasInformacoes.altn || ''} onChange={e => setOutras('altn', e.target.value)} placeholder="ex.: SBJD" />
              </div>
            )}
            {outrasSelecionadas.includes('RALT') && (
              <div key="RALT" className="grid gap-1">
                <label className="font-medium">RALT/</label>
                <input className="input" value={data.outrasInformacoes.ralt || ''} onChange={e => setOutras('ralt', e.target.value)} placeholder="ex.: SBJD" />
              </div>
            )}
            {outrasSelecionadas.includes('TALT') && (
              <div key="TALT" className="grid gap-1">
                <label className="font-medium">TALT/</label>
                <input className="input" value={data.outrasInformacoes.talt || ''} onChange={e => setOutras('talt', e.target.value)} placeholder="ex.: SBJD" />
              </div>
            )}
            {outrasSelecionadas.includes('RIF') && (
              <div key="RIF" className="grid gap-1">
                <label className="font-medium">RIF/</label>
                <input className="input" value={data.outrasInformacoes.rif || ''} onChange={e => setOutras('rif', e.target.value)} placeholder="ex.: SBMT SBJD" />
              </div>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {outrasSelecionadas.includes('PER') && (
              <div key="PER" className="grid gap-1">
                <label className="font-medium">PER/ (PVC)</label>
                <select className="input" value={data.outrasInformacoes.per?.[0] || ''} onChange={e => setOutras('per', e.target.value ? [e.target.value] : [])}>
                  <option value="">Selecione uma categoria</option>
                  <option value="A">A - Categoria A (&lt;91kt)</option>
                  <option value="B">B - Categoria B (91-121kt)</option>
                  <option value="C">C - Categoria C (121-141kt)</option>
                  <option value="D">D - Categoria D (141-166kt)</option>
                  <option value="E">E - Categoria E (166-211kt)</option>
                  <option value="H">H - Helicópteros</option>
                </select>
                {errors.per && <p className="text-red-600 text-sm">{errors.per}</p>}
              </div>
            )}
            {outrasSelecionadas.includes('RMK') && (
              <div className="grid gap-1">
                <label className="font-medium">RMK/*</label>
                <input className="input" value={data.outrasInformacoes.rmk || ''} onChange={e => setOutras('rmk', e.target.value)} placeholder="ex.: REA DELTA ECHO LIMA ALT MAX REA" />
                {errors.rmk && <p className="text-red-600 text-sm">{errors.rmk}</p>}
              </div>
            )}
            {outrasSelecionadas.includes('DOF') && (
              <div className="grid gap-1">
                <label className="font-medium">DOF/*</label>
                <input
                  type="date"
                  className="input"
                  min={ymdToday()}
                  value={ddmmaaToYmd(data.outrasInformacoes.dof)}
                  onChange={e => setOutras('dof', ymdToDdmmaa(e.target.value))}
                />
                {errors.dof && <p className="text-red-600 text-sm">{errors.dof}</p>}
              </div>
            )}
          </div>

        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-suplementar">Campo 19 - Informações Suplementares</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Autonomia*</label>
              <input className="input" value={data.informacaoSuplementar.autonomia} onChange={e => setInfoSup('autonomia', e.target.value)} placeholder="ex.: 0100" />
              {errors.autonomia && <p className="text-red-600 text-sm">{errors.autonomia}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Pessoas a bordo*</label>
              <input type="number" min={1} max={999} className="input" value={data.informacaoSuplementar.pob || ''} onChange={e => setInfoSup('pob', e.target.value === '' ? 1 : Math.max(1, Math.min(999, parseInt(e.target.value, 10))))} placeholder="ex.: 2" />
              {errors.pob && <p className="text-red-600 text-sm">{errors.pob}</p>}
            </div>
            <CheckboxGroup name="radioEmg" label={<><span>Equipamento Rádio Emergência*</span></>} options={[{value:'U',label:'U',tooltip:'UHF'},{value:'V',label:'V',tooltip:'VHF'},{value:'E',label:'E',tooltip:'ELT'}]} values={data.informacaoSuplementar.radioEmergencia || []} onChange={vals => setInfoSup('radioEmergencia', vals)} />
            <div className="grid gap-1">
              <label className="font-medium">Equipamento de Sobrevivência*</label>
              <label className="inline-flex items-center gap-2">
                <input type="checkbox" className="accent-indigo-600" checked={(data.informacaoSuplementar.sobrevivencia||[]).length>0} onChange={e => setInfoSup('sobrevivencia', e.target.checked ? ['S'] : [])} />
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
              </label>
              {(data.informacaoSuplementar.coletes||[]).includes('J') && (
                <CheckboxGroup name="coletesExtras" label={<><span>Opções</span></>} options={[{value:'L',label:'L',tooltip:'Luz'},{value:'F',label:'F',tooltip:'Fluorescente'},{value:'U',label:'U',tooltip:'UHF'},{value:'V',label:'V',tooltip:'VHF'}]} values={(data.informacaoSuplementar.coletes||[]).filter(v=>v!=='J')} onChange={vals => setInfoSup('coletes', ['J', ...vals])} />
              )}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Botes*<sup title="Informar número, capacidade, abrigo (C) e cor se Sim" className="text-slate-500 ml-1">¹</sup></label>
              <label className="inline-flex items-center gap-2">
                <input type="checkbox" className="accent-indigo-600" checked={!!data.informacaoSuplementar.botes?.possui} onChange={e => setInfoSup('botes', { ...(data.informacaoSuplementar.botes||{possui:false}), possui: e.target.checked })} />
              </label>
            </div>
          </div>
          {data.informacaoSuplementar.botes?.possui && (
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="grid gap-1">
                <label className="font-medium">Botes — Número</label>
                <input type="number" min={1} className="input" value={data.informacaoSuplementar.botes?.numero ?? ''} onChange={e => {
                  const val = e.target.value === '' ? undefined : Math.max(1, parseInt(e.target.value, 10));
                  setInfoSup('botes', { ...(data.informacaoSuplementar.botes||{possui:true}), numero: val as any })
                }} placeholder="ex.: 1"/>
              </div>
              <div className="grid gap-1">
                <label className="font-medium">Botes — Capacidade</label>
                <input type="number" min={1} className="input" value={data.informacaoSuplementar.botes?.capacidade ?? ''} onChange={e => {
                  const val = e.target.value === '' ? undefined : Math.max(1, parseInt(e.target.value, 10));
                  setInfoSup('botes', { ...(data.informacaoSuplementar.botes||{possui:true}), capacidade: val as any })
                }} placeholder="ex.: 1"/>
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
                <input className="input" value={data.informacaoSuplementar.botes?.cor || ''} onChange={e => setInfoSup('botes', { ...(data.informacaoSuplementar.botes||{possui:true}), cor: e.target.value })} placeholder="ex.: LARANJA" />
              </div>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Cor e Marca da Aeronave*</label>
              <input className="input" value={data.informacaoSuplementar.corEMarcaAeronave} onChange={e => setInfoSup('corEMarcaAeronave', e.target.value)} placeholder="ex.: BRANCO E VERMELHO COM ESTRELAS BRANCAS"/>
              {errors.corEMarcaAeronave && <p className="text-red-600 text-sm">{errors.corEMarcaAeronave}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">N<sup title="Se selecionado, Observações tornam-se obrigatórias" className="text-slate-500 ml-1">¹</sup></label>
              <div className="flex items-center gap-6">
                <label className="inline-flex items-center gap-2">
                  <input type="checkbox" className="accent-indigo-600" checked={!!data.informacaoSuplementar.n} onChange={e => setInfoSup('n', e.target.checked)} />
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
              <input className="input" value={data.informacaoSuplementar.pilotoEmComando} onChange={e => setInfoSup('pilotoEmComando', e.target.value)} placeholder="ex.: Bregaida" />
              {errors.pilotoEmComando && <p className="text-red-600 text-sm">{errors.pilotoEmComando}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Cód. ANAC 1º Piloto*</label>
              <input className="input" value={data.informacaoSuplementar.anacPrimeiroPiloto} onChange={e => setInfoSup('anacPrimeiroPiloto', e.target.value)} placeholder="ex.: 177762" />
              {errors.anacPrimeiroPiloto && <p className="text-red-600 text-sm">{errors.anacPrimeiroPiloto}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Cód. ANAC 2º Piloto</label>
              <input className="input" value={data.informacaoSuplementar.anacSegundoPiloto || ''} onChange={e => setInfoSup('anacSegundoPiloto', e.target.value)} />
              {errors.anacSegundoPiloto && <p className="text-red-600 text-sm">{errors.anacSegundoPiloto}</p>}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Telefone*</label>
              <input className="input" value={data.informacaoSuplementar.telefone} onChange={e => setInfoSup('telefone', e.target.value.replace(/\D/g, ''))} placeholder="ex.: 11985586633" />
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


