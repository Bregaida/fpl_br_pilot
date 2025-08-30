import { FplFormValues, FplSimplifiedFormValues } from "../schemas"

export interface FplPreviewResponse {
  mensagemFpl: string
  mensagemAbreviada: string
  dadosFpl: Record<string, unknown>
  dataHoraGeracao: string
}

export interface ApiError {
  message: string
  statusCode: number
  error?: string
  timestamp?: string
  path?: string
}

export type FplPreviewRequest = Omit<FplFormValues, 'alternates' | 'equipment' | 'surveillanceEquipment' | 'pbn' | 'nav' | 'com' | 'dat' | 'sur' | 'remarks' | 'endurance' | 'emergencyRadio' | 'survivalEquipment' | 'lifeJackets' | 'dinghies' | 'aircraftColor' | 'contactNumber'> & {
  alternates?: string
  equipment?: string
  surveillanceEquipment?: string
  pbn?: string
  nav?: string
  com?: string
  dat?: string
  sur?: string
  remarks?: string
  endurance?: string
  emergencyRadio?: string
  survivalEquipment?: string
  lifeJackets?: string
  dinghies?: string
  aircraftColor?: string
  contactNumber?: string
}

export type FplSimplifiedPreviewRequest = FplSimplifiedFormValues & {
  alternates?: string
  equipment?: string
  survivalEquipment?: string
  lifeJackets?: string
  aircraftColor?: string
  contactNumber?: string
}
