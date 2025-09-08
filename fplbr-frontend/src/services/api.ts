import axios from 'axios';

const baseURL = (import.meta as any).env?.VITE_API_URL ?? 'http://localhost:8080';

export const api = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' },
  timeout: 20000,
});

export const AerodromosAPI = {
  search: (query: string) => api.get(`/api/v1/aerodromos`, { params: { query } })
};

export const FlightplanAPI = {
  create: (payload: any) => api.post(`/api/v1/flightplans`, payload),
  getById: (id: string | number) => api.get(`/api/v1/flightplans/${id}`)
};
