export interface Airport {
  icao: string
  iata?: string
  name: string
  city: string
  country: string
  countryCode: string
  latitude: number
  longitude: number
  elevation: number
  timezone: string
  type: 'large_airport' | 'medium_airport' | 'small_airport' | 'heliport' | 'closed'
  status: 'operational' | 'closed' | 'military'
  continent: string
  municipality?: string
  scheduledService: boolean
  gpsCode?: string
  localCode?: string
  homeLink?: string
  wikipediaLink?: string
  keywords?: string[]
  lastUpdated: string
}

export interface Runway {
  id: string
  airportIcao: string
  airportIdent: string
  lengthFt: number
  widthFt: number
  surface: string
  lighted: boolean
  closed: boolean
  leIdent?: string
  leLatitude?: number
  leLongitude?: number
  leElevationFt?: number
  leHeadingDegT?: number
  leDisplacedThresholdFt?: number
  heIdent?: string
  heLatitude?: number
  heLongitude?: number
  heElevationFt?: number
  heHeadingDegT?: number
  heDisplacedThresholdFt?: number
}

export interface Frequency {
  id: string
  airportIcao: string
  type: string
  description: string
  frequencyMhz: number
  frequencyKhz?: number
}

export interface Navaid {
  id: string
  filename: string
  ident: string
  name: string
  type: string
  frequencyKhz: number
  latitude: number
  longitude: number
  elevation: number
  isoCountry: string
  dmeFrequencyKhz?: number
  dmeChannel?: string
  dmeLatitude?: number
  dmeLongitude?: number
  dmeElevation?: number
  slavedVariation?: number
  magneticVariation?: number
  usageType?: string
  power?: string
  associatedAirportIcao?: string
}

export interface Notam {
  id: string
  icaoId: string
  notamNumber: string
  type: 'NEW' | 'REPLACE' | 'CANCEL'
  refNotamId?: string
  fir: string
  notamCode: string
  trafficType: string[]
  purpose: string[]
  scope: string[]
  qLowerLimit?: number
  qUpperLimit?: number
  location: string
  validFrom: string
  validUntil: string
  schedule?: string
  body: string
  lowerLimit?: string
  upperLimit?: string
  created: string
  source: string
}

export interface Chart {
  id: string
  airportIcao: string
  chartName: string
  chartCode: string
  category: 'ARRIVAL' | 'DEPARTURE' | 'AIRPORT' | 'APPROACH' | 'OTHER'
  icaoAirportCode: string
  iataAirportCode?: string
  icaoAirportName: string
  iataCountryCode: string
  section: string
  subSection?: string
  fileExtension: string
  fileUrl: string
  fileSize: number
  fileDate: string
  pdfName: string
  pdfType: string
  pdfSize: string
  pdfDate: string
  pdfUrl: string
  thumbnailUrl?: string
  created: string
  lastUpdated: string
}

export interface AirportDetails extends Airport {
  runways: Runway[]
  frequencies: Frequency[]
  navaids: Navaid[]
  notams: Notam[]
  charts: Chart[]
  weather?: {
    metar?: any
    taf?: any
  }
}
