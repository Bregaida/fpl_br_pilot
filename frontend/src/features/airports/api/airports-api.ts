import { api } from "@/shared/lib/api"
import { 
  Airport, 
  AirportDetails, 
  Chart, 
  Frequency, 
  Navaid, 
  Notam, 
  Runway 
} from "../types/airport"

interface SearchAirportsParams {
  query?: string
  country?: string
  type?: string
  limit?: number
  offset?: number
}

export const airportsApi = {
  /**
   * Search for airports based on various criteria
   */
  searchAirports: async (params: SearchAirportsParams = {}): Promise<{data: Airport[], total: number}> => {
    try {
      const response = await api.get<{data: Airport[], total: number}>('/api/v1/airports/search', { params })
      return response.data
    } catch (error) {
      console.error('Error searching airports:', error)
      throw error
    }
  },

  /**
   * Get detailed information about a specific airport by ICAO code
   */
  getAirportDetails: async (icao: string): Promise<AirportDetails> => {
    try {
      const response = await api.get<AirportDetails>(`/api/v1/airports/${icao}`)
      return response.data
    } catch (error) {
      console.error(`Error getting airport details for ${icao}:`, error)
      throw error
    }
  },

  /**
   * Get runways for a specific airport
   */
  getAirportRunways: async (icao: string): Promise<Runway[]> => {
    try {
      const response = await api.get<Runway[]>(`/api/v1/airports/${icao}/runways`)
      return response.data
    } catch (error) {
      console.error(`Error getting runways for ${icao}:`, error)
      throw error
    }
  },

  /**
   * Get frequencies for a specific airport
   */
  getAirportFrequencies: async (icao: string): Promise<Frequency[]> => {
    try {
      const response = await api.get<Frequency[]>(`/api/v1/airports/${icao}/frequencies`)
      return response.data
    } catch (error) {
      console.error(`Error getting frequencies for ${icao}:`, error)
      throw error
    }
  },

  /**
   * Get navaids for a specific airport
   */
  getAirportNavaids: async (icao: string): Promise<Navaid[]> => {
    try {
      const response = await api.get<Navaid[]>(`/api/v1/airports/${icao}/navaids`)
      return response.data
    } catch (error) {
      console.error(`Error getting navaids for ${icao}:`, error)
      throw error
    }
  },

  /**
   * Get NOTAMs for a specific airport
   */
  getAirportNotams: async (icao: string): Promise<Notam[]> => {
    try {
      const response = await api.get<Notam[]>(`/api/v1/airports/${icao}/notams`)
      return response.data
    } catch (error) {
      console.error(`Error getting NOTAMs for ${icao}:`, error)
      throw error
    }
  },

  /**
   * Get charts for a specific airport
   */
  getAirportCharts: async (icao: string): Promise<Chart[]> => {
    try {
      const response = await api.get<Chart[]>(`/api/v1/airports/${icao}/charts`)
      return response.data
    } catch (error) {
      console.error(`Error getting charts for ${icao}:`, error)
      throw error
    }
  },

  /**
   * Get nearby airports within a certain radius (in nautical miles)
   */
  getNearbyAirports: async (lat: number, lon: number, radiusNm: number = 50): Promise<Airport[]> => {
    try {
      const response = await api.get<Airport[]>('/api/v1/airports/nearby', {
        params: { lat, lon, radiusNm }
      })
      return response.data
    } catch (error) {
      console.error('Error getting nearby airports:', error)
      throw error
    }
  },

  /**
   * Get airports by country code
   */
  getAirportsByCountry: async (countryCode: string): Promise<Airport[]> => {
    try {
      const response = await api.get<Airport[]>(`/api/v1/airports/country/${countryCode}`)
      return response.data
    } catch (error) {
      console.error(`Error getting airports for country ${countryCode}:`, error)
      throw error
    }
  },

  /**
   * Get all countries with airports
   */
  getCountriesWithAirports: async (): Promise<{code: string, name: string, count: number}[]> => {
    try {
      const response = await api.get<{code: string, name: string, count: number}[]>('/api/v1/airports/countries')
      return response.data
    } catch (error) {
      console.error('Error getting countries with airports:', error)
      throw error
    }
  },

  /**
   * Search NOTAMs with various filters
   */
  searchNotams: async (params: {
    icao?: string
    fir?: string
    type?: string
    startDate?: string
    endDate?: string
    limit?: number
    offset?: number
  }): Promise<{data: Notam[], total: number}> => {
    try {
      const response = await api.get<{data: Notam[], total: number}>('/api/v1/notams/search', { params })
      return response.data
    } catch (error) {
      console.error('Error searching NOTAMs:', error)
      throw error
    }
  },

  /**
   * Get a specific NOTAM by ID
   */
  getNotam: async (id: string): Promise<Notam> => {
    try {
      const response = await api.get<Notam>(`/api/v1/notams/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error getting NOTAM ${id}:`, error)
      throw error
    }
  },

  /**
   * Get charts by category
   */
  getChartsByCategory: async (icao: string, category: string): Promise<Chart[]> => {
    try {
      const response = await api.get<Chart[]>(`/api/v1/airports/${icao}/charts/category/${category}`)
      return response.data
    } catch (error) {
      console.error(`Error getting ${category} charts for ${icao}:`, error)
      throw error
    }
  },

  /**
   * Get chart by ID
   */
  getChart: async (id: string): Promise<Chart> => {
    try {
      const response = await api.get<Chart>(`/api/v1/charts/${id}`)
      return response.data
    } catch (error) {
      console.error(`Error getting chart ${id}:`, error)
      throw error
    }
  }
}
