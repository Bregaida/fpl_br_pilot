import { FplPreviewRequest, FplPreviewResponse, FplSimplifiedPreviewRequest } from "../types/api"
import { api } from "@/shared/lib/api"

export const fplApi = {
  /**
   * Generate a preview of the FPL message
   * @param data The FPL form data
   * @returns The generated FPL message
   */
  generatePreview: async (data: FplPreviewRequest): Promise<FplPreviewResponse> => {
    try {
      const response = await api.post<FplPreviewResponse>('/api/v1/fpl/preview', data)
      return response.data
    } catch (error) {
      console.error('Error generating FPL preview:', error)
      throw error
    }
  },

  /**
   * Generate a preview of the simplified FPL message
   * @param data The simplified FPL form data
   * @returns The generated FPL message
   */
  generateSimplifiedPreview: async (data: FplSimplifiedPreviewRequest): Promise<FplPreviewResponse> => {
    try {
      const response = await api.post<FplPreviewResponse>('/api/v1/fpl/simplified-preview', data)
      return response.data
    } catch (error) {
      console.error('Error generating simplified FPL preview:', error)
      throw error
    }
  },

  /**
   * Submit the FPL to the ATC system
   * @param data The FPL form data
   * @returns The submission result
   */
  submitFpl: async (data: FplPreviewRequest): Promise<{ success: boolean; message: string }> => {
    try {
      await api.post('/api/v1/fpl/submit', data)
      return { success: true, message: 'FPL submitted successfully' }
    } catch (error) {
      console.error('Error submitting FPL:', error)
      throw error
    }
  },

  /**
   * Submit the simplified FPL to the ATC system
   * @param data The simplified FPL form data
   * @returns The submission result
   */
  submitSimplifiedFpl: async (data: FplSimplifiedPreviewRequest): Promise<{ success: boolean; message: string }> => {
    try {
      await api.post('/api/v1/fpl/simplified-submit', data)
      return { success: true, message: 'Simplified FPL submitted successfully' }
    } catch (error) {
      console.error('Error submitting simplified FPL:', error)
      throw error
    }
  },

  /**
   * Get the list of recent FPLs for the current user
   * @returns List of recent FPLs
   */
  getRecentFpls: async (): Promise<Array<{
    id: string
    aircraftId: string
    departureAerodrome: string
    destinationAerodrome: string
    departureTime: string
    status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED'
    createdAt: string
  }>> => {
    try {
      const response = await api.get('/api/v1/fpl/recent')
      return response.data
    } catch (error) {
      console.error('Error fetching recent FPLs:', error)
      throw error
    }
  },

  /**
   * Get the details of a specific FPL
   * @param fplId The ID of the FPL to fetch
   * @returns The FPL details
   */
  getFplDetails: async (fplId: string): Promise<FplPreviewResponse & {
    status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED'
    createdAt: string
    updatedAt: string
    submittedBy: string
    notes?: string
  }> => {
    try {
      const response = await api.get(`/api/v1/fpl/${fplId}`)
      return response.data
    } catch (error) {
      console.error(`Error fetching FPL ${fplId}:`, error)
      throw error
    }
  },

  /**
   * Cancel a submitted FPL
   * @param fplId The ID of the FPL to cancel
   * @returns The cancellation result
   */
  cancelFpl: async (fplId: string): Promise<{ success: boolean; message: string }> => {
    try {
      await api.post(`/api/v1/fpl/${fplId}/cancel`)
      return { success: true, message: 'FPL cancelled successfully' }
    } catch (error) {
      console.error(`Error cancelling FPL ${fplId}:`, error)
      throw error
    }
  },

  /**
   * Get the status of an FPL
   * @param fplId The ID of the FPL to check
   * @returns The FPL status
   */
  getFplStatus: async (fplId: string): Promise<{
    status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED'
    message?: string
    updatedAt: string
  }> => {
    try {
      const response = await api.get(`/api/v1/fpl/${fplId}/status`)
      return response.data
    } catch (error) {
      console.error(`Error getting status for FPL ${fplId}:`, error)
      throw error
    }
  }
}
