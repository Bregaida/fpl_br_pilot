import React from 'react'
import { useNavigate, useLocation, useParams } from 'react-router-dom'
import RadioGroup from '../components/RadioGroup'
import CheckboxGroup from '../components/CheckboxGroup'
import { PlanoDeVooDTO } from '../types/PlanoDeVooDTO'
import { nowUtcISO, todayDdmmaaUtc, parseHHmmToMinutes, isFutureAtLeastMinutes } from '../utils/time'
import { getSunInfo, isNoturnoPorJanela } from '../services/SunService'
import { apiClient } from '../services/api'

const EQUIP_OPCOES = ['S','R','G','O','D','N','V','I']
const VIG_OPCOES = ['A','C','S','X','B1']

export default function PlanoDeVooClone() {
  const navigate = useNavigate()
  const location = useLocation()
  const { id } = useParams()
  
  // Buscar dados do plano de voo clonado (vem via state ou busca por ID)
  const locationState = location.state as { cloneData?: any, isClone?: boolean } | null
  const cloneData = locationState?.cloneData || null
  
  
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
    perfilConsulta: 'VFR' as 'VFR' | 'IFR',
    rumoMagnetico: '',
    outrasInformacoes: {
      opr: '',
      from: '',
      dof: todayDdmmaaUtc(), // SEMPRE data atual
      rmk: '',
      eet: '',
      per: [],
      sts: '',
      pbn: '',
      nav: '',
      com: '',
      dat: '',
      sur: '',
      dep: '',
      dest: '',
      reg: '',
      sel: '',
      typ: '',
      code: '',
      dle: '',
      orgn: '',
      altn: '',
      ralt: '',
      talt: '',
      rif: ''
    },
    informacaoSuplementar: {
      autonomia: '',
      pob: 1,
      corEMarcaAeronave: '',
      pilotoEmComando: '',
      anacPrimeiroPiloto: '',
      telefone: '',
      radioEmergencia: [],
      sobrevivencia: [],
      coletes: [],
      botes: { 
        possui: false, 
        numero: 1, 
        capacidade: 1, 
        c: false, 
        cor: '' 
      },
      n: false,
      observacoes: '',
      anacSegundoPiloto: ''
    },
    modo: 'PVS'
  })

  const [horaLocal, setHoraLocal] = React.useState<string>('') // HH:mm
  const [errors, setErrors] = React.useState<Record<string,string>>({})
  const [alertaNoturno, setAlertaNoturno] = React.useState<string>('')
  const [submitting, setSubmitting] = React.useState(false)
  const [loading, setLoading] = React.useState(false)
  const [outrasSelecionadas, setOutrasSelecionadas] = React.useState<string[]>([])

  // Garantir que DOF seja sempre a data atual
  React.useEffect(() => {
    const dataAtual = todayDdmmaaUtc()
    console.log('🔄 Verificando DOF - Atual:', dataAtual, 'No estado:', data.outrasInformacoes?.dof)
    if (data.outrasInformacoes?.dof !== dataAtual) {
      console.log('🔄 Atualizando DOF para data atual:', dataAtual)
      setOutras('dof', dataAtual)
    }
  }, [data.outrasInformacoes?.dof])

  // Forçar DOF para data atual ao montar o componente
  React.useEffect(() => {
    const dataAtual = todayDdmmaaUtc()
    console.log('🔄 Inicializando DOF com data atual:', dataAtual)
    setOutras('dof', dataAtual)
  }, [])

  // Forçar DOF para data atual sempre que o componente renderizar
  React.useEffect(() => {
    const dataAtual = todayDdmmaaUtc()
    console.log('🔄 Forçando DOF para data atual:', dataAtual)
    console.log('🔄 Tipo da data atual:', typeof dataAtual)
    console.log('🔄 Data atual completa:', dataAtual)
    setOutras('dof', dataAtual)
  })

  // Definir campos obrigatórios baseado no modo (DOF é fixo, não aparece como checkbox)
  React.useEffect(() => {
    const camposObrigatorios = data.modo === 'PVC' 
      ? ['OPR', 'FROM', 'RMK', 'PER', 'EET']
      : ['OPR', 'FROM', 'RMK']
    
    console.log('🔄 useEffect - Modo alterado:', data.modo)
    console.log('🔄 Campos obrigatórios:', camposObrigatorios)
    console.log('🔄 outrasSelecionadas será definido como:', camposObrigatorios)
    
    setOutrasSelecionadas(camposObrigatorios)
  }, [data.modo])

  // Buscar dados do plano de voo se não vieram via state
  React.useEffect(() => {
    const loadData = (sourceData: any) => {
      // Extrair payload se necessário
      let payload = sourceData
      if (sourceData?.payload) {
        payload = sourceData.payload
        // Se o payload tem um payload interno, usar ele
        if (payload.payload) {
          payload = payload.payload
        }
      }
      
      // Criar novos dados com valores padrão seguros
      const newData = {
        identificacaoDaAeronave: payload?.identificacaoDaAeronave || '',
        indicativoDeChamada: payload?.indicativoDeChamada || false,
        regraDeVooEnum: payload?.regraDeVooEnum || 'V',
        tipoDeVooEnum: payload?.tipoDeVooEnum || 'G',
        numeroDeAeronaves: payload?.numeroDeAeronaves || 1,
        tipoDeAeronave: payload?.tipoDeAeronave || '',
        categoriaEsteiraTurbulenciaEnum: payload?.categoriaEsteiraTurbulenciaEnum || 'L',
        equipamentoCapacidadeDaAeronave: payload?.equipamentoCapacidadeDaAeronave || ['S'],
        vigilancia: payload?.vigilancia || ['A'],
        aerodromoDePartida: payload?.aerodromoDePartida || '',
        horaPartida: nowUtcISO(), // Nova hora para clonagem
        aerodromoDeDestino: payload?.aerodromoDeDestino || '',
        tempoDeVooPrevisto: payload?.tempoDeVooPrevisto || '',
        aerodromoDeAlternativa: payload?.aerodromoDeAlternativa || '',
        aerodromoDeAlternativaSegundo: payload?.aerodromoDeAlternativaSegundo || '',
        velocidadeDeCruzeiro: payload?.velocidadeDeCruzeiro || '',
        nivelDeVoo: payload?.nivelDeVoo || '',
        rota: payload?.rota || '',
        perfilConsulta: payload?.perfilConsulta || 'VFR',
        rumoMagnetico: payload?.rumoMagnetico || '',
        outrasInformacoes: {
          opr: payload?.outrasInformacoes?.opr || '',
          from: payload?.outrasInformacoes?.from || '',
          dof: todayDdmmaaUtc(), // SEMPRE data atual para clonagem
          rmk: payload?.outrasInformacoes?.rmk || '',
          eet: payload?.outrasInformacoes?.eet || '',
          per: payload?.outrasInformacoes?.per || [],
          sts: payload?.outrasInformacoes?.sts || '',
          pbn: payload?.outrasInformacoes?.pbn || '',
          nav: payload?.outrasInformacoes?.nav || '',
          com: payload?.outrasInformacoes?.com || '',
          dat: payload?.outrasInformacoes?.dat || '',
          sur: payload?.outrasInformacoes?.sur || '',
          dep: payload?.outrasInformacoes?.dep || '',
          dest: payload?.outrasInformacoes?.dest || '',
          reg: payload?.outrasInformacoes?.reg || '',
          sel: payload?.outrasInformacoes?.sel || '',
          typ: payload?.outrasInformacoes?.typ || '',
          code: payload?.outrasInformacoes?.code || '',
          dle: payload?.outrasInformacoes?.dle || '',
          orgn: payload?.outrasInformacoes?.orgn || '',
          altn: payload?.outrasInformacoes?.altn || '',
          ralt: payload?.outrasInformacoes?.ralt || '',
          talt: payload?.outrasInformacoes?.talt || '',
          rif: payload?.outrasInformacoes?.rif || ''
        },
        informacaoSuplementar: {
          autonomia: payload?.informacaoSuplementar?.autonomia || '',
          pob: payload?.informacaoSuplementar?.pob || 1,
          corEMarcaAeronave: payload?.informacaoSuplementar?.corEMarcaAeronave || '',
          pilotoEmComando: payload?.informacaoSuplementar?.pilotoEmComando || '',
          anacPrimeiroPiloto: payload?.informacaoSuplementar?.anacPrimeiroPiloto || '',
          telefone: payload?.informacaoSuplementar?.telefone || '',
          radioEmergencia: payload?.informacaoSuplementar?.radioEmergencia || [],
          sobrevivencia: payload?.informacaoSuplementar?.sobrevivencia || [],
          coletes: payload?.informacaoSuplementar?.coletes || [],
          botes: payload?.informacaoSuplementar?.botes || { 
            possui: false, 
            numero: 1, 
            capacidade: 1, 
            c: false, 
            cor: '' 
          },
          n: payload?.informacaoSuplementar?.n || false,
          observacoes: payload?.informacaoSuplementar?.observacoes || '',
          anacSegundoPiloto: payload?.informacaoSuplementar?.anacSegundoPiloto || ''
        },
        modo: payload?.modo || 'PVS'
      }
      
      // Forçar DOF para data atual antes de definir os dados
      newData.outrasInformacoes.dof = todayDdmmaaUtc()
      console.log('🔄 Forçando DOF na clonagem para:', newData.outrasInformacoes.dof)
      
      setData(newData)
      
      // Carregar campos selecionados das outras informações baseado no modo
      const camposComValor = Object.keys(newData.outrasInformacoes || {}).filter(key => 
        newData.outrasInformacoes?.[key as keyof typeof newData.outrasInformacoes] && 
        newData.outrasInformacoes?.[key as keyof typeof newData.outrasInformacoes] !== '' &&
        newData.outrasInformacoes?.[key as keyof typeof newData.outrasInformacoes] !== null &&
        !['opr', 'from', 'rmk', 'dof'].includes(key)
      )
      
      // Definir campos obrigatórios baseado no modo (DOF é fixo, não aparece como checkbox)
      const camposObrigatorios = newData.modo === 'PVC' 
        ? ['OPR', 'FROM', 'RMK', 'PER', 'EET']
        : ['OPR', 'FROM', 'RMK']
      
      // Combinar campos obrigatórios com campos que têm valor
      const todosCampos = [...camposObrigatorios, ...camposComValor.map(campo => campo.toUpperCase())]
      const camposUnicos = [...new Set(todosCampos)] // Remove duplicatas
      
      console.log('🔍 Debug Campo 18:')
      console.log('- Modo:', newData.modo)
      console.log('- Campos obrigatórios:', camposObrigatorios)
      console.log('- Campos com valor:', camposComValor)
      console.log('- Todos os campos:', camposUnicos)
      console.log('- outrasSelecionadas será definido como:', camposUnicos)
      
      setOutrasSelecionadas(camposUnicos)
    }
    
    if (!cloneData && id) {
      setLoading(true)
      apiClient.get(`/api/v1/flightplans/${id}`)
        .then((response: any) => {
          loadData(response)
        })
        .catch(error => {
          console.error('Erro ao buscar dados do plano de voo:', error)
          alert('Erro ao carregar dados do plano de voo')
        })
        .finally(() => {
          setLoading(false)
        })
    } else if (cloneData) {
      loadData(cloneData)
    }
  }, [id, cloneData])

  // Monitorar quando os dados são carregados
  React.useEffect(() => {
    // Dados carregados com sucesso
  }, [data])

  function set<K extends keyof PlanoDeVooDTO>(key: K, value: PlanoDeVooDTO[K]) {
    setData(prev => {
      const newData = { ...prev, [key]: value }
      return newData
    })
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
    if (!ddmmaa || ddmmaa.length !== 6) return ymdToday()
    const dd = ddmmaa.substring(0, 2)
    const mm = ddmmaa.substring(2, 4)
    const aa = ddmmaa.substring(4, 6)
    const yyyy = 2000 + parseInt(aa, 10)
    return `${yyyy}-${mm}-${dd}`
  }


  function buildIsoFromDofAndHora(): string | null {
    const dof = data.outrasInformacoes?.dof
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
    
    return utcTime.toISOString()
  }

  // Converter horaPartida ISO para horaLocal HH:mm
  React.useEffect(() => {
    if (data.horaPartida) {
      try {
        const date = new Date(data.horaPartida)
        const hours = date.getUTCHours().toString().padStart(2, '0')
        const minutes = date.getUTCMinutes().toString().padStart(2, '0')
        setHoraLocal(`${hours}:${minutes}`)
      } catch (e) {
        console.warn('Erro ao converter horaPartida para horaLocal:', e)
      }
    }
  }, [data.horaPartida])

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
    if (!data.informacaoSuplementar?.autonomia || isNaN(parseHHmmToMinutes(data.informacaoSuplementar?.autonomia))) e.autonomia = 'Informe autonomia'
    if (!data.informacaoSuplementar?.pob || data.informacaoSuplementar?.pob < 1 || data.informacaoSuplementar?.pob > 999) e.pob = 'Informe pessoas a bordo (1-999)'
    const eetMin = parseHHmmToMinutes(data.tempoDeVooPrevisto)
    const autMin = parseHHmmToMinutes(data.informacaoSuplementar?.autonomia)
    if (!isNaN(eetMin) && !isNaN(autMin) && eetMin > autMin) e.tempoDeVooPrevisto = 'EET não pode exceder a autonomia informada'

    // Hora partida conforme modo
    const minAhead = data.modo === 'PVC' ? 45 : 15
    console.log('🕐 Validating departure time...')
    console.log('- Mode:', data.modo, 'Min ahead:', minAhead)
    console.log('- horaLocal:', horaLocal)
    console.log('- DOF:', data.outrasInformacoes?.dof)
    
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
    if (!data.outrasInformacoes?.opr) e.opr = 'OPR/ é obrigatório'
    if (!/^[A-Z]{4}$/.test(data.outrasInformacoes?.from)) e.from = 'FROM/ deve ser ICAO 4 letras'
    if (!/^\d{6}$/.test(data.outrasInformacoes?.dof)) e.dof = 'DOF/ deve ser ddmmaa'
    if (data.modo === 'PVC' && (!data.outrasInformacoes?.per || data.outrasInformacoes?.per.length === 0)) e.per = 'PER/ é obrigatório no PVC'
    const rmkVal = (data.outrasInformacoes?.rmk || '').trim()
    if (!rmkVal) e.rmk = 'RMK/ é obrigatório'
    else if (/\bREA\b/i.test(data.rota) && !/\bREA\b/.test(rmkVal)) e.rmk = 'RMK/ deve conter REA e corredores quando rota tiver REA'

    // Informação Suplementar
    if (!data.informacaoSuplementar?.corEMarcaAeronave) e.corEMarcaAeronave = 'Cor e Marca da Aeronave é obrigatório'
    if (!data.informacaoSuplementar?.pilotoEmComando) e.pilotoEmComando = 'Piloto em Comando é obrigatório'
    if (!data.informacaoSuplementar?.anacPrimeiroPiloto) e.anacPrimeiroPiloto = 'Cód. ANAC 1º Piloto é obrigatório'
    if (!/^\d{10,11}$/.test(data.informacaoSuplementar?.telefone)) e.telefone = 'Informe um telefone válido (somente números)'
    if (!data.informacaoSuplementar?.radioEmergencia || (data.informacaoSuplementar?.radioEmergencia.length === 0)) e.radioEmergencia = 'Selecione ao menos um equipamento de rádio de emergência'
    if (data.informacaoSuplementar?.n && !(data.informacaoSuplementar?.observacoes && data.informacaoSuplementar?.observacoes.trim().length>0)) e.observacoes = 'Observações tornam-se obrigatórias quando N = sim'

    setErrors(e)
    return Object.keys(e).length === 0
  }

  async function verificarNoturno() {
    setAlertaNoturno('')
    try {
      const sun = await getSunInfo(data.aerodromoDeAlternativa, data.outrasInformacoes?.dof)
      const noturno = isNoturnoPorJanela(data.horaPartida, data.tempoDeVooPrevisto, sun)
      if (noturno) {
        // Verificação de voo noturno removida - usando apenas dados AISWEB em tempo real
        setAlertaNoturno('Voo noturno detectado - verifique condições na Administração do Aeródromo')
      }
    } catch {
      // manter silencioso (dados provisórios)
    }
  }

  React.useEffect(() => { verificarNoturno() }, [data.aerodromoDeAlternativa, data.horaPartida, data.tempoDeVooPrevisto, data.outrasInformacoes?.dof])

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    console.log('=== FORM SUBMISSION DEBUG ===')
    console.log('Form submitted, validating...')
    console.log('Current form data:', data)
    console.log('Current horaLocal:', horaLocal)
    console.log('Current DOF:', data.outrasInformacoes?.dof)
    
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
      // Garantir DOF em formato correto no envio
      const dofVal = data.outrasInformacoes?.dof || ''
      if (dofVal.includes('-')) {
        // Converter de YYYY-MM-DD para DDMMYY
        const date = new Date(dofVal)
        const dd = String(date.getDate()).padStart(2, '0')
        const mm = String(date.getMonth() + 1).padStart(2, '0')
        const yy = String(date.getFullYear()).slice(-2)
        setOutras('dof', `${dd}${mm}${yy}`)
      }
      
      console.log('Dados do formulário antes da transformação:', data)
      
      const res = await apiClient.post('/api/v1/flightplans', data) as { id: string }
      const id = res?.id
      if (id) {
        navigate(`/flightplan/${id}`)
      } else {
        alert('FPL clonado e enviado com sucesso! Redirecionando para detalhes...')
        // Criar um ID temporário baseado no timestamp
        const tempId = `temp-${Date.now()}`
        // Salvar dados temporariamente no localStorage para exibição
        localStorage.setItem(`flightplan-${tempId}`, JSON.stringify(data))
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

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-indigo-600 mx-auto"></div>
          <p className="mt-4 text-lg text-slate-600">Carregando dados para clonagem...</p>
        </div>
      </div>
    )
  }


  return (
    <section className="grid gap-6 p-4">
      <header className="space-y-1">
        <div className="flex items-center gap-3">
          <h1 className="text-3xl font-semibold tracking-tight">Clonar Plano de Voo</h1>
          <span className="px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm font-medium">
            📋 Clonando
          </span>
        </div>
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

      <form onSubmit={handleSubmit} className="grid gap-6">
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
              <input className="input" value={data.identificacaoDaAeronave || ''}
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
              <input className="input" value={data.aerodromoDePartida || ''} onInput={uppercaseField} onChange={e => set('aerodromoDePartida', e.target.value.toUpperCase())} placeholder="ex.: SBMT" />
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
              <input className="input" value={data.aerodromoDeDestino || ''} onInput={uppercaseField} onChange={e => set('aerodromoDeDestino', e.target.value.toUpperCase())} placeholder="ex.: SBMT" />
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
              <input className="input" value={data.aerodromoDeAlternativaSegundo || ''} onInput={uppercaseField} onChange={e => set('aerodromoDeAlternativaSegundo', e.target.value.toUpperCase() || '')} placeholder="ex.: SBxx (opcional)" />
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
              <textarea className="input" rows={3} value={data.rota} onChange={e => set('rota', e.target.value)} placeholder="ex.: REA ou 2239S04620W/N0100A060 DCT 2235S04718W/N0100A060 REA 2243S04734W/N0100A020 DCT 2255S04748W/N0100A020" />
              {errors.rota && <p className="text-red-600 text-sm">{errors.rota}</p>}
            </div>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Perfil de Consulta</label>
              <div className="flex items-center gap-3">
                <label className="inline-flex items-center gap-2"><input type="radio" className="accent-indigo-600" checked={data.perfilConsulta==='VFR'} onChange={()=>set('perfilConsulta','VFR')} />VFR</label>
                <label className="inline-flex items-center gap-2"><input type="radio" className="accent-indigo-600" checked={data.perfilConsulta==='IFR'} onChange={()=>set('perfilConsulta','IFR')} />IFR</label>
              </div>
              <label className="font-medium">Rumo Magnético</label>
              <input type="number" min={0} max={359} className="input" value={data.rumoMagnetico || ''} onChange={e => set('rumoMagnetico', e.target.value)} placeholder="ex.: 000 a 359" />
            </div>
            <div className="md:col-span-2">
              {/* Sugestões de FL estáticas - sempre visíveis */}
              <div className="p-4 bg-blue-50 rounded-lg">
                <h3 className="font-medium text-blue-900 mb-2">Sugestões de FL com base no rumo e regra de voo:</h3>
                <div className="grid grid-cols-4 md:grid-cols-6 gap-2">
                  {data.perfilConsulta === 'VFR' ? (
                    // VFR: FL ímpares (000-179)
                    <>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 035</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 055</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 075</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 095</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 115</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 135</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 155</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 175</div>
                    </>
                  ) : (
                    // IFR: FL pares (180-359)
                    <>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 045</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 065</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 085</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 105</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 125</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 145</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 165</div>
                      <div className="px-3 py-2 bg-blue-100 text-blue-800 rounded text-sm text-center">FL 185</div>
                    </>
                  )}
                </div>
                <p className="text-sm text-blue-700 mt-2">
                  {data.perfilConsulta === 'VFR' 
                    ? 'VFR: Use FL ímpares para rumos de 000° a 179°' 
                    : 'IFR: Use FL pares para rumos de 180° a 359°'
                  }
                </p>
              </div>
            </div>
          </div>
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-outros">Campo 18 - Outras Informações</h2>
          
          {/* Seleção de campos adicionais */}
          <div className="mb-4">
            <label className="font-medium mb-2 block">Selecionar campos adicionais:</label>
            <div className="grid grid-cols-3 md:grid-cols-6 gap-2">
              {['OPR', 'FROM', 'RMK', 'STS', 'PBN', 'NAV', 'COM', 'DAT', 'SUR', 'DEP', 'DEST', 'REG', 'EET', 'SEL', 'TYP', 'CODE', 'DLE', 'ORGN', 'PER', 'ALTN', 'RALT', 'TALT', 'RIF'].map(field => {
                const isChecked = outrasSelecionadas.includes(field)
                const isObrigatorio = ['OPR', 'FROM', 'RMK', 'PER', 'EET'].includes(field)
                console.log(`🔍 Campo ${field}: checked=${isChecked}, obrigatorio=${isObrigatorio}`)
                
                return (
                <label key={field} className="inline-flex items-center gap-2 text-sm">
                  <input 
                    type="checkbox" 
                    checked={isChecked}
                    onChange={e => {
                      // Campos obrigatórios não podem ser desmarcados (DOF é fixo, não aparece como checkbox)
                      const camposObrigatorios = data.modo === 'PVC' 
                        ? ['OPR', 'FROM', 'RMK', 'PER', 'EET']
                        : ['OPR', 'FROM', 'RMK']
                      
                      if (camposObrigatorios.includes(field) && !e.target.checked) {
                        // Não permitir desmarcar campos obrigatórios
                        return
                      }
                      
                      if (e.target.checked) {
                        setOutrasSelecionadas(prev => [...prev, field])
                      } else {
                        setOutrasSelecionadas(prev => prev.filter(f => f !== field))
                      }
                    }}
                    className="w-4 h-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500"
                  />
                  <span className={['OPR', 'FROM', 'RMK', 'PER', 'EET'].includes(field) ? 'font-semibold text-blue-700' : ''}>{field}</span>
                </label>
                )
              })}
            </div>
          </div>

          {/* Campo DOF fixo */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">DOF/* (Data atual)</label>
              <input
                type="date"
                className="input bg-gray-100"
                value={ddmmaaToYmd(data.outrasInformacoes?.dof)}
                readOnly
                title="Data atual - não pode ser alterada"
                onChange={() => {}} // Prevenir alterações
              />
              <p className="text-xs text-gray-500">Valor atual: {data.outrasInformacoes?.dof || 'vazio'}</p>
              {errors.dof && <p className="text-red-600 text-sm">{errors.dof}</p>}
            </div>
          </div>

          {/* Campos selecionados */}
          {outrasSelecionadas.length > 0 && (
            <div className="mt-4 p-4 bg-gray-50 rounded-lg">
              <h3 className="font-medium text-gray-900 mb-3">Campos selecionados:</h3>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                {outrasSelecionadas.map(field => (
                  <div key={field} className="grid gap-1">
                    <label className="font-medium">{field}*</label>
                    <input 
                      className="input" 
                      value={data.outrasInformacoes?.[field.toLowerCase() as keyof typeof data.outrasInformacoes] || ''} 
                      onChange={e => setOutras(field.toLowerCase() as keyof typeof data.outrasInformacoes, e.target.value)} 
                      placeholder={`ex.: ${field}`}
                    />
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
          <h2 className="text-lg font-semibold" id="bloco-suplementar">Campo 19 - Informações Suplementares</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Autonomia*</label>
              <input className="input" value={data.informacaoSuplementar?.autonomia || ''} onChange={e => setInfoSup('autonomia', e.target.value)} placeholder="ex.: 0100" />
              {errors.autonomia && <p className="text-red-600 text-sm">{errors.autonomia}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Pessoas a bordo*</label>
              <input type="number" min={1} max={999} className="input" value={data.informacaoSuplementar?.pob || ''} onChange={e => setInfoSup('pob', e.target.value === '' ? 1 : Math.max(1, Math.min(999, parseInt(e.target.value, 10))))} placeholder="ex.: 2" />
              {errors.pob && <p className="text-red-600 text-sm">{errors.pob}</p>}
            </div>
            <CheckboxGroup name="radioEmg" label={<><span>Equipamento Rádio Emergência*</span></>} options={[{value:'U',label:'U',tooltip:'UHF'},{value:'V',label:'V',tooltip:'VHF'},{value:'E',label:'E',tooltip:'ELT'}]} values={data.informacaoSuplementar?.radioEmergencia || []} onChange={vals => setInfoSup('radioEmergencia', vals)} />
            <div className="grid gap-1">
              <label className="font-medium">Equipamento de Sobrevivência*</label>
              <label className="inline-flex items-center gap-2">
                <input type="checkbox" className="accent-indigo-600" checked={(data.informacaoSuplementar?.sobrevivencia||[]).length>0} onChange={e => setInfoSup('sobrevivencia', e.target.checked ? ['S'] : [])} />
              </label>
              {(data.informacaoSuplementar?.sobrevivencia||[]).length>0 && (
                <CheckboxGroup name="sobrevExtra" label={<><span>Opções</span></>} options={[
                  {value:'S',label:'S',tooltip:'Equipamentos de sobrevivência'},
                  {value:'P',label:'P',tooltip:'Polar'},
                  {value:'D',label:'D',tooltip:'Deserto'},
                  {value:'M',label:'M',tooltip:'Marítimo'},
                  {value:'J',label:'J',tooltip:'Selva'}
                ]} values={data.informacaoSuplementar?.sobrevivencia || []} onChange={vals => setInfoSup('sobrevivencia', vals)} />
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Coletes*</label>
              <label className="inline-flex items-center gap-2">
                <input type="checkbox" className="accent-indigo-600" checked={(data.informacaoSuplementar?.coletes||[]).includes('J')} onChange={e => {
                  const on = e.target.checked; const rest = (data.informacaoSuplementar?.coletes||[]).filter(x=>x!=='J'); setInfoSup('coletes', on ? ['J', ...rest] : rest)
                }} />
              </label>
              {(data.informacaoSuplementar?.coletes||[]).includes('J') && (
                <CheckboxGroup name="coletesExtras" label={<><span>Opções</span></>} options={[{value:'L',label:'L',tooltip:'Luz'},{value:'F',label:'F',tooltip:'Fluorescente'},{value:'U',label:'U',tooltip:'UHF'},{value:'V',label:'V',tooltip:'VHF'}]} values={(data.informacaoSuplementar?.coletes||[]).filter(v=>v!=='J')} onChange={vals => setInfoSup('coletes', ['J', ...vals])} />
              )}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Botes*<sup title="Informar número, capacidade, abrigo (C) e cor se Sim" className="text-slate-500 ml-1">¹</sup></label>
              <label className="inline-flex items-center gap-2">
                <input type="checkbox" className="accent-indigo-600" checked={!!data.informacaoSuplementar?.botes?.possui} onChange={e => setInfoSup('botes', { ...(data.informacaoSuplementar?.botes||{possui:false}), possui: e.target.checked })} />
              </label>
            </div>
          </div>
          {data.informacaoSuplementar?.botes?.possui && (
            <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="grid gap-1">
                <label className="font-medium">Botes — Número</label>
                <input type="number" min={1} className="input" value={data.informacaoSuplementar?.botes?.numero || 1} onChange={e => {
                  const val = e.target.value === '' ? 1 : Math.max(1, parseInt(e.target.value, 10));
                  setInfoSup('botes', { ...(data.informacaoSuplementar?.botes||{possui:true}), numero: val })
                }} placeholder="ex.: 1"/>
              </div>
              <div className="grid gap-1">
                <label className="font-medium">Botes — Capacidade</label>
                <input type="number" min={1} className="input" value={data.informacaoSuplementar?.botes?.capacidade || 1} onChange={e => {
                  const val = e.target.value === '' ? 1 : Math.max(1, parseInt(e.target.value, 10));
                  setInfoSup('botes', { ...(data.informacaoSuplementar?.botes||{possui:true}), capacidade: val })
                }} placeholder="ex.: 1"/>
              </div>
              <div className="grid gap-1">
                <label className="font-medium">Botes — Abrigo (C)</label>
                <select className="input" value={data.informacaoSuplementar?.botes?.c ? 'true' : 'false'} onChange={e => setInfoSup('botes', { ...(data.informacaoSuplementar?.botes||{possui:true}), c: e.target.value === 'true' })}>
                  <option value="false">Não</option>
                  <option value="true">Sim</option>
                </select>
              </div>
              <div className="grid gap-1">
                <label className="font-medium">Botes — Cor</label>
                <input className="input" value={data.informacaoSuplementar?.botes?.cor || ''} onChange={e => setInfoSup('botes', { ...(data.informacaoSuplementar?.botes||{possui:true}), cor: e.target.value })} placeholder="ex.: LARANJA" />
              </div>
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Cor e Marca da Aeronave*</label>
              <input className="input" value={data.informacaoSuplementar?.corEMarcaAeronave || ''} onChange={e => setInfoSup('corEMarcaAeronave', e.target.value)} placeholder="ex.: BRANCO E VERMELHO COM ESTRELAS BRANCAS"/>
              {errors.corEMarcaAeronave && <p className="text-red-600 text-sm">{errors.corEMarcaAeronave}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">N<sup title="Se selecionado, Observações tornam-se obrigatórias" className="text-slate-500 ml-1">¹</sup></label>
              <div className="flex items-center gap-6">
                <label className="inline-flex items-center gap-2">
                  <input type="checkbox" className="accent-indigo-600" checked={!!data.informacaoSuplementar?.n} onChange={e => setInfoSup('n', e.target.checked)} />
                </label>
              </div>
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Observações</label>
              <input className="input" value={data.informacaoSuplementar?.observacoes || ''} onChange={e => setInfoSup('observacoes', e.target.value)} />
              {errors.observacoes && <p className="text-red-600 text-sm">{errors.observacoes}</p>}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Piloto em Comando*</label>
              <input className="input" value={data.informacaoSuplementar?.pilotoEmComando || ''} onChange={e => setInfoSup('pilotoEmComando', e.target.value)} placeholder="ex.: Bregaida" />
              {errors.pilotoEmComando && <p className="text-red-600 text-sm">{errors.pilotoEmComando}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Cód. ANAC 1º Piloto*</label>
              <input className="input" value={data.informacaoSuplementar?.anacPrimeiroPiloto || ''} onChange={e => setInfoSup('anacPrimeiroPiloto', e.target.value)} placeholder="ex.: 177762" />
              {errors.anacPrimeiroPiloto && <p className="text-red-600 text-sm">{errors.anacPrimeiroPiloto}</p>}
            </div>
            <div className="grid gap-1">
              <label className="font-medium">Cód. ANAC 2º Piloto</label>
              <input className="input" value={data.informacaoSuplementar?.anacSegundoPiloto || ''} onChange={e => setInfoSup('anacSegundoPiloto', e.target.value)} placeholder="ex.: 177762 (opcional)" />
              {errors.anacSegundoPiloto && <p className="text-red-600 text-sm">{errors.anacSegundoPiloto}</p>}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="grid gap-1">
              <label className="font-medium">Telefone*</label>
              <input className="input" value={data.informacaoSuplementar?.telefone || ''} onChange={e => setInfoSup('telefone', e.target.value.replace(/\D/g, ''))} placeholder="ex.: 11985586633" />
              {errors.telefone && <p className="text-red-600 text-sm">{errors.telefone}</p>}
            </div>
            <div />
            <div />
          </div>
        </div>

        <div className="flex items-center gap-3">
          <button type="submit" className="px-4 py-2 rounded-xl bg-indigo-600 text-white" disabled={submitting}>{submitting ? 'Clonando...' : 'Clonar e Salvar'}</button>
        </div>
      </form>
    </section>
  )
}
