export interface MetarData {
  raw: string
  station: string
  time: string
  wind: {
    degrees: number
    speed: number
    gust?: number
    unit: 'KT' | 'MPS'
  }
  visibility: {
    value: number
    unit: 'M' | 'KM' | 'SM'
  }
  weather: string[]
  clouds: Array<{
    type: string
    altitude: number
    modifier?: string
  }>
  temperature: number
  dewpoint: number
  qnh: number
  trend?: string
}

export interface TafData {
  raw: string
  station: string
  issueTime: string
  validFrom: string
  validTo: string
  forecast: Array<{
    type: 'BECMG' | 'TEMPO' | 'FM' | 'PROB'
    from?: string
    to?: string
    probability?: number
    wind?: {
      degrees: number
      speed: number
      gust?: number
      unit: 'KT' | 'MPS'
    }
    visibility?: {
      value: number
      unit: 'M' | 'KM' | 'SM'
    }
    weather?: string[]
    clouds?: Array<{
      type: string
      altitude: number
      modifier?: string
    }>
    windShear?: {
      height: number
      degrees: number
      speed: number
      unit: 'FT' | 'M'
    }
  }>
}

export interface WeatherBriefing {
  metar: MetarData
  taf: TafData
  sigmet?: Array<{
    id: string
    validFrom: string
    validTo: string
    fir: string
    description: string
  }>
  airmet?: Array<{
    id: string
    validFrom: string
    validTo: string
    fir: string
    description: string
  }>
  notam?: Array<{
    id: string
    validFrom: string
    validTo: string
    location: string
    description: string
  }>
}
