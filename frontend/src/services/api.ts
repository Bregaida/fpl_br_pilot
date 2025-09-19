import axios from 'axios';
import type { AxiosRequestConfig } from 'axios';

const baseURL = (import.meta as any).env?.VITE_API_URL ?? 'http://localhost:8080';

export const api = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 30000,
  withCredentials: true,
});

export async function apiRequest<T = unknown>(config: AxiosRequestConfig) : Promise<T> {
  const response = await api.request<T>(config);
  return response.data as T;
}

export const AerodromosAPI = {
  search: (query: string) => api.get(`/api/aerodromos`, { params: { q: query } })
};

export const FlightplanAPI = {
  create: (payload: any) => api.post(`/api/v1/flightplans`, payload),
  getById: (id: string | number) => api.get(`/api/v1/flightplans/${id}`),
  getSubmissionById: (id: string | number) => api.get(`/api/v1/flightplans/submissions/${id}`),
  getSubmissionView: (id: string | number) => api.get(`/api/v1/flightplans/submissions/${id}/view`),
  listSubmissions: (params?: { ident?: string; limit?: number }) => api.get(`/api/v1/flightplans/submissions`, { params }),
  getRotaerData: (icao: string) => api.get(`/api/v1/flightplans/rotaer/${icao}`),
  getAiswebFull: (icao: string) => api.get(`/api/aisweb/full/${icao}`)
};
