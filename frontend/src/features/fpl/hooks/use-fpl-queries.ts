import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { fplApi } from "../api/fpl-api"
import { FplPreviewRequest, FplSimplifiedPreviewRequest } from "../types/api"

export const FPL_QUERY_KEYS = {
  all: ['fpl'] as const,
  lists: () => [...FPL_QUERY_KEYS.all, 'list'] as const,
  list: (filters: Record<string, unknown>) => 
    [...FPL_QUERY_KEYS.lists(), { filters }] as const,
  details: () => [...FPL_QUERY_KEYS.all, 'detail'] as const,
  detail: (id: string) => [...FPL_QUERY_KEYS.details(), id] as const,
  status: (id: string) => [...FPL_QUERY_KEYS.detail(id), 'status'] as const,
}

// Hooks for FPL preview
export const useGenerateFplPreview = () => {
  return useMutation({
    mutationFn: (data: FplPreviewRequest) => 
      fplApi.generatePreview(data),
  })
}

export const useGenerateSimplifiedFplPreview = () => {
  return useMutation({
    mutationFn: (data: FplSimplifiedPreviewRequest) => 
      fplApi.generateSimplifiedPreview(data),
  })
}

// Hooks for FPL submission
export const useSubmitFpl = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (data: FplPreviewRequest) => 
      fplApi.submitFpl(data),
    onSuccess: () => {
      // Invalidate the recent FPLs query to refetch the list
      queryClient.invalidateQueries({ 
        queryKey: FPL_QUERY_KEYS.lists() 
      })
    },
  })
}

export const useSubmitSimplifiedFpl = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (data: FplSimplifiedPreviewRequest) => 
      fplApi.submitSimplifiedFpl(data),
    onSuccess: () => {
      // Invalidate the recent FPLs query to refetch the list
      queryClient.invalidateQueries({ 
        queryKey: FPL_QUERY_KEYS.lists() 
      })
    },
  })
}

// Hooks for fetching FPL data
export const useRecentFpls = () => {
  return useQuery({
    queryKey: FPL_QUERY_KEYS.lists(),
    queryFn: () => fplApi.getRecentFpls(),
    staleTime: 5 * 60 * 1000, // 5 minutes
  })
}

export const useFplDetails = (fplId: string) => {
  return useQuery({
    queryKey: FPL_QUERY_KEYS.detail(fplId),
    queryFn: () => fplApi.getFplDetails(fplId),
    enabled: !!fplId,
  })
}

export const useFplStatus = (fplId: string) => {
  return useQuery({
    queryKey: FPL_QUERY_KEYS.status(fplId),
    queryFn: () => fplApi.getFplStatus(fplId),
    enabled: !!fplId,
    refetchInterval: 30 * 1000, // Poll every 30 seconds
  })
}

// Hooks for FPL actions
export const useCancelFpl = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (fplId: string) => fplApi.cancelFpl(fplId),
    onSuccess: (_, fplId) => {
      // Invalidate both the specific FPL and the recent list
      queryClient.invalidateQueries({ 
        queryKey: FPL_QUERY_KEYS.detail(fplId) 
      })
      queryClient.invalidateQueries({ 
        queryKey: FPL_QUERY_KEYS.lists() 
      })
    },
  })
}

// Hook to combine FPL details and status
export const useFplWithStatus = (fplId: string) => {
  const { data: fplDetails, ...detailsQuery } = useFplDetails(fplId)
  const { data: status, ...statusQuery } = useFplStatus(fplId)
  
  return {
    data: fplDetails ? { ...fplDetails, ...status } : undefined,
    isLoading: detailsQuery.isLoading || statusQuery.isLoading,
    isError: detailsQuery.isError || statusQuery.isError,
    error: detailsQuery.error || statusQuery.error,
    refetch: () => {
      detailsQuery.refetch()
      statusQuery.refetch()
    },
  }
}
