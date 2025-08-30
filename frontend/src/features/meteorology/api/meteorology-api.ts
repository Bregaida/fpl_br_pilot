import { api } from "@/shared/lib/api"
import { MetarData, TafData, WeatherBriefing } from "../types/metar-taf"

export const meteorologyApi = {
  /**
   * Get METAR data for a specific airport
   * @param icaoCode The ICAO code of the airport
   * @returns METAR data
   */
  getMetar: async (icaoCode: string): Promise<MetarData> => {
    try {
      const response = await api.get<MetarData>(`/api/v1/meteorology/metar/${icaoCode}`)
      return response.data
    } catch (error) {
      console.error('Error fetching METAR data:', error)
      throw error
    }
  },

  /**
   * Get TAF data for a specific airport
   * @param icaoCode The ICAO code of the airport
   * @returns TAF data
   */
  getTaf: async (icaoCode: string): Promise<TafData> => {
    try {
      const response = await api.get<TafData>(`/api/v1/meteorology/taf/${icaoCode}`)
      return response.data
    } catch (error) {
      console.error('Error fetching TAF data:', error)
      throw error
    }
  },

  /**
   * Get weather briefing for a specific airport
   * @param icaoCode The ICAO code of the airport
   * @returns Complete weather briefing including METAR, TAF, SIGMET, AIRMET, and NOTAMs
   */
  getWeatherBriefing: async (icaoCode: string): Promise<WeatherBriefing> => {
    try {
      const response = await api.get<WeatherBriefing>(`/api/v1/meteorology/briefing/${icaoCode}`)
      return response.data
    } catch (error) {
      console.error('Error fetching weather briefing:', error)
      throw error
    }
  },

  /**
   * Get METAR data for multiple airports
   * @param icaoCodes Array of ICAO codes
   * @returns Record of METAR data keyed by ICAO code
   */
  getMultipleMetars: async (icaoCodes: string[]): Promise<Record<string, MetarData>> => {
    try {
      const response = await api.post<Record<string, MetarData>>('/api/v1/meteorology/multiple-metars', { icaoCodes })
      return response.data
    } catch (error) {
      console.error('Error fetching multiple METARs:', error)
      throw error
    }
  },

  /**
   * Get TAF data for multiple airports
   * @param icaoCodes Array of ICAO codes
   * @returns Record of TAF data keyed by ICAO code
   */
  getMultipleTafs: async (icaoCodes: string[]): Promise<Record<string, TafData>> => {
    try {
      const response = await api.post<Record<string, TafData>>('/api/v1/meteorology/multiple-tafs', { icaoCodes })
      return response.data
    } catch (error) {
      console.error('Error fetching multiple TAFs:', error)
      throw error
    }
  },

  /**
   * Get SIGMETs for a specific region or FIR
   * @param fir Flight Information Region (e.g., 'SBBS' for Brasilia FIR)
   * @returns Array of SIGMETs
   */
  getSigmet: async (fir: string) => {
    try {
      const response = await api.get(`/api/v1/meteorology/sigmet/${fir}`)
      return response.data
    } catch (error) {
      console.error('Error fetching SIGMETs:', error)
      throw error
    }
  },

  /**
   * Get AIRMETs for a specific region or FIR
   * @param fir Flight Information Region (e.g., 'SBBS' for Brasilia FIR)
   * @returns Array of AIRMETs
   */
  getAirmet: async (fir: string) => {
    try {
      const response = await api.get(`/api/v1/meteorology/airmet/${fir}`)
      return response.data
    } catch (error) {
      console.error('Error fetching AIRMETs:', error)
      throw error
    }
  },

  /**
   * Get weather radar image for a specific region
   * @param region Region code (e.g., 'br' for Brazil)
   * @returns URL to the weather radar image
   */
  getWeatherRadar: async (region: string): Promise<string> => {
    try {
      const response = await api.get(`/api/v1/meteorology/radar/${region}`)
      return response.data.imageUrl
    } catch (error) {
      console.error('Error fetching weather radar:', error)
      throw error
    }
  },

  /**
   * Get satellite image for a specific region
   * @param region Region code (e.g., 'south-america')
   * @param type Image type (e.g., 'visible', 'infrared', 'water-vapor')
   * @returns URL to the satellite image
   */
  getSatelliteImage: async (region: string, type: string = 'infrared'): Promise<string> => {
    try {
      const response = await api.get(`/api/v1/meteorology/satellite/${region}?type=${type}`)
      return response.data.imageUrl
    } catch (error) {
      console.error('Error fetching satellite image:', error)
      throw error
    }
  }
}
