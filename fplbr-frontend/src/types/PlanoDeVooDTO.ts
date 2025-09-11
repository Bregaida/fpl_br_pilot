export type ModoPlano = 'PVC' | 'PVS'

export interface BotesInfo {
  possui: boolean
  numero?: number
  capacidade?: number
  c?: boolean
  cor?: string
}

export interface InformacaoSuplementar {
  autonomia: string // HHmm
  pob: number // Pessoas a bordo (máximo 3 dígitos)
  radioEmergencia?: string[] // U, V, E
  sobrevivencia?: string[] // S, P, D, M, J
  coletes?: string[] // se incluir J, habilita demais opções L,F,U,V
  botes?: BotesInfo
  corEMarcaAeronave: string
  observacoes?: string
  pilotoEmComando: string
  anacPrimeiroPiloto: string
  anacSegundoPiloto?: string
  telefone: string // 10-11 dígitos
  n?: boolean // se true, observações obrigatório
}

export interface OutrasInformacoes {
  sts?: string // Status especial
  pbn?: string // Performance Based Navigation
  nav?: string // Navegação
  com?: string // Comunicação
  dat?: string // Dados
  sur?: string // Vigilância
  dep?: string // Aeródromo de partida
  dest?: string // Aeródromo de destino
  reg?: string // Registro da aeronave
  eet?: string // ex: "SBC0005 SBBS0150"
  sel?: string // SELCAL
  typ?: string // Tipo de aeronave
  code?: string // Código
  dle?: string // Delay
  opr: string // Operador
  orgn?: string // Origem
  per?: string[] // PVC: A, B, C, D, E, H
  altn?: string // Alternativo
  ralt?: string // Rota alternativa
  talt?: string // Takeoff alternativo
  rif?: string // Route if
  rmk?: string // Observações
  from: string // ICAO 4 letras
  dof: string // ddmmaa
}

export interface PlanoDeVooDTO {
  identificacaoDaAeronave: string
  indicativoDeChamada: boolean
  regraDeVooEnum: 'I' | 'V' | 'Y' | 'Z'
  tipoDeVooEnum: 'G' | 'S' | 'N' | 'M' | 'X'
  numeroDeAeronaves: number
  tipoDeAeronave: string
  categoriaEsteiraTurbulenciaEnum: 'L' | 'M' | 'H' | 'J'
  equipamentoCapacidadeDaAeronave: string[]
  vigilancia: string[]
  aerodromoDePartida: string
  horaPartida: string // ISO UTC
  aerodromoDeDestino: string
  tempoDeVooPrevisto: string // HHmm
  aerodromoDeAlternativa: string
  aerodromoDeAlternativaSegundo?: string
  velocidadeDeCruzeiro: string
  nivelDeVoo: string // VFR | Axxx | Fxxx
  rota: string
  outrasInformacoes: OutrasInformacoes
  informacaoSuplementar: InformacaoSuplementar
  modo: ModoPlano
}


