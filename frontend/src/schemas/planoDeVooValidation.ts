import { z } from 'zod'

// Schema para Plano de Voo
export const planoDeVooSchema = z.object({
  identificacaoDaAeronave: z.string().min(1, 'Identificação da aeronave é obrigatória'),
  indicativoDeChamada: z.boolean(),
  regraDeVooEnum: z.enum(['V', 'I']),
  tipoDeVooEnum: z.enum(['G', 'M', 'S', 'N', 'X']),
  numeroDeAeronaves: z.number().min(1, 'Número de aeronaves deve ser pelo menos 1'),
  tipoDeAeronave: z.string().min(1, 'Tipo de aeronave é obrigatório'),
  categoriaEsteiraTurbulenciaEnum: z.enum(['L', 'M', 'H', 'J']),
  equipamentoCapacidadeDaAeronave: z.array(z.string()).min(1, 'Pelo menos um equipamento deve ser selecionado'),
  vigilancia: z.array(z.string()).min(1, 'Pelo menos uma vigilância deve ser selecionada'),
  aerodromoDePartida: z.string().min(1, 'Aeródromo de partida é obrigatório'),
  horaPartida: z.string().min(1, 'Hora de partida é obrigatória'),
  aerodromoDeDestino: z.string().min(1, 'Aeródromo de destino é obrigatório'),
  tempoDeVooPrevisto: z.string().min(1, 'Tempo de voo previsto é obrigatório'),
  aerodromoDeAlternativa: z.string().optional(),
  velocidadeDeCruzeiro: z.string().min(1, 'Velocidade de cruzeiro é obrigatória'),
  nivelDeVoo: z.string().min(1, 'Nível de voo é obrigatório'),
  rota: z.string().min(1, 'Rota é obrigatória'),
  outrasInformacoes: z.object({
    opr: z.string().optional(),
    from: z.string().optional(),
    dof: z.string().optional(),
    rmk: z.string().optional()
  }),
  informacaoSuplementar: z.object({
    autonomia: z.string().optional(),
    corEMarcaAeronave: z.string().optional(),
    pilotoEmComando: z.string().optional(),
    anacPrimeiroPiloto: z.string().optional(),
    telefone: z.string().optional()
  }),
  modo: z.enum(['PVS', 'PVA', 'PVF'])
})

export type PlanoDeVooFormData = z.infer<typeof planoDeVooSchema>
