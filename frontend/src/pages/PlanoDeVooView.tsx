import React from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { FlightplanAPI } from '../services/api'
import MiniMap from '../components/MiniMap'


export default function PlanoDeVooView() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = React.useState(true)
  const [erro, setErro] = React.useState<string>('')
  const [data, setData] = React.useState<any>(null)
  const [activeTab, setActiveTab] = React.useState(0)
  
  const [aiswebFullData, setAiswebFullData] = React.useState<{[key: string]: any}>({})
  const [aiswebLoading, setAiswebLoading] = React.useState<{[key: string]: boolean}>({})
  const [expandedObservations, setExpandedObservations] = React.useState<{[key: string]: boolean}>({})
  const [expandedNotams, setExpandedNotams] = React.useState<{[key: string]: boolean}>({})
  const [expandedInfotempSection, setExpandedInfotempSection] = React.useState<boolean>(false)
  const [expandedNotamsSection, setExpandedNotamsSection] = React.useState<boolean>(true)
  const [suplementosFirData, setSuplementosFirData] = React.useState<{[key: string]: any}>({})
  const [suplementosFirLoading, setSuplementosFirLoading] = React.useState<{[key: string]: boolean}>({})
  const [expandedSupplementosSection, setExpandedSupplementosSection] = React.useState<boolean>(true)
  const [expandedFirCards, setExpandedFirCards] = React.useState<{[key: string]: boolean}>({})
  const [expandedSuplementoItems, setExpandedSuplementoItems] = React.useState<{[key: string]: boolean}>({})
  const [expandedObservationsSection, setExpandedObservationsSection] = React.useState<boolean>(true)
  const [expandedCartasCards, setExpandedCartasCards] = React.useState<{[key: string]: boolean}>({})

  // Função para copiar para clipboard
  const copyToClipboard = async (text: string) => {
    try {
      await navigator.clipboard.writeText(text)
      // Você pode adicionar uma notificação de sucesso aqui
      alert('PLN copiado para a área de transferência!')
    } catch (err) {
      console.error('Erro ao copiar: ', err)
      alert('Erro ao copiar para a área de transferência')
    }
  }

  // Função para baixar FPL como arquivo .txt
  const downloadFPL = (fplText: string, registration: string, dof: string) => {
    const filename = `FPL_${registration || 'UNKNOWN'}_${dof || 'UNKNOWN'}.txt`
    const blob = new Blob([fplText], { type: 'text/plain' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
  }

  // Função para buscar dados completos do AISWEB
  const fetchAiswebFullData = async (icao: string) => {
    if (!icao || aiswebFullData[icao] || aiswebLoading[icao]) return
    
    console.log(`[DEBUG] Iniciando busca AISWEB para ${icao}`)
    setAiswebLoading(prev => ({ ...prev, [icao]: true }))
    try {
      const response = await FlightplanAPI.getAiswebFull(icao)
      console.log(`[DEBUG] Resposta AISWEB para ${icao}:`, response.data)
      setAiswebFullData(prev => ({ ...prev, [icao]: response.data }))
    } catch (error) {
      console.error(`Erro ao buscar dados completos AISWEB para ${icao}:`, error)
      setAiswebFullData(prev => ({ ...prev, [icao]: null }))
    } finally {
      setAiswebLoading(prev => ({ ...prev, [icao]: false }))
    }
  }

  const fetchSupplementosFir = async (icao: string) => {
    if (!icao || suplementosFirData[icao] || suplementosFirLoading[icao]) return
    
    console.log(`[DEBUG] Iniciando busca Suplementos FIR para ${icao}`)
    setSuplementosFirLoading(prev => ({ ...prev, [icao]: true }))
    try {
      const response = await fetch(`/api/aisweb/suplementos/${icao}`)
      if (response.ok) {
        const data = await response.json()
        console.log(`[DEBUG] Resposta Suplementos FIR para ${icao}:`, data)
        setSuplementosFirData(prev => ({ ...prev, [icao]: data }))
      } else {
        console.warn(`Erro ao buscar suplementos para ${icao}: ${response.status}`)
        setSuplementosFirData(prev => ({ ...prev, [icao]: null }))
      }
    } catch (error) {
      console.error(`Erro ao buscar suplementos FIR para ${icao}:`, error)
      setSuplementosFirData(prev => ({ ...prev, [icao]: null }))
    } finally {
      setSuplementosFirLoading(prev => ({ ...prev, [icao]: false }))
    }
  }

  // Função para converter coordenadas decimais para HMS
  const convertToHMS = (decimal: number, isLatitude: boolean) => {
    const abs = Math.abs(decimal)
    const degrees = Math.floor(abs)
    const minutes = Math.floor((abs - degrees) * 60)
    const seconds = ((abs - degrees) * 60 - minutes) * 60
    
    let direction: string
    if (isLatitude) {
      direction = decimal >= 0 ? 'N' : 'S'
    } else {
      direction = decimal >= 0 ? 'E' : 'W'
    }
    
    return `${degrees}°${minutes.toString().padStart(2, '0')}'${seconds.toFixed(2).padStart(5, '0')}"${direction}`
  }

  // Funções de toggle para collapse
  const toggleFirCard = (firCode: string) => {
    setExpandedFirCards(prev => ({
      ...prev,
      [firCode]: !prev[firCode]
    }))
  }

  const toggleSuplementoItem = (itemId: string) => {
    setExpandedSuplementoItems(prev => ({
      ...prev,
      [itemId]: !prev[itemId]
    }))
  }

  const toggleCartasCard = (cartaId: string) => {
    setExpandedCartasCards(prev => ({
      ...prev,
      [cartaId]: !prev[cartaId]
    }))
  }

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
        
        // Buscar dados do backend com PLN codificado
        const res = await FlightplanAPI.getSubmissionView(id as string)
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

  // Carregar suplementos das FIRs
  React.useEffect(() => {
    const firs = ['SBCW', 'SBAO', 'SBAZ', 'SBRE', 'SBBS']
    firs.forEach(fir => {
      fetchSupplementosFir(fir)
    })
  }, [])

  // Buscar dados completos AISWEB quando os dados do plano de voo forem carregados
  React.useEffect(() => {
    if (!data) return
    
    const payload = data?.payload || data || {}
    const aerodromos = [
      payload.aerodromoDePartida,
      payload.aerodromoDeDestino,
      payload.aerodromoDeAlternativa,
      payload.aerodromoDeAlternativaSegundo
    ].filter(Boolean)
    
    aerodromos.forEach(icao => {
      if (icao && !aiswebFullData[icao] && !aiswebLoading[icao]) {
        fetchAiswebFullData(icao)
      }
    })
  }, [data])

  if (loading) return <div className="p-6">Carregando...</div>
  if (erro) return (
    <div className="p-6">
      <p className="text-red-600 mb-4">{erro}</p>
      <button className="px-4 py-2 rounded-xl bg-indigo-600 text-white" onClick={() => navigate('/flightplan/novo')}>Novo FPL</button>
    </div>
  )
  const payload = data?.payload || data || {}
  console.log('Payload na página de detalhes:', payload)
  console.log('equipamentoCapacidadeDaAeronave:', payload.equipamentoCapacidadeDaAeronave)
  console.log('vigilancia:', payload.vigilancia)
  console.log('plnCodificado:', data?.plnCodificado)

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

      {/* Painel Principal - Detalhes do Plano de Voo */}
      <div className="bg-gradient-to-br from-slate-50 to-gray-100 p-6 rounded-3xl border border-slate-200 shadow-xl">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-12 h-12 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-2xl flex items-center justify-center">
            <span className="text-white text-2xl">📋</span>
          </div>
          <div>
            <h1 className="text-2xl font-bold text-slate-800">Detalhes do Plano de Voo</h1>
            <p className="text-slate-600">Informações completas do flight plan</p>
          </div>
      </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {/* Campo 7 - Identificação da Aeronave */}
          <div className="bg-gradient-to-br from-blue-50 to-indigo-50 p-4 rounded-xl border border-blue-200 shadow-md">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-8 h-8 bg-blue-500 rounded-lg flex items-center justify-center">
                <span className="text-white text-sm">✈️</span>
              </div>
              <div>
                <h3 className="text-sm font-bold text-blue-800">Campo 7</h3>
                <p className="text-xs text-blue-600">Identificação da Aeronave</p>
              </div>
            </div>
            <div className="space-y-2">
              <div className="bg-white p-3 rounded-lg border border-blue-100 shadow-sm">
                <div className="text-xs text-blue-600 font-medium mb-1">Identificação</div>
                <div className="text-sm font-bold text-blue-800">{payload.identificacaoDaAeronave}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-blue-100 shadow-sm">
                <div className="text-xs text-blue-600 font-medium mb-1">Indicativo</div>
                <div className="text-sm font-bold text-blue-800">{payload.indicativoDeChamada ? 'Sim' : 'Não'}</div>
              </div>
            </div>
      </div>

          {/* Campo 8 - Regras de Voo e Tipo de Voo */}
          <div className="bg-gradient-to-br from-green-50 to-emerald-50 p-4 rounded-xl border border-green-200 shadow-md">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-8 h-8 bg-green-500 rounded-lg flex items-center justify-center">
                <span className="text-white text-sm">📋</span>
              </div>
              <div>
                <h3 className="text-sm font-bold text-green-800">Campo 8</h3>
                <p className="text-xs text-green-600">Regras e Tipo de Voo</p>
              </div>
            </div>
            <div className="space-y-2">
              <div className="bg-white p-3 rounded-lg border border-green-100 shadow-sm">
                <div className="text-xs text-green-600 font-medium mb-1">Regra de Voo</div>
                <div className="text-sm font-bold text-green-800">{payload.regraDeVooEnum}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-green-100 shadow-sm">
                <div className="text-xs text-green-600 font-medium mb-1">Tipo de Voo</div>
                <div className="text-sm font-bold text-green-800">{payload.tipoDeVooEnum}</div>
              </div>
            </div>
      </div>

          {/* Campo 9 - Informações da Aeronave */}
          <div className="bg-gradient-to-br from-purple-50 to-violet-50 p-4 rounded-xl border border-purple-200 shadow-md">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-8 h-8 bg-purple-500 rounded-lg flex items-center justify-center">
                <span className="text-white text-sm">🛩️</span>
          </div>
              <div>
                <h3 className="text-sm font-bold text-purple-800">Campo 9</h3>
                <p className="text-xs text-purple-600">Informações da Aeronave</p>
          </div>
        </div>
            <div className="space-y-2">
              <div className="bg-white p-3 rounded-lg border border-purple-100 shadow-sm">
                <div className="text-xs text-purple-600 font-medium mb-1">Número de Aeronaves</div>
                <div className="text-sm font-bold text-purple-800">{payload.numeroDeAeronaves}</div>
          </div>
              <div className="bg-white p-3 rounded-lg border border-purple-100 shadow-sm">
                <div className="text-xs text-purple-600 font-medium mb-1">Tipo de Aeronave</div>
                <div className="text-sm font-bold text-purple-800">{payload.tipoDeAeronave}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-purple-100 shadow-sm">
                <div className="text-xs text-purple-600 font-medium mb-1">Esteira de Turbulância</div>
                <div className="text-sm font-bold text-purple-800">{payload.categoriaEsteiraTurbulenciaEnum}</div>
          </div>
        </div>
      </div>

          {/* Campo 10 - Equipamentos e Vigilância */}
          <div className="bg-gradient-to-br from-orange-50 to-amber-50 p-4 rounded-xl border border-orange-200 shadow-md">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-8 h-8 bg-orange-500 rounded-lg flex items-center justify-center">
                <span className="text-white text-sm">📡</span>
              </div>
              <div>
                <h3 className="text-sm font-bold text-orange-800">Campo 10</h3>
                <p className="text-xs text-orange-600">Equipamentos e Vigilância</p>
              </div>
            </div>
            <div className="space-y-2">
              <div className="bg-white p-3 rounded-lg border border-orange-100 shadow-sm">
                <div className="text-xs text-orange-600 font-medium mb-1">Rádio Comunicação</div>
                <div className="text-sm font-bold text-orange-800">{(payload.equipamentoCapacidadeDaAeronave||[]).join(', ') || 'N/A'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-orange-100 shadow-sm">
                <div className="text-xs text-orange-600 font-medium mb-1">Vigilância</div>
                <div className="text-sm font-bold text-orange-800">{(payload.vigilancia||[]).join(', ') || 'N/A'}</div>
              </div>
            </div>
      </div>

          {/* Campo 13 - Informações de Partida */}
          <div className="bg-gradient-to-br from-red-50 to-rose-50 p-4 rounded-xl border border-red-200 shadow-md">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-8 h-8 bg-red-500 rounded-lg flex items-center justify-center">
                <span className="text-white text-sm">🚀</span>
              </div>
              <div>
                <h3 className="text-sm font-bold text-red-800">Campo 13</h3>
                <p className="text-xs text-red-600">Informações de Partida</p>
              </div>
            </div>
            <div className="space-y-2">
              <div className="bg-white p-3 rounded-lg border border-red-100 shadow-sm">
                <div className="text-xs text-red-600 font-medium mb-1">Aeródromo de Partida</div>
                <div className="text-sm font-bold text-red-800">{payload.aerodromoDePartida}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-red-100 shadow-sm">
                <div className="text-xs text-red-600 font-medium mb-1">Hora de Partida (ZULU)</div>
                <div className="text-sm font-bold text-red-800">{formatTimeWithLocal(payload.horaPartida)}</div>
              </div>
            </div>
      </div>

          {/* Campo 15 - Informações de Rota */}
          <div className="bg-gradient-to-br from-cyan-50 to-blue-50 p-4 rounded-xl border border-cyan-200 shadow-md">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-8 h-8 bg-cyan-500 rounded-lg flex items-center justify-center">
                <span className="text-white text-sm">🛣️</span>
              </div>
              <div>
                <h3 className="text-sm font-bold text-cyan-800">Campo 15</h3>
                <p className="text-xs text-cyan-600">Informações de Rota</p>
              </div>
            </div>
            <div className="space-y-2">
              <div className="bg-white p-3 rounded-lg border border-cyan-100 shadow-sm">
                <div className="text-xs text-cyan-600 font-medium mb-1">Velocidade de Cruzeiro</div>
                <div className="text-sm font-bold text-cyan-800">{payload.velocidadeDeCruzeiro}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-cyan-100 shadow-sm">
                <div className="text-xs text-cyan-600 font-medium mb-1">Nível de Voo</div>
                <div className="text-sm font-bold text-cyan-800">{payload.nivelDeVoo}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-cyan-100 shadow-sm">
                <div className="text-xs text-cyan-600 font-medium mb-1">Rota</div>
                <div className="text-sm font-bold text-cyan-800">{payload.rota}</div>
              </div>
            </div>
      </div>

          {/* Campo 16 - Destino e Alternativas */}
          <div className="bg-gradient-to-br from-emerald-50 to-teal-50 p-4 rounded-xl border border-emerald-200 shadow-md">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-8 h-8 bg-emerald-500 rounded-lg flex items-center justify-center">
                <span className="text-white text-sm">🎯</span>
              </div>
              <div>
                <h3 className="text-sm font-bold text-emerald-800">Campo 16</h3>
                <p className="text-xs text-emerald-600">Destino e Alternativas</p>
              </div>
            </div>
            <div className="space-y-2">
              <div className="bg-white p-3 rounded-lg border border-emerald-100 shadow-sm">
                <div className="text-xs text-emerald-600 font-medium mb-1">Aeródromo de Destino</div>
                <div className="text-sm font-bold text-emerald-800">{payload.aerodromoDeDestino}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-emerald-100 shadow-sm">
                <div className="text-xs text-emerald-600 font-medium mb-1">EET Total</div>
                <div className="text-sm font-bold text-emerald-800">{payload.tempoDeVooPrevisto}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-emerald-100 shadow-sm">
                <div className="text-xs text-emerald-600 font-medium mb-1">Aeródromo Alternativo</div>
                <div className="text-sm font-bold text-emerald-800">{payload.aerodromoDeAlternativa}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-emerald-100 shadow-sm">
                <div className="text-xs text-emerald-600 font-medium mb-1">2º Aeródromo Alternativo</div>
                <div className="text-sm font-bold text-emerald-800">{payload.aerodromoDeAlternativaSegundo}</div>
              </div>
            </div>
          </div>

          {/* Campo 18 - Outras Informações */}
          <div className="bg-gradient-to-br from-indigo-50 to-purple-50 p-4 rounded-xl border border-indigo-200 shadow-md lg:col-span-2">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-8 h-8 bg-indigo-500 rounded-lg flex items-center justify-center">
                <span className="text-white text-sm">📋</span>
              </div>
              <div>
                <h3 className="text-sm font-bold text-indigo-800">Campo 18</h3>
                <p className="text-xs text-indigo-600">Outras Informações</p>
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2">
          {payload.outrasInformacoes?.sts && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">STS/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.sts}</div>
            </div>
          )}
          {payload.outrasInformacoes?.pbn && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">PBN/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.pbn}</div>
            </div>
          )}
          {payload.outrasInformacoes?.nav && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">NAV/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.nav}</div>
            </div>
          )}
          {payload.outrasInformacoes?.com && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">COM/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.com}</div>
            </div>
          )}
          {payload.outrasInformacoes?.dat && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">DAT/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.dat}</div>
            </div>
          )}
          {payload.outrasInformacoes?.sur && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">SUR/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.sur}</div>
            </div>
          )}
          {payload.outrasInformacoes?.dep && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">DEP/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.dep}</div>
            </div>
          )}
          {payload.outrasInformacoes?.dest && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">DEST/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.dest}</div>
            </div>
          )}
          {payload.outrasInformacoes?.reg && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">REG/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.reg}</div>
            </div>
          )}
          {payload.outrasInformacoes?.eet && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">EET/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.eet}</div>
            </div>
          )}
          {payload.outrasInformacoes?.sel && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">SEL/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.sel}</div>
            </div>
          )}
          {payload.outrasInformacoes?.typ && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">TYP/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.typ}</div>
            </div>
          )}
          {payload.outrasInformacoes?.code && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">CODE/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.code}</div>
            </div>
          )}
          {payload.outrasInformacoes?.dle && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">DLE/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.dle}</div>
            </div>
          )}
          {payload.outrasInformacoes?.opr && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">OPR/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.opr}</div>
            </div>
          )}
          {payload.outrasInformacoes?.orgn && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">ORGN/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.orgn}</div>
            </div>
          )}
        {(payload.outrasInformacoes?.per||[]).length ? (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">PER/</div>
              <div className="text-sm font-bold text-indigo-800">{(payload.outrasInformacoes?.per||[]).join(', ')}</div>
            </div>
        ) : null}
          {payload.outrasInformacoes?.altn && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">ALTN/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.altn}</div>
      </div>
          )}
          {payload.outrasInformacoes?.ralt && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">RALT/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.ralt}</div>
            </div>
          )}
          {payload.outrasInformacoes?.talt && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">TALT/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.talt}</div>
            </div>
          )}
          {payload.outrasInformacoes?.rif && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">RIF/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.rif}</div>
            </div>
          )}
          {payload.outrasInformacoes?.rmk && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">RMK/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.rmk}</div>
            </div>
          )}
          {payload.outrasInformacoes?.from && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">FROM/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.from}</div>
            </div>
          )}
          {payload.outrasInformacoes?.dof && (
            <div className="bg-white p-3 rounded-lg border border-indigo-100 shadow-sm">
              <div className="text-xs text-indigo-600 font-medium mb-1">DOF/</div>
              <div className="text-sm font-bold text-indigo-800">{payload.outrasInformacoes.dof}</div>
            </div>
          )}
            </div>
          </div>

          {/* Campo 19 - Informações Suplementares */}
          <div className="bg-gradient-to-br from-pink-50 to-rose-50 p-4 rounded-xl border border-pink-200 shadow-md lg:col-span-3">
            <div className="flex items-center gap-2 mb-3">
              <div className="w-8 h-8 bg-pink-500 rounded-lg flex items-center justify-center">
                <span className="text-white text-sm">👥</span>
              </div>
              <div>
                <h3 className="text-sm font-bold text-pink-800">Campo 19</h3>
                <p className="text-xs text-pink-600">Informações Suplementares</p>
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">Autonomia</div>
                <div className="text-sm font-bold text-pink-800">{payload.informacaoSuplementar?.autonomia || 'N/A'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">Pessoas a bordo</div>
                <div className="text-sm font-bold text-pink-800">{payload.informacaoSuplementar?.pob || 'N/A'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">Equipamento Rádio Emergência</div>
                <div className="text-sm font-bold text-pink-800">{(payload.informacaoSuplementar?.radioEmergencia||[]).join(', ') || 'N/A'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">Equipamento de Sobrevivência</div>
                <div className="text-sm font-bold text-pink-800">{(payload.informacaoSuplementar?.sobrevivencia||[]).length ? (payload.informacaoSuplementar?.sobrevivencia||[]).join(', ') : 'Não'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">Coletes</div>
                <div className="text-sm font-bold text-pink-800">{(payload.informacaoSuplementar?.coletes||[]).length ? (payload.informacaoSuplementar?.coletes||[]).join(', ') : 'Não'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">Botes</div>
                <div className="text-sm font-bold text-pink-800">{payload.informacaoSuplementar?.botes?.possui ? 'Sim' : 'Não'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">Cor e Marca da Aeronave</div>
                <div className="text-sm font-bold text-pink-800">{payload.informacaoSuplementar?.corEMarcaAeronave || 'N/A'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">N</div>
                <div className="text-sm font-bold text-pink-800">{payload.informacaoSuplementar?.n ? 'Sim' : 'Não'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">Piloto em Comando</div>
                <div className="text-sm font-bold text-pink-800">{payload.informacaoSuplementar?.pilotoEmComando || 'N/A'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">Cód. ANAC 1º Piloto</div>
                <div className="text-sm font-bold text-pink-800">{payload.informacaoSuplementar?.anacPrimeiroPiloto || 'N/A'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">Cód. ANAC 2º Piloto</div>
                <div className="text-sm font-bold text-pink-800">{payload.informacaoSuplementar?.anacSegundoPiloto || 'N/A'}</div>
              </div>
              <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm">
                <div className="text-xs text-pink-600 font-medium mb-1">Telefone</div>
                <div className="text-sm font-bold text-pink-800">{payload.informacaoSuplementar?.telefone || 'N/A'}</div>
              </div>
              {payload.informacaoSuplementar?.observacoes && (
                <div className="bg-white p-3 rounded-lg border border-pink-100 shadow-sm md:col-span-2 lg:col-span-3">
                  <div className="text-xs text-pink-600 font-medium mb-1">Observações</div>
                  <div className="text-sm font-bold text-pink-800">{payload.informacaoSuplementar?.observacoes}</div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* Bloco PLN Codificado */}
      {data?.plnCodificado && (
        <div className="card-pln-codificado">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-semibold text-gray-900">PLN Codificado</h2>
            <div className="flex gap-2">
              <button
                onClick={() => copyToClipboard(data.plnCodificado)}
                className="btn-primary-pastel"
                aria-label="Copiar plano de voo codificado"
              >
                📋 Copiar
              </button>
              <button
                onClick={() => downloadFPL(data.plnCodificado, payload.identificacaoDaAeronave, payload.outrasInformacoes?.dof)}
                className="btn-success-pastel"
              >
                💾 Baixar .txt
              </button>
            </div>
          </div>
          <div className="code-block">
            <pre className="code-text">
              {data.plnCodificado}
            </pre>
          </div>
        </div>
      )}

      {/* Painel ROTAER */}
      {data && (
        <div className="rotaer-panel">
          <h2 className="text-2xl font-bold text-gray-900 mb-6 flex items-center">
            🛩️ ROTAER - Informações dos Aeródromos
          </h2>
          
          {(() => {
            const payload = data?.payload || data || {}
            const aerodromos = [
              { icao: payload.aerodromoDePartida, label: 'Aeródromo de Partida' },
              { icao: payload.aerodromoDeDestino, label: 'Aeródromo de Destino' },
              { icao: payload.aerodromoDeAlternativa, label: 'Aeródromo de Alternativa' },
              { icao: payload.aerodromoDeAlternativaSegundo, label: '2º Aeródromo de Alternativa' }
            ].filter(a => a.icao)
            
            if (aerodromos.length === 0) {
              return (
                <div className="text-center py-8">
                  <p className="text-gray-600 text-lg">Nenhum aeródromo encontrado no plano de voo.</p>
                </div>
              )
            }
            
            return (
              <>
                <div className="rotaer-tabs">
                  {aerodromos.map((aerodromo, index) => (
                    <div
                      key={aerodromo.icao}
                      className={`rotaer-tab ${activeTab === index ? 'active' : ''}`}
                      onClick={() => setActiveTab(index)}
                      onKeyDown={(e) => {
                        if (e.key === 'Enter' || e.key === ' ') {
                          e.preventDefault()
                          setActiveTab(index)
                        }
                      }}
                      role="tab"
                      tabIndex={0}
                      aria-selected={activeTab === index}
                    >
                      {aerodromo.icao}
                    </div>
                  ))}
                </div>
                
                <div className="rotaer-content">
                  {(() => {
                    const currentAerodromo = aerodromos[activeTab]
                    const icao = currentAerodromo.icao
                    const aiswebData = aiswebFullData[icao]
                    const aerodromoInfo = aiswebData?.aerodromo
                    const rotaerInfo = aiswebData?.rotaer
                    const meteoInfo = aiswebData?.meteo
                    const metarDecoded = aiswebData?.meteo?.metarDecoded
                    const tafDecoded = aiswebData?.meteo?.tafDecoded
                    
                    
                    
                    
                    const notamInfo = aiswebData?.notam
                    const infotempInfo = aiswebData?.infotemp
                    const isLoading = aiswebLoading[icao]
                    
                    // Debug logs
                    console.log(`[DEBUG] ${icao} - aiswebData:`, aiswebData)
                    console.log(`[DEBUG] ${icao} - notamInfo:`, notamInfo)
                    console.log(`[DEBUG] ${icao} - infotempInfo:`, infotempInfo)
                    
                    if (isLoading) {
                      return (
                        <div className="text-center py-8">
                          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                          <p className="text-gray-600 text-lg">Carregando dados ROTAER para {icao}...</p>
                        </div>
                      )
                    }
                    
                    if (!rotaerInfo) {
                      return (
                        <div className="text-center py-8">
                          <p className="text-gray-600 text-lg">Dados ROTAER não encontrados para {icao}.</p>
                        </div>
                      )
                    }
                    
                    return (
                      <div className="rotaer-grid">
                        {/* Informações Básicas */}
                        <div className="rotaer-section">
                          <h3 className="rotaer-section-title">📋 Informações Básicas</h3>
                          <div className="space-y-3">
                            <div className="rotaer-field">
                              <span className="rotaer-label">ICAO:</span>
                              <span className="rotaer-value font-bold text-blue-600">{aerodromoInfo?.icao || rotaerInfo.icao || icao}</span>
                            </div>
                            <div className="rotaer-field">
                              <span className="rotaer-label">IATA:</span>
                              <span className="rotaer-value font-bold text-green-600">{aerodromoInfo?.iata || rotaerInfo.iata || 'N/A'}</span>
                            </div>
                            <div className="rotaer-field">
                              <span className="rotaer-label">CIAD:</span>
                              <span className="rotaer-value">{rotaerInfo.ciad || 'N/A'}</span>
                            </div>
                            <div className="rotaer-field">
                              <span className="rotaer-label">Nome:</span>
                              <span className="rotaer-value">{rotaerInfo.nome || 'N/A'}</span>
                            </div>
                            <div className="rotaer-field">
                              <span className="rotaer-label">Tipo:</span>
                              <span className="rotaer-value">{rotaerInfo.typeUtil || 'N/A'}</span>
                            </div>
                            <div className="rotaer-field">
                              <span className="rotaer-label">Operação:</span>
                              <span className="rotaer-value">{rotaerInfo.typeOpr || 'N/A'}</span>
                            </div>
                            <div className="rotaer-field">
                              <span className="rotaer-label">Cidade:</span>
                              <span className="rotaer-value">{rotaerInfo.cidade || 'N/A'}</span>
                            </div>
                            <div className="rotaer-field">
                              <span className="rotaer-label">UF:</span>
                              <span className="rotaer-value">{rotaerInfo.uf || 'N/A'}</span>
                            </div>
                            <div className="rotaer-field">
                              <span className="rotaer-label">Operador:</span>
                              <span className="rotaer-value">{rotaerInfo.operadora || rotaerInfo.operador || 'N/A'}</span>
                            </div>
                            <div className="rotaer-field">
                              <span className="rotaer-label">UTC:</span>
                              <span className="rotaer-value">{rotaerInfo.utc || 'N/A'}</span>
                            </div>
                            <div className="rotaer-field">
                              <span className="rotaer-label">Altitude:</span>
                              <span className="rotaer-value">
                                {(() => {
                                  const altitudeM = rotaerInfo.altitude
                                  const altitudeFt = rotaerInfo.altFt
                                  
                                  // Se ambos estão disponíveis, usar os valores originais
                                  if (altitudeM && altitudeFt) {
                                    return (
                                      <>
                                        {altitudeM}m 
                                        <span className="text-gray-500 ml-1">
                                          ({altitudeFt} pés)
                                        </span>
                                      </>
                                    )
                                  }
                                  
                                  // Se só tem metros, converter para pés
                                  if (altitudeM && !altitudeFt) {
                                    const convertedFt = Math.round(altitudeM * 3.28084)
                                    return (
                                      <>
                                        {altitudeM}m 
                                        <span className="text-gray-500 ml-1">
                                          ({convertedFt} pés)
                                        </span>
                                      </>
                                    )
                                  }
                                  
                                  // Se só tem pés, converter para metros
                                  if (!altitudeM && altitudeFt) {
                                    const convertedM = Math.round(altitudeFt / 3.28084)
                                    return (
                                      <>
                                        {convertedM}m 
                                        <span className="text-gray-500 ml-1">
                                          ({altitudeFt} pés)
                                        </span>
                                      </>
                                    )
                                  }
                                  
                                  // Se nenhum está disponível
                                  return 'N/A'
                                })()}
                              </span>
                            </div>
                          </div>
                        </div>

                        {/* Coordenadas e Mapa */}
                        <div className="rotaer-section">
                          <h3 className="rotaer-section-title">🗺️ Localização</h3>
                          {rotaerInfo.latitude && rotaerInfo.longitude ? (
                            <div className="space-y-4">
                              <div className="rotaer-coordinates">
                                <div className="font-semibold mb-2">Coordenadas:</div>
                                <div>Lat: {convertToHMS(rotaerInfo.latitude, true)}</div>
                                <div>Lon: {convertToHMS(rotaerInfo.longitude, false)}</div>
                                <div className="text-xs text-gray-500 mt-2">
                                  Decimal: {rotaerInfo.latitude.toFixed(6)}, {rotaerInfo.longitude.toFixed(6)}
                                </div>
                              </div>
                              <MiniMap
                                latitude={rotaerInfo.latitude}
                                longitude={rotaerInfo.longitude}
                                icao={icao}
                                className="mt-4"
                              />
                              
                              {/* Cartas de Navegação - Apenas quando há 3 ou menos cartas */}
                              {aiswebData?.cartas && aiswebData.cartas.items && aiswebData.cartas.items.length <= 3 && (
                                <div className="mt-6">
                                  <div className="bg-gradient-to-br from-blue-50 to-indigo-50 p-6 rounded-xl border border-blue-200 shadow-lg">
                                    <div className="flex items-center gap-4 mb-4">
                                      <div className="w-12 h-12 bg-blue-500 rounded-xl flex items-center justify-center">
                                        <span className="text-white text-xl">📄</span>
                                      </div>
                                      <div>
                                        <h4 className="text-lg font-bold text-blue-800">Cartas de Navegação</h4>
                                        {aiswebData.cartas.items && aiswebData.cartas.items.length > 0 ? (
                                          <p className="text-sm text-blue-600">{aiswebData.cartas.items.length} cartas disponíveis</p>
                                        ) : (
                                          <p className="text-sm text-blue-600">Não há cartas para este Aeródromo</p>
                                        )}
                                      </div>
                                    </div>
                                    
                                    {/* Conteúdo das cartas ou mensagem */}
                                    {aiswebData.cartas.items && aiswebData.cartas.items.length > 0 ? (
                                      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                      {aiswebData.cartas.items.map((carta: any, index: number) => {
                                        // Função para obter ícone baseado no tipo
                                        const getIcon = (tipo: string) => {
                                          switch (tipo) {
                                            case 'VAC': return '🛬'
                                            case 'ADC': return '🏢'
                                            case 'PDC': return '🅿️'
                                            case 'SID': return '🚀'
                                            case 'STAR': return '🎯'
                                            case 'IAC': return '📡'
                                            default: return '📄'
                                          }
                                        }

                                        const cartaId = `carta-${carta.id}-${index}`
                                        const isExpanded = expandedCartasCards[cartaId] !== false

                                        return (
                                          <div key={cartaId} className="bg-white p-4 rounded-lg border border-blue-100 shadow-sm hover:shadow-md transition-shadow">
                                            <div className="flex items-center justify-between mb-3">
                                              <div className="flex items-center gap-3">
                                                <div className="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
                                                  <span className="text-blue-600 text-lg">{getIcon(carta.tipo)}</span>
                                                </div>
                                                <div>
                                                  <div className="text-sm font-bold text-blue-800">{carta.tipo}</div>
                                                  <div className="text-xs text-blue-600">{carta.tipoDescr}</div>
                                                </div>
                                              </div>
                                              <button
                                                onClick={() => toggleCartasCard(cartaId)}
                                                className="px-2 py-1 bg-blue-100 hover:bg-blue-200 text-blue-600 rounded text-xs font-medium transition-colors"
                                              >
                                                {isExpanded ? '▼' : '▶'}
                                              </button>
                                            </div>
                                            
                                            {/* Conteúdo expansível */}
                                            <div className={`transition-all duration-300 ${
                                              isExpanded ? 'block' : 'hidden'
                                            }`}>
                                              <div className="space-y-2">
                                                <div>
                                                  <div className="text-xs text-blue-600 font-medium mb-1">Nome</div>
                                                  <div className="text-sm font-bold text-blue-800">{carta.nome}</div>
                                                </div>
                                                <div>
                                                  <div className="text-xs text-blue-600 font-medium mb-1">Emenda</div>
                                                  <div className="text-sm text-blue-700">{carta.amdt}</div>
                                                </div>
                                                {carta.use && (
                                                  <div>
                                                    <div className="text-xs text-blue-600 font-medium mb-1">Uso</div>
                                                    <div className="text-sm text-blue-700">{carta.use}</div>
                                                  </div>
                                                )}
                                                {carta.downloadUrl && (
                                                  <div className="pt-3 border-t border-blue-200">
                                                    <button
                                                      onClick={() => window.open(carta.downloadUrl, '_blank')}
                                                      className="w-full inline-flex items-center justify-center gap-2 px-3 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors text-sm font-medium"
                                                    >
                                                      📥 Baixar
                                                    </button>
                                                  </div>
                                                )}
                                              </div>
                                            </div>
                                          </div>
                                        )
                                      })}
                                      </div>
                                    ) : (
                                      <div className="text-center py-8">
                                        <div className="text-gray-400 text-4xl mb-2">📄</div>
                                        <p className="text-gray-500 text-sm">Não há cartas para este Aeródromo</p>
                                      </div>
                                    )}
                                  </div>
                                </div>
                              )}
                            </div>
                          ) : (
                            <p className="text-gray-600">Coordenadas não disponíveis</p>
                          )}
                        </div>

                        {/* Cartas de Navegação - Movidas para cima quando há mais de 3 cartas */}
                        {aiswebData?.cartas?.items && aiswebData.cartas.items.length > 3 && (
                          <div className="rotaer-section md:col-span-2">
                            <div className="bg-gradient-to-br from-blue-50 to-indigo-50 p-6 rounded-xl border border-blue-200 shadow-lg">
                              <div className="flex items-center gap-4 mb-4">
                                <div className="w-12 h-12 bg-blue-500 rounded-xl flex items-center justify-center">
                                  <span className="text-white text-xl">📄</span>
                                </div>
                                <div>
                                  <h4 className="text-lg font-bold text-blue-800">Cartas de Navegação</h4>
                                  <p className="text-sm text-blue-600">{aiswebData.cartas.items.length} cartas disponíveis</p>
                                </div>
                              </div>
                              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                {aiswebData.cartas.items.map((carta: any, index: number) => {
                                  // Função para obter ícone baseado no tipo
                                  const getIcon = (tipo: string) => {
                                    switch (tipo) {
                                      case 'VAC': return '🛬'
                                      case 'ADC': return '🏢'
                                      case 'PDC': return '🅿️'
                                      case 'SID': return '🚀'
                                      case 'STAR': return '🎯'
                                      case 'IAC': return '📡'
                                      default: return '📄'
                                    }
                                  }

                                  const cartaId = `carta-${carta.id}-${index}`
                                  const isExpanded = expandedCartasCards[cartaId] !== false

                                  return (
                                    <div key={cartaId} className="bg-white p-4 rounded-lg border border-blue-100 shadow-sm hover:shadow-md transition-shadow">
                                      <div className="flex items-center justify-between mb-3">
                                        <div className="flex items-center gap-3">
                                          <div className="w-8 h-8 bg-blue-100 rounded-lg flex items-center justify-center">
                                            <span className="text-blue-600 text-lg">{getIcon(carta.tipo)}</span>
                                          </div>
                                          <div>
                                            <div className="text-sm font-bold text-blue-800">{carta.tipo}</div>
                                            <div className="text-xs text-blue-600">{carta.tipoDescr}</div>
                                          </div>
                                        </div>
                                        <button
                                          onClick={() => toggleCartasCard(cartaId)}
                                          className="px-2 py-1 bg-blue-100 hover:bg-blue-200 text-blue-600 rounded text-xs font-medium transition-colors"
                                        >
                                          {isExpanded ? '▼' : '▶'}
                                        </button>
                                      </div>
                                      
                                      {/* Conteúdo expansível */}
                                      <div className={`transition-all duration-300 ${
                                        isExpanded ? 'block' : 'hidden'
                                      }`}>
                                        <div className="space-y-2">
                                          <div>
                                            <div className="text-xs text-blue-600 font-medium mb-1">Nome</div>
                                            <div className="text-sm font-bold text-blue-800">{carta.nome}</div>
                                          </div>
                                          <div>
                                            <div className="text-xs text-blue-600 font-medium mb-1">Emenda</div>
                                            <div className="text-sm text-blue-700">{carta.amdt}</div>
                                          </div>
                                          {carta.use && (
                                            <div>
                                              <div className="text-xs text-blue-600 font-medium mb-1">Uso</div>
                                              <div className="text-sm text-blue-700">{carta.use}</div>
                                            </div>
                                          )}
                                          {carta.downloadUrl && (
                                            <div className="pt-3 border-t border-blue-200">
                                              <button
                                                onClick={() => window.open(carta.downloadUrl, '_blank')}
                                                className="inline-flex items-center gap-2 text-xs text-blue-600 hover:text-blue-800 font-medium bg-blue-50 hover:bg-blue-100 px-3 py-2 rounded-lg transition-colors"
                                              >
                                                📄 Baixar Carta
                                                <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 6H6a2 2 0 00-2 2v10a2 2 0 002 2h10a2 2 0 002-2v-4M14 4h6m0 0v6m0-6L10 14" />
                                                </svg>
                                              </button>
                                            </div>
                                          )}
                                        </div>
                                      </div>
                                    </div>
                                  )
                                })}
                              </div>
                            </div>
                          </div>
                        )}

                        {/* Pistas e Iluminação */}
                        {rotaerInfo.pistas && rotaerInfo.pistas.length > 0 && (
                          <div className="rotaer-section">
                            <h3 className="rotaer-section-title">🛬 Pistas</h3>
                            <div className="rotaer-list">
                              {rotaerInfo.pistas.map((pista: any, index: number) => (
                                <div key={`pista-${pista.ident}-${index}`} className="rotaer-list-item">
                                  <div className="font-semibold text-lg mb-3 text-blue-800">{pista.ident}</div>
                                  
                                  {/* Informações Básicas da Pista - Bloquinho com Background */}
                                  <div className="bg-gradient-to-br from-blue-50 to-indigo-50 p-4 rounded-xl border border-blue-200 shadow-sm mb-4">
                                    <div className="flex items-center gap-2 mb-3">
                                      <div className="w-6 h-6 bg-blue-500 rounded-full flex items-center justify-center">
                                        <span className="text-white text-xs">📏</span>
                                    </div>
                                      <h4 className="font-semibold text-blue-800">Especificações da Pista</h4>
                                    </div>
                                    
                                    <div className="grid grid-cols-2 gap-4">
                                      <div className="bg-white p-3 rounded-lg border border-blue-100 shadow-sm">
                                        <div className="text-xs text-blue-600 font-medium mb-1">Comprimento</div>
                                        <div className="text-lg font-bold text-blue-800">{pista.length}m</div>
                                    </div>
                                      
                                      <div className="bg-white p-3 rounded-lg border border-blue-100 shadow-sm">
                                        <div className="text-xs text-blue-600 font-medium mb-1">Largura</div>
                                        <div className="text-lg font-bold text-blue-800">{pista.width}m</div>
                                    </div>
                                      
                                      <div className="bg-white p-3 rounded-lg border border-blue-100 shadow-sm">
                                        <div className="text-xs text-blue-600 font-medium mb-1">Superfície</div>
                                        <div className="text-lg font-bold text-blue-800">{pista.surface}</div>
                                      </div>
                                      
                                      <div className="bg-white p-3 rounded-lg border border-blue-100 shadow-sm">
                                        <div className="text-xs text-blue-600 font-medium mb-1">Código</div>
                                        <div className="text-lg font-bold text-blue-800">{pista.surface_c || 'N/A'}</div>
                                      </div>
                                      
                                    {pista.condition && (
                                        <div className="bg-white p-3 rounded-lg border border-blue-100 shadow-sm">
                                          <div className="text-xs text-blue-600 font-medium mb-1">Condição</div>
                                          <div className="text-lg font-bold text-blue-800">{pista.condition}</div>
                                      </div>
                                    )}
                                      
                                    {pista.condition_c && (
                                        <div className="bg-white p-3 rounded-lg border border-blue-100 shadow-sm">
                                          <div className="text-xs text-blue-600 font-medium mb-1">Código Condição</div>
                                          <div className="text-lg font-bold text-blue-800">{pista.condition_c}</div>
                                      </div>
                                    )}
                                    </div>
                                  </div>

                                  {/* Luzes da Pista */}
                                  {pista.luzes && pista.luzes.length > 0 && (
                                    <div className="mt-3 mb-3">
                                      <div className="font-medium text-sm mb-2 text-gray-700">💡 Luzes da Pista:</div>
                                      <div className="grid grid-cols-1 md:grid-cols-2 gap-2">
                                        {pista.luzes.map((luz: any, luzIndex: number) => (
                                          <div key={`luz-${luz.codigo}-${luzIndex}`} className="text-xs bg-gray-100 px-3 py-2 rounded border">
                                            <span className="font-medium text-blue-600">{luz.codigo}:</span> {luz.descricao}
                                          </div>
                                        ))}
                                      </div>
                                    </div>
                                  )}

                                  {/* Cabeceiras */}
                                  {pista.cabeceiras && pista.cabeceiras.length > 0 && (
                                    <div className="mt-3">
                                      <div className="font-medium text-sm mb-3 text-gray-700">🎯 Cabeceiras:</div>
                                      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                        {pista.cabeceiras.map((cabeceira: any, cabIndex: number) => (
                                          <div key={`cabeceira-${cabeceira.ident}-${cabIndex}`} className="bg-blue-50 p-3 rounded-lg border border-blue-200">
                                            <div className="font-medium text-sm text-blue-800 mb-2">{cabeceira.ident}</div>
                                            
                                            {/* Informações da Cabeceira */}
                                            <div className="space-y-1 mb-2">
                                              {cabeceira.latitude && cabeceira.longitude && (
                                                <div className="text-xs text-gray-600">
                                                  <span className="font-medium">Coordenadas:</span> 
                                                  {convertToHMS(cabeceira.latitude, true)} / {convertToHMS(cabeceira.longitude, false)}
                                                </div>
                                              )}
                                              {cabeceira.elevation && (
                                                <div className="text-xs text-gray-600">
                                                  <span className="font-medium">Elevação:</span> {cabeceira.elevation}m
                                                </div>
                                              )}
                                              {cabeceira.magnetic_heading && (
                                                <div className="text-xs text-gray-600">
                                                  <span className="font-medium">Rumo Magnético:</span> {cabeceira.magnetic_heading}°
                                                </div>
                                              )}
                                              {cabeceira.true_heading && (
                                                <div className="text-xs text-gray-600">
                                                  <span className="font-medium">Rumo Verdadeiro:</span> {cabeceira.true_heading}°
                                                </div>
                                              )}
                                            </div>

                                            {/* Luzes da Cabeceira */}
                                            {cabeceira.luzes && cabeceira.luzes.length > 0 && (
                                              <div className="mt-2">
                                                <div className="font-medium text-xs text-gray-700 mb-1">Luzes:</div>
                                                <div className="space-y-1">
                                                  {cabeceira.luzes.map((luz: any, luzIndex: number) => (
                                                    <div key={`cabeceira-luz-${luz.codigo}-${luzIndex}`} className="text-xs text-gray-600 bg-white px-2 py-1 rounded">
                                                      <span className="font-medium">{luz.codigo}:</span> {luz.descricao}
                                                    </div>
                                                  ))}
                                                </div>
                                              </div>
                                            )}
                                          </div>
                                        ))}
                                      </div>
                                    </div>
                                  )}
                                </div>
                              ))}
                            </div>
                            
                            {/* Iluminação do Aeródromo */}
                            {rotaerInfo.iluminacaoAerodromo && rotaerInfo.iluminacaoAerodromo.length > 0 && (
                              <div className="rotaer-list-item mt-6">
                                <h4 className="font-semibold text-lg mb-3 text-gray-800 flex items-center">
                                  💡 Iluminação do Aeródromo
                                </h4>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                  {rotaerInfo.iluminacaoAerodromo.map((luz: any, index: number) => (
                                    <div key={`iluminacao-${luz.codigo}-${index}`} className="bg-yellow-50 p-3 rounded-lg border border-yellow-200">
                                      <div className="font-medium text-yellow-800 mb-1">{luz.codigo}</div>
                                      <div className="text-sm text-gray-700">{luz.descricao}</div>
                                    </div>
                                  ))}
                                </div>
                              </div>
                            )}
                          </div>
                        )}

                        {/* Serviços */}
                        {rotaerInfo.servicos && (
                          <div className="rotaer-section">
                            <h3 className="rotaer-section-title">📡 Serviços</h3>
                            <div className="space-y-4">
                              {/* Comunicações */}
                              {rotaerInfo.servicos.com && rotaerInfo.servicos.com.length > 0 && (
                                <div>
                                  <div className="font-semibold text-sm mb-3 text-gray-800">📻 Comunicações:</div>
                                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                    {rotaerInfo.servicos.com.map((com: any, index: number) => (
                                      <div key={`com-${com.type}-${com.callsign}-${index}`} className="bg-green-50 p-3 rounded-lg border border-green-200">
                                        <div className="font-medium text-green-800">{com.type}</div>
                                        <div className="text-sm font-semibold text-green-700">{com.callsign}</div>
                                        <div className="text-sm text-gray-600 mt-1">
                                          {com.frequencias ? com.frequencias.join(', ') : 'N/A'}
                                        </div>
                                        {com.horario && (
                                          <div className="text-xs text-gray-500 mt-1">
                                            Horário: {com.horario}
                                          </div>
                                        )}
                                        {com.observacoes && (
                                          <div className="text-xs text-gray-500 mt-1">
                                            Obs: {com.observacoes}
                                          </div>
                                        )}
                                      </div>
                                    ))}
                                  </div>
                                </div>
                              )}

                              {/* Combustível */}
                              {rotaerInfo.servicos.combustivel && (
                                <div className="bg-yellow-50 p-3 rounded-lg border border-yellow-200">
                                  <div className="font-semibold text-sm text-yellow-800 mb-1">⛽ Combustível:</div>
                                  <div className="text-sm text-gray-700">{rotaerInfo.servicos.combustivel}</div>
                                </div>
                              )}

                              {/* Serviço Aeronave */}
                              {rotaerInfo.servicos.servicoAeronave && (
                                <div className="bg-blue-50 p-3 rounded-lg border border-blue-200">
                                  <div className="font-semibold text-sm text-blue-800 mb-1">✈️ Serviço Aeronave:</div>
                                  <div className="text-sm text-gray-700">{rotaerInfo.servicos.servicoAeronave}</div>
                                </div>
                              )}

                              {/* Serviço Aeroporto */}
                              {rotaerInfo.servicos.servicoAeroporto && (
                                <div className="bg-purple-50 p-3 rounded-lg border border-purple-200">
                                  <div className="font-semibold text-sm text-purple-800 mb-1">🏢 Serviço Aeroporto:</div>
                                  <div className="text-sm text-gray-700">{rotaerInfo.servicos.servicoAeroporto}</div>
                                </div>
                              )}

                              {/* Meteorologia */}
                              {meteoInfo?.sol && (
                                <div className="bg-cyan-50 p-3 rounded-lg border border-cyan-200">
                                  <div className="font-semibold text-sm text-cyan-800 mb-2">🌤️ Meteorologia:</div>
                                  <div className="grid grid-cols-2 gap-4">
                                    <div className="text-center">
                                      <div className="text-xs text-gray-600 flex items-center justify-center gap-1">
                                        🌅 Nascer do Sol
                                      </div>
                                      <div className="font-bold text-sm text-cyan-700">
                                        {meteoInfo.sol.sunrise ? (() => {
                                          try {
                                            if (typeof meteoInfo.sol.sunrise === 'string') {
                                              return meteoInfo.sol.sunrise
                                            }
                                            if (meteoInfo.sol.sunrise.hour !== undefined) {
                                              const hour = meteoInfo.sol.sunrise.hour.toString().padStart(2, '0')
                                              const minute = meteoInfo.sol.sunrise.minute.toString().padStart(2, '0')
                                              return `${hour}:${minute}`
                                            }
                                            return 'N/A'
                                          } catch {
                                            return 'N/A'
                                          }
                                        })() : 'N/A'}
                                      </div>
                                    </div>
                                    <div className="text-center">
                                      <div className="text-xs text-gray-600 flex items-center justify-center gap-1">
                                        🌇 Pôr do Sol
                                      </div>
                                      <div className="font-bold text-sm text-cyan-700">
                                        {meteoInfo.sol.sunset ? (() => {
                                          try {
                                            if (typeof meteoInfo.sol.sunset === 'string') {
                                              return meteoInfo.sol.sunset
                                            }
                                            if (meteoInfo.sol.sunset.hour !== undefined) {
                                              const hour = meteoInfo.sol.sunset.hour.toString().padStart(2, '0')
                                              const minute = meteoInfo.sol.sunset.minute.toString().padStart(2, '0')
                                              return `${hour}:${minute}`
                                            }
                                            return 'N/A'
                                          } catch {
                                            return 'N/A'
                                          }
                                        })() : 'N/A'}
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              )}

                              {/* METAR */}
                              <div className="bg-indigo-50 p-3 rounded-lg border border-indigo-200">
                                <div className="font-semibold text-sm text-indigo-800 mb-2">📊 METAR:</div>
                                
                                
                                {meteoInfo?.metar ? (
                                  <div className="space-y-2">
                                    {/* Código Original */}
                                    <div>
                                      <div className="text-xs text-gray-600 mb-1">Código Original:</div>
                                      <div className="font-mono text-xs bg-gray-100 p-2 rounded border">
                                        {meteoInfo.metar.raw || 'N/A'}
                                      </div>
                                    </div>
                                    
                                    {/* Decodificação */}
                                    {metarDecoded ? (
                                      <div>
                                        <div className="text-xs text-gray-600 mb-1">Decodificação:</div>
                                        <div className="text-xs whitespace-pre-line bg-gray-50 p-2 rounded border">
                                          {metarDecoded}
                                        </div>
                                      </div>
                                    ) : (
                                      <div className="text-xs text-gray-500 italic">
                                        Decodificação não disponível
                                      </div>
                                    )}
                                  </div>
                                ) : (
                                  <div className="text-xs text-gray-500 italic">
                                    METAR não disponível
                                  </div>
                                )}
                              </div>

                              {/* TAF */}
                              <div className="bg-red-50 p-3 rounded-lg border border-red-200">
                                <div className="font-semibold text-sm text-red-800 mb-2">📈 TAF:</div>
                                
                                
                                {meteoInfo?.taf ? (
                                  <div className="space-y-2">
                                    {/* Código Original */}
                                    <div>
                                      <div className="text-xs text-gray-600 mb-1">Código Original:</div>
                                      <div className="font-mono text-xs bg-gray-100 p-2 rounded border">
                                        {meteoInfo.taf.raw || 'N/A'}
                                      </div>
                                    </div>
                                    
                                    {/* Decodificação */}
                                    {tafDecoded ? (
                                      <div>
                                        <div className="text-xs text-gray-600 mb-1">Decodificação:</div>
                                        <div className="text-xs whitespace-pre-line bg-gray-50 p-2 rounded border">
                                          {tafDecoded}
                                        </div>
                                      </div>
                                    ) : (
                                      <div className="text-xs text-gray-500 italic">
                                        Decodificação não disponível
                                      </div>
                                    )}
                                  </div>
                                ) : (
                                  <div className="text-xs text-gray-500 italic">
                                    TAF não disponível
                                  </div>
                                )}
                              </div>

                              {/* Navegação */}
                              {rotaerInfo.servicos.nav && (
                                <div className="bg-orange-50 p-3 rounded-lg border border-orange-200">
                                  <div className="font-semibold text-sm text-orange-800 mb-1">🧭 Navegação:</div>
                                  <div className="text-sm text-gray-700">{rotaerInfo.servicos.nav}</div>
                                </div>
                              )}

                              {/* Outros Serviços */}
                              {rotaerInfo.servicos.outros && rotaerInfo.servicos.outros.length > 0 && (
                                <div>
                                  <div className="font-semibold text-sm mb-3 text-gray-800">🔧 Outros Serviços:</div>
                                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                    {rotaerInfo.servicos.outros.map((servico: any, index: number) => (
                                      <div key={`outro-${index}`} className="bg-gray-50 p-3 rounded-lg border border-gray-200">
                                        <div className="font-medium text-gray-800">{servico.tipo || 'Serviço'}</div>
                                        <div className="text-sm text-gray-700 mt-1">{servico.descricao}</div>
                                        {servico.horario && (
                                          <div className="text-xs text-gray-500 mt-1">
                                            Horário: {servico.horario}
                                          </div>
                                        )}
                                      </div>
                                    ))}
                                  </div>
                                </div>
                              )}
                            </div>
                          </div>
                        )}




                        {/* Complementos */}
                        {((rotaerInfo.complementos && rotaerInfo.complementos.length > 0) || (rotaerInfo.distanciasDeclaradas && rotaerInfo.distanciasDeclaradas.length > 0)) && (
                          <div className="rotaer-section md:col-span-2">
                            <h3 className="rotaer-section-title">📋 Complementos</h3>
                            <div className="space-y-4">
                              {/* Distâncias Declaradas */}
                              {rotaerInfo.distanciasDeclaradas && rotaerInfo.distanciasDeclaradas.length > 0 && (
                                <div className="bg-gradient-to-br from-slate-50 to-gray-100 p-5 rounded-xl border border-slate-200 shadow-sm">
                                  <div className="flex items-center gap-3 mb-4">
                                    <div className="w-10 h-10 bg-slate-100 rounded-full flex items-center justify-center">
                                      <span className="text-slate-600 text-lg">📏</span>
                                    </div>
                                    <div>
                                      <h4 className="font-semibold text-slate-800">Distâncias Declaradas</h4>
                                      <p className="text-xs text-slate-500">Informações de pista para decolagem e pouso</p>
                                    </div>
                                  </div>
                                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    {rotaerInfo.distanciasDeclaradas.map((dist: any, index: number) => (
                                      <div key={`dist-${dist.pista}-${index}`} className="bg-white p-4 rounded-lg border border-slate-200 shadow-sm hover:shadow-md transition-shadow">
                                        <div className="flex items-center gap-2 mb-3">
                                          <div className="w-6 h-6 bg-slate-100 rounded-full flex items-center justify-center">
                                            <span className="text-slate-600 font-bold text-xs">{dist.pista}</span>
                                          </div>
                                          <div className="font-semibold text-slate-800">Pista {dist.pista}</div>
                                        </div>
                                        <div className="grid grid-cols-2 gap-3">
                                          <div className="bg-slate-50 p-2 rounded border">
                                            <div className="text-xs text-slate-500 font-medium">TORA</div>
                                            <div className="text-sm font-semibold text-slate-800">{dist.tora}m</div>
                                          </div>
                                          <div className="bg-slate-50 p-2 rounded border">
                                            <div className="text-xs text-slate-500 font-medium">TODA</div>
                                            <div className="text-sm font-semibold text-slate-800">{dist.toda}m</div>
                                          </div>
                                          <div className="bg-slate-50 p-2 rounded border">
                                            <div className="text-xs text-slate-500 font-medium">ASDA</div>
                                            <div className="text-sm font-semibold text-slate-800">{dist.asda}m</div>
                                          </div>
                                          <div className="bg-slate-50 p-2 rounded border">
                                            <div className="text-xs text-slate-500 font-medium">LDA</div>
                                            <div className="text-sm font-semibold text-slate-800">{dist.lda}m</div>
                                          </div>
                                        </div>
                                      </div>
                                    ))}
                                  </div>
                                </div>
                              )}
                              
                              {/* Complementos originais */}
                              {rotaerInfo.complementos && rotaerInfo.complementos.length > 0 && (
                                <div className="space-y-3">
                                  {rotaerInfo.complementos.map((comp: any, index: number) => (
                                    <div key={`comp-${comp.codigo}-${index}`} className="bg-gradient-to-r from-blue-50 to-indigo-50 p-4 rounded-xl border border-blue-200 shadow-sm hover:shadow-md transition-all duration-200">
                                      <div className="flex items-start gap-3">
                                        <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center flex-shrink-0 mt-0.5">
                                          <span className="text-blue-600 font-bold text-xs">{comp.codigo}</span>
                                        </div>
                                        <div className="flex-1 min-w-0">
                                          <div className="font-semibold text-blue-800 mb-2">
                                            {(() => {
                                              switch (comp.codigo) {
                                                case "2.16": return "COORDENADAS E OPERAÇÕES";
                                                case "2.14": return "ALTITUDE MÍNIMA";
                                                case "2.20": return "REGULAMENTOS LOCAIS";
                                                case "2.3": return "HORÁRIO DE FUNCIONAMENTO";
                                                case "2.23": return "INFORMAÇÕES ADICIONAIS";
                                                case "2.6": return "SERVIÇOS DE SALVAMENTO";
                                                default: return comp.codigo;
                                              }
                                            })()}
                                          </div>
                                          <div className="text-sm text-gray-700 leading-relaxed" dangerouslySetInnerHTML={{ __html: comp.texto }} />
                                        </div>
                                      </div>
                                    </div>
                                  ))}
                                </div>
                              )}
                            </div>
                          </div>
                        )}



                        {/* Observações */}
                        {rotaerInfo.observacoes && rotaerInfo.observacoes.length > 0 && (() => {
                          const total = rotaerInfo.observacoes.length
                          
                          return (
                            <div className="rotaer-section md:col-span-2">
                              <div className="flex items-center justify-between mb-6">
                                <div className="flex items-center gap-3">
                                  <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center shadow-lg">
                                    <span className="text-white text-xl">📝</span>
                                  </div>
                                  <div>
                                    <div className="flex items-center gap-3">
                                      <h3 className="text-xl font-bold text-gray-900">Observações</h3>
                                      <div className="flex items-center gap-2">
                                        <span className="px-2 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-800">
                                          {total} Itens
                                        </span>
                                      </div>
                                    </div>
                                    <p className="text-sm text-gray-500">{total} observações disponíveis</p>
                                  </div>
                                </div>
                                <div className="text-right">
                                  <div className="text-xs text-gray-400">Última atualização</div>
                                  <div className="text-sm font-medium text-gray-600">{new Date().toLocaleTimeString('pt-BR')}</div>
                                </div>
                              </div>
                              
                              {/* Botão de controle do painel (sempre visível) */}
                              <div className="mb-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                                <div className="flex items-center justify-between">
                                  <div className="flex items-center gap-2">
                                    <span className="text-blue-600">📝</span>
                                    <span className="text-sm text-blue-800">
                                      Observações disponíveis
                                    </span>
                                  </div>
                                  <button
                                    onClick={() => setExpandedObservationsSection(!expandedObservationsSection)}
                                    className="px-3 py-1 bg-blue-100 hover:bg-blue-200 text-blue-800 text-sm font-medium rounded-lg transition-colors"
                                  >
                                    {expandedObservationsSection ? 'Ocultar' : 'Mostrar'} Painel
                                  </button>
                                </div>
                              </div>
                            
                              {/* Grid de Cards Observações Melhorado */}
                              <div className={`grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6 items-start transition-all duration-300 ${
                                !expandedObservationsSection ? 'hidden' : ''
                              }`}>
                                {rotaerInfo.observacoes
                                  .sort((a: any, b: any) => {
                                    // Priorizar código 2.3 (HORÁRIO DE FUNCIONAMENTO) como primeiro
                                    if (a.codigo === '2.3') return -1;
                                    if (b.codigo === '2.3') return 1;
                                    return 0;
                                  })
                                  .slice(0, 12).map((obs: any, index: number) => {
                                  const isExpanded = expandedObservations[`obs-${obs.codigo}-${index}`] || false
                                  const category = (() => {
                                    switch (obs.codigo) {
                                      case "2.2": return "DADOS GEOGRÁFICOS E ADMINISTRATIVOS";
                                      case "2.3": return "HORÁRIO DE FUNCIONAMENTO";
                                      case "2.6": return "SERVIÇOS DE SALVAMENTO E COMBATE A INCÊNDIO";
                                      case "2.8": return "DADOS DE PÁTIOS, PISTAS DE TÁXI E PONTOS DE VERIFICAÇÃO";
                                      case "2.10": return "OBSTÁCULOS DE AERÓDROMO";
                                      case "2.12": return "CARACTERÍSTICAS FÍSICAS DA PISTA";
                                      case "2.19": return "AUXÍLIOS-RÁDIO A NAVEGAÇÃO E POUSO";
                                      case "2.20": return "REGULAMENTOS LOCAIS DE AERÓDROMO";
                                      case "2.22": return "PROCEDIMENTOS DE VOO";
                                      case "2.23": return "INFORMAÇÃO ADICIONAL";
                                      default: return obs.codigo || `Obs ${index + 1}`;
                                    }
                                  })()
                                  
                                  const getCategoryIcon = (codigo: string) => {
                                    switch (codigo) {
                                      case "2.2": return "🏢";
                                      case "2.3": return "🕐";
                                      case "2.6": return "🚨";
                                      case "2.8": return "🛬";
                                      case "2.10": return "⚠️";
                                      case "2.12": return "🛣️";
                                      case "2.19": return "📡";
                                      case "2.20": return "📋";
                                      case "2.22": return "✈️";
                                      case "2.23": return "ℹ️";
                                      default: return "📝";
                                    }
                                  }
                                  
                                  const getCategoryColor = (codigo: string) => {
                                    switch (codigo) {
                                      case "2.2": return "from-blue-500 to-blue-600";
                                      case "2.3": return "from-green-500 to-green-600";
                                      case "2.6": return "from-red-500 to-red-600";
                                      case "2.8": return "from-purple-500 to-purple-600";
                                      case "2.10": return "from-orange-500 to-orange-600";
                                      case "2.12": return "from-indigo-500 to-indigo-600";
                                      case "2.19": return "from-cyan-500 to-cyan-600";
                                      case "2.20": return "from-pink-500 to-pink-600";
                                      case "2.22": return "from-teal-500 to-teal-600";
                                      case "2.23": return "from-gray-500 to-gray-600";
                                      default: return "from-blue-500 to-blue-600";
                                    }
                                  }
                                  
                                  return (
                                    <div key={`obs-${obs.codigo}-${index}`} className="group bg-white rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 transform hover:-translate-y-2 border border-gray-200 hover:border-gray-300 overflow-hidden">
                                      {/* Header do Card */}
                                      <div className="relative">
                                        <div className={`h-2 bg-gradient-to-r ${getCategoryColor(obs.codigo)}`}></div>
                                        <div className={`p-6 ${!isExpanded ? 'pb-3' : ''}`}>
                                          <div className={`flex items-start justify-between ${!isExpanded ? 'mb-2' : 'mb-4'}`}>
                                            <div className="flex items-center gap-4">
                                              <div className={`w-14 h-14 rounded-xl flex items-center justify-center text-white font-bold text-xl shadow-lg bg-gradient-to-br ${getCategoryColor(obs.codigo)}`}>
                                                {getCategoryIcon(obs.codigo)}
                                            </div>
                                              <div className="flex-1 min-w-0">
                                                <div className="font-bold text-gray-900 text-lg leading-tight mb-2">
                                                  {category}
                                              </div>
                                                <div className="flex items-center gap-2">
                                                  <span className="px-3 py-1 rounded-full text-xs font-semibold bg-gray-100 text-gray-700">
                                                    {obs.codigo}
                                                  </span>
                                                  <span className="px-3 py-1 rounded-full text-xs font-semibold bg-blue-100 text-blue-700">
                                                  Observação
                                                </span>
                                              </div>
                                            </div>
                                          </div>
                                          
                                            {/* Botão Toggle Melhorado */}
                                          <button
                                            onClick={() => setExpandedObservations(prev => ({ ...prev, [`obs-${obs.codigo}-${index}`]: !prev[`obs-${obs.codigo}-${index}`] }))}
                                              className={`p-3 rounded-xl transition-all duration-200 ${
                                                isExpanded 
                                                  ? 'bg-blue-100 text-blue-600 hover:bg-blue-200' 
                                                  : 'bg-gray-100 text-gray-600 hover:bg-gray-200'
                                              }`}
                                              title={isExpanded ? 'Minimizar' : 'Expandir'}
                                            >
                                              <span className={`text-lg transition-transform duration-200 ${isExpanded ? 'rotate-180' : ''}`}>
                                              {isExpanded ? '▲' : '▼'}
                                            </span>
                                          </button>
                                        </div>
                                        
                                        {/* Conteúdo da observação */}
                                        {isExpanded ? (
                                            <div className="space-y-4">
                                              <div className="bg-gradient-to-br from-gray-50 to-gray-100 p-5 rounded-xl border border-gray-200">
                                                <div className="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap" dangerouslySetInnerHTML={{ __html: obs.texto }} />
                                            </div>
                                          </div>
                                        ) : (
                                            /* Visualização Compacta Melhorada */
                                            <div className="h-16 flex items-center">
                                              <div className="bg-gradient-to-br from-gray-50 to-gray-100 p-3 rounded-lg border border-gray-200 w-full">
                                                <div className="text-xs text-gray-600 leading-tight" style={{
                                              display: '-webkit-box',
                                                  WebkitLineClamp: 1,
                                              WebkitBoxOrient: 'vertical',
                                              overflow: 'hidden'
                                            }}>
                                                  {obs.texto?.replace(/<[^>]*>/g, '').substring(0, 100)}...
                                                </div>
                                            </div>
                                          </div>
                                        )}
                                        </div>
                                      </div>
                                    </div>
                                  )
                                })}
                              </div>
                            </div>
                          )
                        })()}

                        {/* NOTAMs */}
                        {notamInfo?.items?.length > 0 && (() => {
                          // Calcular contadores
                          const ativos = notamInfo.items.filter((item: any) => {
                            const now = new Date()
                            const validUntil = item.validUntil ? new Date(item.validUntil) : null
                            return validUntil && validUntil > now
                          }).length
                          const vencidos = notamInfo.items.length - ativos
                          const temApenasVencidos = ativos === 0
                          
                          return (
                          <div className="rotaer-section md:col-span-2">
                              <div className="flex items-center justify-between mb-6">
                                <div className="flex items-center gap-3">
                                  <div className="w-10 h-10 bg-gradient-to-br from-red-500 to-red-600 rounded-xl flex items-center justify-center shadow-lg">
                                    <span className="text-white text-xl">⚠️</span>
                                  </div>
                                  <div>
                                    <div className="flex items-center gap-3">
                                      <h3 className="text-xl font-bold text-gray-900">NOTAMs</h3>
                                      <div className="flex items-center gap-2">
                                        <span className="px-2 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-800">
                                          {ativos} Ativos
                                        </span>
                                        <span className="px-2 py-1 rounded-full text-xs font-semibold bg-red-100 text-red-800">
                                          {vencidos} Vencidos
                                        </span>
                                      </div>
                                    </div>
                                    <p className="text-sm text-gray-500">{notamInfo.items.length} notificações para aeronavegantes</p>
                                  </div>
                                </div>
                                <div className="text-right">
                                  <div className="text-xs text-gray-400">Última atualização</div>
                                  <div className="text-sm font-medium text-gray-600">{new Date().toLocaleTimeString('pt-BR')}</div>
                                </div>
                              </div>
                              
                              {/* Banner de aviso se todos estão vencidos */}
                              {temApenasVencidos && (
                                <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg">
                                  <div className="flex items-center justify-between">
                                    <div className="flex items-center gap-2">
                                      <span className="text-red-600">⚠️</span>
                                      <span className="text-sm text-red-800">
                                        Todos os NOTAMs estão vencidos. Seção minimizada automaticamente.
                                      </span>
                                    </div>
                                    <button
                                      onClick={() => setExpandedNotamsSection(!expandedNotamsSection)}
                                      className="px-3 py-1 bg-red-100 hover:bg-red-200 text-red-800 text-sm font-medium rounded-lg transition-colors"
                                    >
                                      {expandedNotamsSection ? 'Ocultar' : 'Mostrar'} Vencidos
                                    </button>
                                  </div>
                                </div>
                              )}
                              
                              {/* Botão de controle do painel (sempre visível) */}
                              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg">
                                <div className="flex items-center justify-between">
                                  <div className="flex items-center gap-2">
                                    <span className="text-red-600">⚠️</span>
                                    <span className="text-sm text-red-800">
                                      {ativos > 0 && vencidos > 0 
                                        ? `NOTAMs ativos e vencidos disponíveis`
                                        : ativos > 0 
                                          ? `NOTAMs ativos disponíveis`
                                          : `NOTAMs vencidos disponíveis`
                                      }
                                    </span>
                                  </div>
                                  <button
                                    onClick={() => setExpandedNotamsSection(!expandedNotamsSection)}
                                    className="px-3 py-1 bg-red-100 hover:bg-red-200 text-red-800 text-sm font-medium rounded-lg transition-colors"
                                  >
                                    {expandedNotamsSection ? 'Ocultar' : 'Mostrar'} Painel
                                  </button>
                                </div>
                              </div>
                              
                            {/* Grid de Cards NOTAMs */}
                            <div className={`grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 items-start transition-all duration-300 ${
                              !expandedNotamsSection ? 'hidden' : ''
                            }`}>
                              {notamInfo.items
                                .sort((a: any, b: any) => {
                                  // Ativos primeiro
                                  const aActive = a.validUntil ? new Date(a.validUntil) > new Date() : false
                                  const bActive = b.validUntil ? new Date(b.validUntil) > new Date() : false
                                  
                                  if (aActive && !bActive) return -1
                                  if (!aActive && bActive) return 1
                                  
                                  // Se ambos são ativos, ordenar por data (mais recente primeiro)
                                  if (aActive && bActive) {
                                    const aDate = new Date(a.validUntil || a.validFrom || 0)
                                    const bDate = new Date(b.validUntil || b.validFrom || 0)
                                    return bDate.getTime() - aDate.getTime()
                                  }
                                  
                                  // Se ambos são vencidos, ordenar por data (mais recente primeiro)
                                  if (!aActive && !bActive) {
                                    const aDate = new Date(a.validUntil || a.validFrom || 0)
                                    const bDate = new Date(b.validUntil || b.validFrom || 0)
                                    return bDate.getTime() - aDate.getTime()
                                  }
                                  
                                  return 0
                                })
                                .map((notam: any, index: number) => {
                                const isExpanded = expandedNotams[`notam-${notam.id}-${index}`] || false
                                const isActive = notam.validUntil ? new Date(notam.validUntil) > new Date() : false
                                
                                return (
                                  <div key={`notam-${notam.id}-${index}`} className="bg-white rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 ring-2 ring-red-200 border border-red-300">
                                    <div className="p-5">
                                      <div className="flex items-start justify-between mb-4">
                                        <div className="flex items-center gap-3">
                                          <div className="w-12 h-12 rounded-xl flex items-center justify-center text-white font-bold text-lg shadow-md bg-gradient-to-br from-red-500 to-red-600">
                                            ⚠️
                                          </div>
                                          <div>
                                            <div className="font-bold text-gray-900 text-lg">
                                              {notam.id || notam.icaoCode || 'N/A'}
                                            </div>
                                            <div className="flex items-center gap-2 mt-1">
                                              <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                                                notam.type === 'NOTAMN' ? 'bg-blue-100 text-blue-800' :
                                                notam.type === 'NOTAMR' ? 'bg-orange-100 text-orange-800' :
                                                notam.type === 'NOTAMC' ? 'bg-purple-100 text-purple-800' :
                                                'bg-gray-100 text-gray-800'
                                              }`}>
                                                {notam.type || 'NOTAM'}
                                              </span>
                                            </div>
                                          </div>
                                        </div>
                                        
                                        <div className="flex items-center gap-2">
                                          {/* Status Badge */}
                                          <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                                            isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                                          }`}>
                                            {isActive ? 'Ativo' : 'Vencido'}
                                          </span>
                                          
                                          {/* Botão Toggle */}
                                          <button
                                            onClick={() => setExpandedNotams(prev => ({ ...prev, [`notam-${notam.id}-${index}`]: !prev[`notam-${notam.id}-${index}`] }))}
                                            className="p-2 rounded-full bg-gray-100 hover:bg-gray-200 transition-colors duration-200"
                                            title={isExpanded ? 'Minimizar' : 'Ampliar'}
                                          >
                                            <span className={`text-gray-600 transition-transform duration-200 ${isExpanded ? 'rotate-180' : ''}`}>
                                              {isExpanded ? '▲' : '▼'}
                                            </span>
                                          </button>
                                        </div>
                                      </div>
                                      
                                      {/* Conteúdo Expandido/Collapsado */}
                                      {isExpanded ? (
                                        <div className="space-y-3">
                                          <div className="bg-gray-50 p-4 rounded-xl border border-gray-200">
                                            <div className="text-sm text-gray-700 leading-relaxed">
                                              {notam.message || 'Mensagem não disponível'}
                                            </div>
                                          </div>
                                          
                                          {/* Informações detalhadas */}
                                          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                            {notam.icaoCode && (
                                              <div className="bg-blue-50 p-3 rounded-lg border border-blue-200">
                                                <div className="text-xs text-blue-600 font-medium">Aeródromo</div>
                                                <div className="text-sm font-semibold text-blue-800">{notam.icaoCode}</div>
                                              </div>
                                            )}
                                            {notam.location && (
                                              <div className="bg-green-50 p-3 rounded-lg border border-green-200">
                                                <div className="text-xs text-green-600 font-medium">Localização</div>
                                                <div className="text-sm font-semibold text-green-800">{notam.location}</div>
                                              </div>
                                            )}
                                            {notam.validFrom && (
                                              <div className="bg-yellow-50 p-3 rounded-lg border border-yellow-200">
                                                <div className="text-xs text-yellow-600 font-medium">Válido de</div>
                                                <div className="text-sm font-semibold text-yellow-800">
                                                  {new Date(notam.validFrom).toLocaleString('pt-BR')}
                                                </div>
                                              </div>
                                            )}
                                            {notam.validUntil && (
                                              <div className="bg-red-50 p-3 rounded-lg border border-red-200">
                                                <div className="text-xs text-red-600 font-medium">Válido até</div>
                                                <div className="text-sm font-semibold text-red-800">
                                                  {new Date(notam.validUntil).toLocaleString('pt-BR')}
                                                </div>
                                              </div>
                                            )}
                                          </div>
                                        </div>
                                      ) : (
                                        /* Visualização Compacta */
                                        <div className="space-y-2">
                                          <div className="text-sm text-gray-700 leading-relaxed" style={{
                                            display: '-webkit-box',
                                            WebkitLineClamp: 2,
                                            WebkitBoxOrient: 'vertical',
                                            overflow: 'hidden'
                                          }}>
                                            {notam.message?.substring(0, 150)}...
                                          </div>
                                          <div className="flex items-center justify-between text-xs text-gray-500">
                                            <span>{notam.icaoCode || 'N/A'}</span>
                                            <span>{notam.validUntil ? new Date(notam.validUntil).toLocaleDateString('pt-BR') : 'N/A'}</span>
                                          </div>
                                        </div>
                                      )}
                                    </div>
                                  </div>
                                )
                              })}
                            </div>
                          </div>
                          )
                        })()}
                        

                        {/* INFOTEMP */}
                        {infotempInfo?.itens?.length > 0 && (() => {
                          // Calcular contadores
                          const ativos = infotempInfo.itens.filter((item: any) => item.ativoAgora).length
                          const vencidos = infotempInfo.itens.length - ativos
                          const temApenasVencidos = ativos === 0
                          
                          return (
                          <div className="rotaer-section md:col-span-2">
                              <div className="flex items-center justify-between mb-6">
                                <div className="flex items-center gap-3">
                                  <div className="w-10 h-10 bg-gradient-to-br from-yellow-500 to-yellow-600 rounded-xl flex items-center justify-center shadow-lg">
                                    <span className="text-white text-xl">ℹ️</span>
                                    </div>
                                  <div>
                                    <div className="flex items-center gap-3">
                                      <h3 className="text-xl font-bold text-gray-900">INFOTEMP</h3>
                                      <div className="flex items-center gap-2">
                                        <span className="px-2 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-800">
                                          {ativos} Ativos
                                        </span>
                                        <span className="px-2 py-1 rounded-full text-xs font-semibold bg-red-100 text-red-800">
                                          {vencidos} Vencidos
                                        </span>
                                      </div>
                                    </div>
                                    <p className="text-sm text-gray-500">{infotempInfo.itens.length} informações temporárias</p>
                                  </div>
                                </div>
                                <div className="text-right">
                                  <div className="text-xs text-gray-400">Última atualização</div>
                                  <div className="text-sm font-medium text-gray-600">{new Date().toLocaleTimeString('pt-BR')}</div>
                                    </div>
                                  </div>
                                  
                            {/* Controle de Expansão da Seção */}
                            {temApenasVencidos && (
                              <div className="mb-4 p-3 bg-yellow-50 border border-yellow-200 rounded-lg">
                                <div className="flex items-center justify-between">
                                  <div className="flex items-center gap-2">
                                    <span className="text-yellow-600">⚠️</span>
                                    <span className="text-sm text-yellow-800">
                                      Todos os INFOTEMPs estão vencidos.
                                    </span>
                                  </div>
                                  <button
                                    onClick={() => setExpandedInfotempSection(!expandedInfotempSection)}
                                    className="px-3 py-1 bg-yellow-100 hover:bg-yellow-200 text-yellow-800 text-sm font-medium rounded-lg transition-colors"
                                  >
                                    {expandedInfotempSection ? 'Ocultar' : 'Mostrar'} Vencidos
                                  </button>
                                </div>
                                    </div>
                                  )}
                                  
                            {/* Grid de Cards INFOTEMP */}
                            <div className={`grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 items-start transition-all duration-300 ${
                              temApenasVencidos && !expandedInfotempSection ? 'hidden' : ''
                            }`}>
                              {infotempInfo.itens
                                .sort((a: any, b: any) => {
                                  // Ativos primeiro
                                  if (a.ativoAgora && !b.ativoAgora) return -1
                                  if (!a.ativoAgora && b.ativoAgora) return 1
                                  
                                  // Se ambos são ativos, ordenar por data (mais recente primeiro)
                                  if (a.ativoAgora && b.ativoAgora) {
                                    const aDate = new Date(a.fimVigencia || a.dataPublicacao || a.inicioVigencia || 0)
                                    const bDate = new Date(b.fimVigencia || b.dataPublicacao || b.inicioVigencia || 0)
                                    return bDate.getTime() - aDate.getTime()
                                  }
                                  
                                  // Se ambos são vencidos, ordenar por data (mais recente primeiro)
                                  if (!a.ativoAgora && !b.ativoAgora) {
                                    const aDate = new Date(a.fimVigencia || a.dataPublicacao || 0)
                                    const bDate = new Date(b.fimVigencia || b.dataPublicacao || 0)
                                    return bDate.getTime() - aDate.getTime()
                                  }
                                  
                                  return 0
                                })
                                .map((item: any, index: number) => {
                                const isExpanded = expandedObservations[`infotemp-${item.id}-${index}`] || false
                                
                                return (
                                  <div key={`infotemp-${item.id}-${index}`} className="bg-white rounded-2xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1 ring-2 ring-yellow-200 border border-yellow-300">
                                    <div className="p-5">
                                      <div className="flex items-start justify-between mb-4">
                                        <div className="flex items-center gap-3">
                                          <div className="w-12 h-12 rounded-xl flex items-center justify-center text-white font-bold text-lg shadow-md bg-gradient-to-br from-yellow-500 to-yellow-600">
                                            ℹ️
                                          </div>
                                          <div>
                                            <div className="font-bold text-gray-900 text-lg">
                                      {item.icao || item.codigo || 'N/A'}
                                    </div>
                                            <div className="flex items-center gap-2 mt-1">
                                              <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                                                item.severidade === 3 ? 'bg-red-100 text-red-800' :
                                                item.severidade === 2 ? 'bg-yellow-100 text-yellow-800' :
                                                item.severidade === 1 ? 'bg-orange-100 text-orange-800' :
                                                'bg-green-100 text-green-800'
                                              }`}>
                                                {item.severidade === 3 ? 'Fechamento' :
                                                 item.severidade === 2 ? 'Restrição' :
                                                 item.severidade === 1 ? 'Modificação' :
                                                 'Info'}
                                              </span>
                                            </div>
                                          </div>
                                        </div>
                                        
                                        <div className="flex items-center gap-2">
                                          {/* Status Badge */}
                                          <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                                            item.ativoAgora ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                                          }`}>
                                            {item.ativoAgora ? 'Ativo' : 'Vencido'}
                                          </span>
                                          
                                          {/* Botão Toggle */}
                                          <button
                                            onClick={() => setExpandedObservations(prev => ({ ...prev, [`infotemp-${item.id}-${index}`]: !prev[`infotemp-${item.id}-${index}`] }))}
                                            className="p-2 rounded-full bg-gray-100 hover:bg-gray-200 transition-colors duration-200"
                                            title={isExpanded ? 'Minimizar' : 'Ampliar'}
                                          >
                                            <span className={`text-gray-600 transition-transform duration-200 ${isExpanded ? 'rotate-180' : ''}`}>
                                              {isExpanded ? '▲' : '▼'}
                                            </span>
                                          </button>
                                    </div>
                                  </div>
                                  
                                      {/* Conteúdo Expandido/Collapsado */}
                                      {isExpanded ? (
                                        <div className="space-y-4">
                                  {/* Observação/Descrição */}
                                          <div className="bg-yellow-50 p-4 rounded-xl border border-yellow-200">
                                            <div className="flex items-center gap-2 mb-2">
                                              <span className="text-lg">📝</span>
                                              <span className="text-sm font-semibold text-yellow-700">Descrição</span>
                                            </div>
                                            <div className="text-sm text-gray-700 leading-relaxed">
                                              {item.observacao || 'N/A'}
                                    </div>
                                  </div>
                                  
                                  {/* Informações Técnicas */}
                                          <div className="space-y-2">
                                            <div className="flex items-center gap-2 text-sm">
                                              <span className="text-blue-500">📊</span>
                                              <span className="text-gray-600">Severidade:</span>
                                              <span className="font-semibold text-gray-900">
                                                {item.severidade === 3 ? 'Fechamento' :
                                                 item.severidade === 2 ? 'Restrição' :
                                                 item.severidade === 1 ? 'Modificação' :
                                                 'Informação'}
                                              </span>
                                            </div>
                                            <div className="flex items-center gap-2 text-sm">
                                              <span className="text-green-500">⚡</span>
                                              <span className="text-gray-600">Status:</span>
                                              <span className={`px-2 py-1 rounded text-xs font-semibold ${
                                                item.ativoAgora ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                                              }`}>
                                                {item.ativoAgora ? 'Ativo' : 'Vencido'}
                                        </span>
                                      </div>
                                            <div className="flex items-center gap-2 text-sm">
                                              <span className="text-blue-500">📊</span>
                                              <span className="text-gray-600">Impacto:</span>
                                              <span className="font-semibold text-gray-900">{item.impactoOperacional || 'INFO'}</span>
                                      </div>
                                            {item.periodoInvalido && (
                                              <div className="flex items-center gap-2 text-sm">
                                                <span className="text-red-500">⚠️</span>
                                                <span className="text-gray-600">Período:</span>
                                                <span className="font-semibold text-red-900">Inválido</span>
                                      </div>
                                    )}
                                  </div>
                                  
                                          {/* Datas de Vigência */}
                                          <div className="bg-gradient-to-r from-yellow-50 to-orange-50 p-3 rounded-xl border border-yellow-200">
                                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                                              <div>
                                                <div className="text-xs font-medium text-yellow-700 mb-1">Início</div>
                                                <div className="text-sm font-bold text-yellow-900">
                                                  {item.inicioVigencia ? new Date(item.inicioVigencia).toLocaleDateString('pt-BR') : 'N/A'}
                                    </div>
                                              </div>
                                              <div>
                                                <div className="text-xs font-medium text-yellow-700 mb-1">Fim</div>
                                                <div className="text-sm font-bold text-yellow-900">
                                                  {item.fimVigencia ? new Date(item.fimVigencia).toLocaleDateString('pt-BR') : 'N/A'}
                                                </div>
                                              </div>
                                    </div>
                                  </div>
                                  
                                          {/* Data de Publicação */}
                                          {item.dataPublicacao && (
                                            <div className="bg-gray-50 p-3 rounded-xl border border-gray-200">
                                              <div className="text-xs font-medium text-gray-700 mb-1">Publicado em</div>
                                              <div className="text-sm font-bold text-gray-900">
                                                {new Date(item.dataPublicacao).toLocaleDateString('pt-BR')}
                                              </div>
                                    </div>
                                  )}
                                </div>
                                      ) : (
                                        /* Visualização Compacta */
                                        <div className="space-y-2">
                                          {/* Observação Resumida */}
                                          {item.observacao && (
                                            <div className="bg-yellow-50 p-3 rounded-lg border border-yellow-200">
                                              <p className="text-sm text-yellow-800 leading-relaxed" style={{
                                                display: '-webkit-box',
                                                WebkitLineClamp: 2,
                                                WebkitBoxOrient: 'vertical',
                                                overflow: 'hidden'
                                              }}>
                                                {item.observacao}
                                              </p>
                                            </div>
                                          )}
                                          
                                          {/* Informações Essenciais */}
                                          <div className="flex items-center justify-between text-xs text-gray-600">
                                            <div className="flex items-center gap-2">
                                              <span>📊 Severidade: {item.severidade === 3 ? 'Fechamento' :
                                                                    item.severidade === 2 ? 'Restrição' :
                                                                    item.severidade === 1 ? 'Modificação' :
                                                                    'Info'}</span>
                                              <span className={`px-2 py-1 rounded text-xs font-semibold ${
                                                item.ativoAgora ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                                              }`}>
                                                {item.ativoAgora ? 'Ativo' : 'Vencido'}
                                              </span>
                                            </div>
                                            <div className="text-yellow-600 font-medium">
                                              {item.fimVigencia ? new Date(item.fimVigencia).toLocaleDateString('pt-BR') : 'N/A'}
                                            </div>
                            </div>
                          </div>
                        )}
                                    </div>
                                  </div>
                                )
                              })}
                            </div>
                          </div>
                        )})()}

                        {/* SUPLEMENTOS DAS FIRS */}
                        {(() => {
                          const firs = [
                            { code: 'SBCW', name: 'Curitiba' },
                            { code: 'SBAO', name: 'Atlântico' },
                            { code: 'SBAZ', name: 'Amazônica' },
                            { code: 'SBRE', name: 'Recife' },
                            { code: 'SBBS', name: 'Brasília' }
                          ]
                          
                          // Verificar se há dados ou carregamento
                          const isLoading = firs.some(fir => 
                            suplementosFirLoading[fir.code]
                          )
                          const hasAnyData = firs.some(fir => 
                            suplementosFirData[fir.code] && !suplementosFirLoading[fir.code]
                          )
                          
                          if (!hasAnyData && !isLoading) return null
                          
                          return (
                            <div className="rotaer-section md:col-span-2">
                              <div className="flex items-center justify-between mb-6">
                                <div className="flex items-center gap-3">
                                  <div className="w-10 h-10 bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl flex items-center justify-center shadow-lg">
                                    <span className="text-white text-xl">📋</span>
                                  </div>
                                  <div>
                                    <div className="flex items-center gap-3">
                                      <h3 className="text-xl font-bold text-gray-900">Suplementos por FIR</h3>
                                      <div className="flex items-center gap-2">
                                        {firs.map(fir => {
                                          const data = suplementosFirData[fir.code]
                                          const loading = suplementosFirLoading[fir.code]
                                          const count = data?.items?.length || 0
                                          
                                          if (loading) {
                                            return (
                                              <span key={fir.code} className="px-2 py-1 rounded-full text-xs font-semibold bg-gray-100 text-gray-500">
                                                {fir.name} ⏳
                                              </span>
                                            )
                                          }
                                          
                                          if (count > 0) {
                                            return (
                                              <span key={fir.code} className="px-2 py-1 rounded-full text-xs font-semibold bg-purple-100 text-purple-800">
                                                {fir.name}: {count}
                                              </span>
                                            )
                                          }
                                          
                                          return null
                                        })}
                                      </div>
                                    </div>
                                    <p className="text-sm text-gray-500">Suplementos de navegação aérea por região</p>
                                  </div>
                                </div>
                                
                                <button
                                  onClick={() => setExpandedSupplementosSection(!expandedSupplementosSection)}
                                  className="px-4 py-2 bg-purple-100 hover:bg-purple-200 text-purple-800 font-medium rounded-lg transition-colors"
                                >
                                  {expandedSupplementosSection ? 'Ocultar' : 'Mostrar'} Painel
                                </button>
                              </div>
                              
                              {/* Grid de Suplementos por FIR */}
                              <div className={`space-y-6 transition-all duration-300 ${
                                !expandedSupplementosSection ? 'hidden' : ''
                              }`}>
                                {firs.map(fir => {
                                  const data = suplementosFirData[fir.code]
                                  const loading = suplementosFirLoading[fir.code]
                                  
                                  if (loading) {
                                    return (
                                      <div key={fir.code} className="bg-white rounded-xl border border-gray-200 shadow-sm">
                                        <div className="p-6">
                                          <div className="flex items-center gap-3 mb-4">
                                            <div className="w-8 h-8 bg-purple-100 rounded-lg flex items-center justify-center">
                                              <span className="text-purple-600 font-bold text-sm">{fir.code}</span>
                                            </div>
                                            <h4 className="text-lg font-bold text-gray-900">{fir.name}</h4>
                                          </div>
                                          <div className="flex items-center justify-center py-8">
                                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-purple-600"></div>
                                            <span className="ml-3 text-gray-600">Carregando suplementos...</span>
                                          </div>
                                        </div>
                                      </div>
                                    )
                                  }
                                  
                                  // Se não há dados carregados ainda, não exibir
                                  if (!data) return null
                                  
                                  // Se há dados mas não há items, mostrar mensagem
                                  if (!data.items?.length) {
                                    return (
                                      <div key={fir.code} className="bg-white rounded-xl border border-gray-200 shadow-sm">
                                        <div className="p-6">
                                          <div className="flex items-center justify-between mb-4">
                                            <div className="flex items-center gap-3">
                                              <div className="w-8 h-8 bg-purple-100 rounded-lg flex items-center justify-center">
                                                <span className="text-purple-600 font-bold text-sm">{fir.code}</span>
                                              </div>
                                              <div>
                                                <h4 className="text-lg font-bold text-gray-900">{fir.name}</h4>
                                                <p className="text-sm text-gray-500">Não há suplementos registrados</p>
                                              </div>
                                            </div>
                                            
                                            <button
                                              onClick={() => toggleFirCard(fir.code)}
                                              className="px-3 py-1 bg-purple-100 hover:bg-purple-200 text-purple-800 font-medium rounded-lg transition-colors text-sm"
                                            >
                                              {expandedFirCards[fir.code] ? 'Ocultar' : 'Mostrar'} Detalhes
                                            </button>
                                          </div>
                                          
                                          <div className={`transition-all duration-300 ${
                                            expandedFirCards[fir.code] ? 'block' : 'hidden'
                                          }`}>
                                            <div className="text-center py-8">
                                              <div className="text-gray-400 text-4xl mb-2">📋</div>
                                              <p className="text-gray-500 text-sm">Não há suplementos registrados</p>
                                            </div>
                                          </div>
                                        </div>
                                      </div>
                                    )
                                  }
                                  
                                  return (
                                    <div key={fir.code} className="bg-white rounded-xl border border-gray-200 shadow-sm">
                                      <div className="p-6">
                                        <div className="flex items-center justify-between mb-4">
                                          <div className="flex items-center gap-3">
                                            <div className="w-8 h-8 bg-purple-100 rounded-lg flex items-center justify-center">
                                              <span className="text-purple-600 font-bold text-sm">{fir.code}</span>
                                            </div>
                                            <div>
                                              <h4 className="text-lg font-bold text-gray-900">{fir.name}</h4>
                                              <p className="text-sm text-gray-500">{data.items.length} suplementos disponíveis</p>
                                            </div>
                                          </div>
                                          
                                          <div className="flex items-center gap-3">
                                            {data.meta && (
                                              <div className="text-xs text-gray-500">
                                                Total: {data.meta.total} suplementos
                                              </div>
                                            )}
                                            <button
                                              onClick={() => toggleFirCard(fir.code)}
                                              className="px-3 py-1 bg-purple-100 hover:bg-purple-200 text-purple-800 font-medium rounded-lg transition-colors text-sm"
                                            >
                                              {expandedFirCards[fir.code] ? 'Ocultar' : 'Mostrar'} Suplementos
                                            </button>
                                          </div>
                                        </div>
                                        
                                        {/* Grid de Suplementos */}
                                        <div className={`transition-all duration-300 ${
                                          expandedFirCards[fir.code] ? 'block' : 'hidden'
                                        }`}>
                                          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                          {data.items.slice(0, 6).map((item: any, index: number) => (
                                            <div key={`${fir.code}-${item.id}-${index}`} 
                                                 className="bg-gradient-to-br from-purple-50 to-indigo-50 p-4 rounded-lg border border-purple-200 hover:shadow-md transition-shadow">
                                              <div className="flex justify-between items-start mb-3">
                                                <div className="font-semibold text-sm text-purple-800 leading-tight">
                                                  {item.name || `Suplemento ${index + 1}`}
                                                </div>
                                                <div className="flex items-center gap-2">
                                                  {item.type && (
                                                    <span className={`px-2 py-1 rounded text-xs font-medium ${
                                                      item.type === 'airac' ? 'bg-purple-100 text-purple-800' :
                                                      item.type === 'aip' ? 'bg-indigo-100 text-indigo-800' :
                                                      'bg-blue-100 text-blue-800'
                                                    }`}>
                                                      {item.type.toUpperCase()}
                                                    </span>
                                                  )}
                                                  <button
                                                    onClick={() => toggleSuplementoItem(`${fir.code}-${item.id}`)}
                                                    className="px-2 py-1 bg-white hover:bg-gray-50 text-gray-600 rounded text-xs font-medium transition-colors"
                                                  >
                                                    {expandedSuplementoItems[`${fir.code}-${item.id}`] ? '▼' : '▶'}
                                                  </button>
                                                </div>
                                              </div>
                                              
                                              {/* Status */}
                                              {item.status && (
                                                <div className="mb-3">
                                                  <span className={`px-2 py-1 rounded text-xs font-medium ${
                                                    item.status === 'em vigor' ? 'bg-green-100 text-green-800' :
                                                    item.status === 'vencido' ? 'bg-red-100 text-red-800' :
                                                    'bg-gray-100 text-gray-800'
                                                  }`}>
                                                    {item.status}
                                                  </span>
                                                </div>
                                              )}
                                              
                                              {/* Conteúdo expansível */}
                                              <div className={`transition-all duration-300 ${
                                                expandedSuplementoItems[`${fir.code}-${item.id}`] ? 'block' : 'hidden'
                                              }`}>
                                                {/* Título do Suplemento */}
                                                {item.titulo && (
                                                  <div className="mb-3">
                                                    <div className="text-xs text-gray-500 mb-1">Título:</div>
                                                    <div className="text-sm font-semibold text-purple-800">
                                                      {item.titulo}
                                                    </div>
                                                  </div>
                                                )}
                                                
                                                {/* Texto do Suplemento */}
                                                {item.texto && (
                                                  <div className="mb-3">
                                                    <div className="text-xs text-gray-500 mb-1">Descrição:</div>
                                                    <div className="text-sm text-gray-700 bg-white p-2 rounded border">
                                                      {item.texto}
                                                    </div>
                                                  </div>
                                                )}
                                                
                                                {/* Duração */}
                                                {item.duracao && (
                                                  <div className="mb-3">
                                                    <div className="text-xs text-gray-500 mb-1">Duração:</div>
                                                    <div className="text-sm text-gray-700">
                                                      {item.duracao}
                                                    </div>
                                                  </div>
                                                )}
                                                
                                                {/* Referência */}
                                                {item.ref && (
                                                  <div className="mb-3">
                                                    <div className="text-xs text-gray-500 mb-1">Referência:</div>
                                                    <div className="text-sm text-blue-600 font-medium">
                                                      {item.ref}
                                                    </div>
                                                  </div>
                                                )}
                                                
                                                {/* Número e Série */}
                                                <div className="grid grid-cols-2 gap-2 mb-3">
                                                  {item.n && (
                                                    <div>
                                                      <div className="text-xs text-gray-500 mb-1">Número:</div>
                                                      <div className="text-sm font-medium text-gray-800">
                                                        {item.n}
                                                      </div>
                                                    </div>
                                                  )}
                                                  {item.serie && (
                                                    <div>
                                                      <div className="text-xs text-gray-500 mb-1">Série:</div>
                                                      <div className="text-sm font-medium text-gray-800">
                                                        {item.serie}
                                                      </div>
                                                    </div>
                                                  )}
                                                </div>
                                                
                                                {/* Informações de Data */}
                                                <div className="space-y-2 text-xs text-gray-600 mb-3">
                                                  {item.effectiveDate && (
                                                    <div className="flex justify-between">
                                                      <span className="text-gray-500">Vigência:</span>
                                                      <span className="font-medium">
                                                        {new Date(item.effectiveDate).toLocaleDateString('pt-BR')}
                                                      </span>
                                                    </div>
                                                  )}
                                                  {item.expirationDate && (
                                                    <div className="flex justify-between">
                                                      <span className="text-gray-500">Expira:</span>
                                                      <span className={`font-medium ${
                                                        new Date(item.expirationDate) < new Date() ? 'text-red-600' : 'text-gray-800'
                                                      }`}>
                                                        {new Date(item.expirationDate).toLocaleDateString('pt-BR')}
                                                      </span>
                                                    </div>
                                                  )}
                                                  {item.createdAt && (
                                                    <div className="flex justify-between">
                                                      <span className="text-gray-500">Criado:</span>
                                                      <span className="font-medium">
                                                        {new Date(item.createdAt).toLocaleDateString('pt-BR')}
                                                      </span>
                                                    </div>
                                                  )}
                                                </div>
                                                
                                                {/* Anexo */}
                                                {item.anexo && (
                                                  <div className="mb-3">
                                                    <div className="text-xs text-gray-500 mb-1">Anexo:</div>
                                                    <div className="text-sm text-gray-700">
                                                      {item.anexo}
                                                    </div>
                                                  </div>
                                                )}
                                                
                                                {/* ID do Suplemento */}
                                                <div className="mt-3 pt-3 border-t border-purple-200">
                                                  <div className="text-xs text-gray-500">
                                                    ID: {item.id}
                                                  </div>
                                                </div>
                                              </div>
                                            </div>
                                          ))}
                                          </div>
                                          
                                          {/* Mostrar mais */}
                                          {data.items.length > 6 && (
                                            <div className="mt-4 pt-4 border-t border-gray-200 text-center">
                                              <span className="text-sm text-gray-500">
                                                ... e mais {data.items.length - 6} suplementos
                                              </span>
                                            </div>
                                          )}
                                        </div>
                                      </div>
                                    </div>
                                  )
                                })}
                              </div>
                            </div>
                          )
                        })()}

                        {/* SUPLEMENTOS (AIXM/AIP) */}
                        {aiswebData?.suplementos?.items?.length > 0 && (
                          <div className="rotaer-section md:col-span-2">
                            <h3 className="rotaer-section-title">📚 Suplementos (AIXM/AIP)</h3>
                            <div className="rotaer-list max-h-64 overflow-y-auto">
                              {aiswebData.suplementos.items.slice(0, 5).map((item: any, index: number) => (
                                <div key={`suplemento-${item.id}-${index}`} className="bg-blue-50 p-4 rounded-lg border border-blue-200 mb-3">
                                  <div className="flex justify-between items-start mb-3">
                                    <div className="font-semibold text-sm text-blue-800">
                                      {item.titulo || item.nome || `Suplemento ${index + 1}`}
                                    </div>
                                    <div className="text-xs text-gray-500 text-right">
                                      <div>Publicado: {item.dataPublicacao ? new Date(item.dataPublicacao).toLocaleDateString('pt-BR') : 'N/A'}</div>
                                      {item.dataValidade && (
                                        <div>Válido até: {new Date(item.dataValidade).toLocaleDateString('pt-BR')}</div>
                                      )}
                                    </div>
                                  </div>
                                  
                                  {/* Tipo/Categoria */}
                                  <div className="text-sm font-medium text-blue-800 mb-2">
                                    {item.tipo && (
                                      <span className={`px-2 py-1 rounded text-xs mr-2 ${
                                        item.tipo === 'AIXM' ? 'bg-purple-100 text-purple-800' :
                                        item.tipo === 'AIP' ? 'bg-indigo-100 text-indigo-800' :
                                        'bg-blue-100 text-blue-800'
                                      }`}>
                                        {item.tipo}
                                      </span>
                                    )}
                                    {item.categoria && (
                                      <span className="text-blue-700">{item.categoria}</span>
                                    )}
                                  </div>
                                  
                                  {/* Descrição */}
                                  <div className="text-sm text-gray-700 mb-3">
                                    <div className="font-medium text-blue-700 mb-1">Descrição:</div>
                                    <div className="bg-white p-2 rounded border">
                                      {item.descricao || item.observacao || 'N/A'}
                                    </div>
                                  </div>
                                  
                                  {/* Informações Técnicas */}
                                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mb-3">
                                    {item.versao && (
                                      <div className="text-xs text-gray-600">
                                        <span className="font-medium">Versão:</span> {item.versao}
                                      </div>
                                    )}
                                    {item.formato && (
                                      <div className="text-xs text-gray-600">
                                        <span className="font-medium">Formato:</span> {item.formato}
                                      </div>
                                    )}
                                    {item.tamanho && (
                                      <div className="text-xs text-gray-600">
                                        <span className="font-medium">Tamanho:</span> {item.tamanho}
                                      </div>
                                    )}
                                    {item.idioma && (
                                      <div className="text-xs text-gray-600">
                                        <span className="font-medium">Idioma:</span> {item.idioma}
                                      </div>
                                    )}
                                  </div>
                                  
                                  {/* Status e Download */}
                                  <div className="flex justify-between items-center mt-3 pt-2 border-t border-blue-200">
                                    <div className="text-xs text-gray-500">
                                      {item.fonte && <><span className="font-medium">Fonte:</span> {item.fonte}</>}
                                    </div>
                                    <div className="flex gap-2">
                                      <div className={`px-3 py-1 rounded text-xs font-medium ${
                                        item.ativo ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                                      }`}>
                                        {item.ativo ? '✅ Ativo' : '❌ Inativo'}
                                      </div>
                                      {(item.url || item.link || item.downloadUrl) && (
                                        <a 
                                          href={item.url || item.link || item.downloadUrl} 
                                          target="_blank" 
                                          rel="noopener noreferrer"
                                          className="bg-blue-600 hover:bg-blue-700 text-white text-xs px-3 py-1 rounded transition-colors"
                                        >
                                          📥 Download
                                        </a>
                                      )}
                                    </div>
                                  </div>
                                </div>
                              ))}
                            </div>
                          </div>
                        )}
                      </div>
                    )
                  })()}
                </div>
              </>
            )
          })()}
        </div>
      )}
    </section>
  )
}




