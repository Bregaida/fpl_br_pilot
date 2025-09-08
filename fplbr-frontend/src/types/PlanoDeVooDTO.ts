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
  eet?: string // ex: "SBC0005 SBBS0150"
  opr: string
  from: string // ICAO 4 letras
  per?: string[] // PVC: A, B, C, D, E, H
  rmk?: string
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


