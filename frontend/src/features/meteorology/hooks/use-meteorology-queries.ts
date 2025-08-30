import { useQuery, useQueries } from "@tanstack/react-query"
import { meteorologyApi } from "../api/meteorology-api"
import type { MetarData, TafData, WeatherBriefing } from "../types/metar-taf"

export const METEOROLOGY_QUERY_KEYS = {
  all: ['meteorology'] as const,
  metar: (icao: string) => [...METEOROLOGY_QUERY_KEYS.all, 'metar', icao] as const,
  taf: (icao: string) => [...METEOROLOGY_QUERY_KEYS.all, 'taf', icao] as const,
  briefing: (icao: string) => [...METEOROLOGY_QUERY_KEYS.all, 'briefing', icao] as const,
  multipleMetars: (icaoCodes: string[]) => [...METEOROLOGY_QUERY_KEYS.all, 'multiple-metars', ...icaoCodes] as const,
  multipleTafs: (icaoCodes: string[]) => [...METEOROLOGY_QUERY_KEYS.all, 'multiple-tafs', ...icaoCodes] as const,
  sigmet: (fir: string) => [...METEOROLOGY_QUERY_KEYS.all, 'sigmet', fir] as const,
  airmet: (fir: string) => [...METEOROLOGY_QUERY_KEYS.all, 'airmet', fir] as const,
  radar: (region: string) => [...METEOROLOGY_QUERY_KEYS.all, 'radar', region] as const,
  satellite: (region: string, type: string) => 
    [...METEOROLOGY_QUERY_KEYS.all, 'satellite', region, type] as const,
}

// Hooks for single airport data
export const useMetar = (icaoCode: string, enabled = true) => {
  return useQuery<MetarData>({
    queryKey: METEOROLOGY_QUERY_KEYS.metar(icaoCode),
    queryFn: () => meteorologyApi.getMetar(icaoCode),
    enabled: !!icaoCode && enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchInterval: 15 * 60 * 1000, // 15 minutes
  })
}

export const useTaf = (icaoCode: string, enabled = true) => {
  return useQuery<TafData>({
    queryKey: METEOROLOGY_QUERY_KEYS.taf(icaoCode),
    queryFn: () => meteorologyApi.getTaf(icaoCode),
    enabled: !!icaoCode && enabled,
    staleTime: 15 * 60 * 1000, // 15 minutes
    refetchInterval: 30 * 60 * 1000, // 30 minutes
  })
}

export const useWeatherBriefing = (icaoCode: string, enabled = true) => {
  return useQuery<WeatherBriefing>({
    queryKey: METEOROLOGY_QUERY_KEYS.briefing(icaoCode),
    queryFn: () => meteorologyApi.getWeatherBriefing(icaoCode),
    enabled: !!icaoCode && enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchInterval: 15 * 60 * 1000, // 15 minutes
  })
}

// Hooks for multiple airports data
export const useMultipleMetars = (icaoCodes: string[], enabled = true) => {
  return useQuery<Record<string, MetarData>>({
    queryKey: METEOROLOGY_QUERY_KEYS.multipleMetars(icaoCodes),
    queryFn: () => meteorologyApi.getMultipleMetars(icaoCodes),
    enabled: icaoCodes.length > 0 && enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchInterval: 15 * 60 * 1000, // 15 minutes
  })
}

export const useMultipleTafs = (icaoCodes: string[], enabled = true) => {
  return useQuery<Record<string, TafData>>({
    queryKey: METEOROLOGY_QUERY_KEYS.multipleTafs(icaoCodes),
    queryFn: () => meteorologyApi.getMultipleTafs(icaoCodes),
    enabled: icaoCodes.length > 0 && enabled,
    staleTime: 15 * 60 * 1000, // 15 minutes
    refetchInterval: 30 * 60 * 1000, // 30 minutes
  })
}

// Hooks for regional data
export const useSigmet = (fir: string, enabled = true) => {
  return useQuery({
    queryKey: METEOROLOGY_QUERY_KEYS.sigmet(fir),
    queryFn: () => meteorologyApi.getSigmet(fir),
    enabled: !!fir && enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchInterval: 15 * 60 * 1000, // 15 minutes
  })
}

export const useAirmet = (fir: string, enabled = true) => {
  return useQuery({
    queryKey: METEOROLOGY_QUERY_KEYS.airmet(fir),
    queryFn: () => meteorologyApi.getAirmet(fir),
    enabled: !!fir && enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchInterval: 15 * 60 * 1000, // 15 minutes
  })
}

// Hooks for weather imagery
export const useWeatherRadar = (region: string, enabled = true) => {
  return useQuery<string>({
    queryKey: METEOROLOGY_QUERY_KEYS.radar(region),
    queryFn: () => meteorologyApi.getWeatherRadar(region),
    enabled: !!region && enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchInterval: 10 * 60 * 1000, // 10 minutes
  })
}

export const useSatelliteImage = (region: string, type: string = 'infrared', enabled = true) => {
  return useQuery<string>({
    queryKey: METEOROLOGY_QUERY_KEYS.satellite(region, type),
    queryFn: () => meteorologyApi.getSatelliteImage(region, type),
    enabled: !!region && enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchInterval: 10 * 60 * 1000, // 10 minutes
  })
}

// Hook to get all weather data for a specific airport
export const useAirportWeather = (icaoCode: string, enabled = true) => {
  return useQueries({
    queries: [
      {
        ...useMetar(icaoCode, enabled),
        queryKey: METEOROLOGY_QUERY_KEYS.metar(icaoCode),
      },
      {
        ...useTaf(icaoCode, enabled),
        queryKey: METEOROLOGY_QUERY_KEYS.taf(icaoCode),
      },
    ],
    combine: (results) => {
      const [metar, taf] = results
      return {
        metar: metar.data,
        taf: taf.data,
        isLoading: metar.isLoading || taf.isLoading,
        isError: metar.isError || taf.isError,
        error: metar.error || taf.error,
        refetch: () => {
          metar.refetch()
          taf.refetch()
        },
      }
    },
  })
}
