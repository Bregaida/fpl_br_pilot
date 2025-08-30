import { useQuery, useQueries, useInfiniteQuery, useQueryClient } from '@tanstack/react-query'
import { airportsApi } from '../api/airports-api'
import type { 
  Airport, 
  AirportDetails, 
  Chart, 
  Frequency, 
  Navaid, 
  Notam, 
  Runway,
  SearchAirportsParams
} from '../types/airport'

export const AIRPORTS_QUERY_KEYS = {
  all: ['airports'] as const,
  lists: () => [...AIRPORTS_QUERY_KEYS.all, 'list'] as const,
  list: (filters: SearchAirportsParams) => 
    [...AIRPORTS_QUERY_KEYS.lists(), { filters }] as const,
  details: () => [...AIRPORTS_QUERY_KEYS.all, 'detail'] as const,
  detail: (icao: string) => [...AIRPORTS_QUERY_KEYS.details(), icao] as const,
  runways: (icao: string) => [...AIRPORTS_QUERY_KEYS.detail(icao), 'runways'] as const,
  frequencies: (icao: string) => [...AIRPORTS_QUERY_KEYS.detail(icao), 'frequencies'] as const,
  navaids: (icao: string) => [...AIRPORTS_QUERY_KEYS.detail(icao), 'navaids'] as const,
  notams: (icao: string) => [...AIRPORTS_QUERY_KEYS.detail(icao), 'notams'] as const,
  charts: (icao: string) => [...AIRPORTS_QUERY_KEYS.detail(icao), 'charts'] as const,
  chartCategories: (icao: string) => 
    [...AIRPORTS_QUERY_KEYS.charts(icao), 'categories'] as const,
  chartCategory: (icao: string, category: string) => 
    [...AIRPORTS_QUERY_KEYS.chartCategories(icao), category] as const,
  chart: (id: string) => [...AIRPORTS_QUERY_KEYS.all, 'chart', id] as const,
  nearby: () => [...AIRPORTS_QUERY_KEYS.all, 'nearby'] as const,
  nearbyAirports: (lat: number, lon: number, radiusNm: number) => 
    [...AIRPORTS_QUERY_KEYS.nearby(), { lat, lon, radiusNm }] as const,
  countries: () => [...AIRPORTS_QUERY_KEYS.all, 'countries'] as const,
  countryAirports: (countryCode: string) => 
    [...AIRPORTS_QUERY_KEYS.countries(), countryCode] as const,
  searchNotams: (params: any) => 
    [...AIRPORTS_QUERY_KEYS.all, 'notams', 'search', { ...params }] as const,
  notam: (id: string) => [...AIRPORTS_QUERY_KEYS.all, 'notam', id] as const,
}

// Airport Queries
export const useSearchAirports = (params: SearchAirportsParams = {}, enabled = true) => {
  return useQuery({
    queryKey: AIRPORTS_QUERY_KEYS.list(params),
    queryFn: () => airportsApi.searchAirports(params),
    enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
  })
}

export const useAirportDetails = (icao: string, enabled = true) => {
  return useQuery<AirportDetails>({
    queryKey: AIRPORTS_QUERY_KEYS.detail(icao),
    queryFn: () => airportsApi.getAirportDetails(icao),
    enabled: !!icao && enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
  })
}

export const useAirportRunways = (icao: string, enabled = true) => {
  return useQuery<Runway[]>({
    queryKey: AIRPORTS_QUERY_KEYS.runways(icao),
    queryFn: () => airportsApi.getAirportRunways(icao),
    enabled: !!icao && enabled,
    staleTime: 15 * 60 * 1000, // 15 minutes
  })
}

export const useAirportFrequencies = (icao: string, enabled = true) => {
  return useQuery<Frequency[]>({
    queryKey: AIRPORTS_QUERY_KEYS.frequencies(icao),
    queryFn: () => airportsApi.getAirportFrequencies(icao),
    enabled: !!icao && enabled,
    staleTime: 15 * 60 * 1000, // 15 minutes
  })
}

export const useAirportNavaids = (icao: string, enabled = true) => {
  return useQuery<Navaid[]>({
    queryKey: AIRPORTS_QUERY_KEYS.navaids(icao),
    queryFn: () => airportsApi.getAirportNavaids(icao),
    enabled: !!icao && enabled,
    staleTime: 15 * 60 * 1000, // 15 minutes
  })
}

export const useAirportNotams = (icao: string, enabled = true) => {
  return useQuery<Notam[]>({
    queryKey: AIRPORTS_QUERY_KEYS.notams(icao),
    queryFn: () => airportsApi.getAirportNotams(icao),
    enabled: !!icao && enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
    refetchInterval: 15 * 60 * 1000, // 15 minutes
  })
}

export const useAirportCharts = (icao: string, enabled = true) => {
  return useQuery<Chart[]>({
    queryKey: AIRPORTS_QUERY_KEYS.charts(icao),
    queryFn: () => airportsApi.getAirportCharts(icao),
    enabled: !!icao && enabled,
    staleTime: 60 * 60 * 1000, // 1 hour
  })
}

export const useChartsByCategory = (icao: string, category: string, enabled = true) => {
  return useQuery<Chart[]>({
    queryKey: AIRPORTS_QUERY_KEYS.chartCategory(icao, category),
    queryFn: () => airportsApi.getChartsByCategory(icao, category),
    enabled: !!icao && !!category && enabled,
    staleTime: 60 * 60 * 1000, // 1 hour
  })
}

export const useChart = (id: string, enabled = true) => {
  return useQuery<Chart>({
    queryKey: AIRPORTS_QUERY_KEYS.chart(id),
    queryFn: () => airportsApi.getChart(id),
    enabled: !!id && enabled,
    staleTime: 60 * 60 * 1000, // 1 hour
  })
}

// Location-based Queries
export const useNearbyAirports = (lat: number, lon: number, radiusNm: number = 50, enabled = true) => {
  return useQuery<Airport[]>({
    queryKey: AIRPORTS_QUERY_KEYS.nearbyAirports(lat, lon, radiusNm),
    queryFn: () => airportsApi.getNearbyAirports(lat, lon, radiusNm),
    enabled: (lat !== undefined && lon !== undefined) && enabled,
    staleTime: 15 * 60 * 1000, // 15 minutes
  })
}

// Country Queries
export const useCountriesWithAirports = (enabled = true) => {
  return useQuery<{code: string, name: string, count: number}[]>({
    queryKey: AIRPORTS_QUERY_KEYS.countries(),
    queryFn: () => airportsApi.getCountriesWithAirports(),
    enabled,
    staleTime: 24 * 60 * 60 * 1000, // 24 hours
  })
}

export const useAirportsByCountry = (countryCode: string, enabled = true) => {
  return useQuery<Airport[]>({
    queryKey: AIRPORTS_QUERY_KEYS.countryAirports(countryCode),
    queryFn: () => airportsApi.getAirportsByCountry(countryCode),
    enabled: !!countryCode && enabled,
    staleTime: 24 * 60 * 60 * 1000, // 24 hours
  })
}

// NOTAM Queries
export const useSearchNotams = (params: any = {}, enabled = true) => {
  return useQuery<{data: Notam[], total: number}>({
    queryKey: AIRPORTS_QUERY_KEYS.searchNotams(params),
    queryFn: () => airportsApi.searchNotams(params),
    enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
  })
}

export const useNotam = (id: string, enabled = true) => {
  return useQuery<Notam>({
    queryKey: AIRPORTS_QUERY_KEYS.notam(id),
    queryFn: () => airportsApi.getNotam(id),
    enabled: !!id && enabled,
    staleTime: 5 * 60 * 1000, // 5 minutes
  })
}

// Combined Hooks
export const useAirportAllData = (icao: string, enabled = true) => {
  const queries = useQueries({
    queries: [
      {
        ...useAirportDetails(icao, enabled),
        queryKey: AIRPORTS_QUERY_KEYS.detail(icao),
      },
      {
        ...useAirportRunways(icao, enabled),
        queryKey: AIRPORTS_QUERY_KEYS.runways(icao),
      },
      {
        ...useAirportFrequencies(icao, enabled),
        queryKey: AIRPORTS_QUERY_KEYS.frequencies(icao),
      },
      {
        ...useAirportNotams(icao, enabled),
        queryKey: AIRPORTS_QUERY_KEYS.notams(icao),
      },
      {
        ...useAirportCharts(icao, enabled),
        queryKey: AIRPORTS_QUERY_KEYS.charts(icao),
      },
    ],
  })

  const [details, runways, frequencies, notams, charts] = queries
  
  return {
    details: details.data,
    runways: runways.data,
    frequencies: frequencies.data,
    notams: notams.data,
    charts: charts.data,
    isLoading: queries.some(query => query.isLoading),
    isError: queries.some(query => query.isError),
    error: queries.find(query => query.error)?.error,
    refetchAll: () => {
      queries.forEach(query => query.refetch())
    },
  }
}
