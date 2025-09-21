import { apiClient } from './api'

export const AuthAPI = {
  register: (payload: any) => apiClient.post('/api/v1/auth/register', payload),
  verify2fa: (email: string, code: number) => apiClient.post('/api/v1/auth/2fa/verify', { email, code }),
  verifyTOTP: (payload: { code: string, sessionId: string }) => apiClient.post('/api/v1/auth/2fa/verify-setup', payload),
  login: (login: string, password: string, totp?: number) => apiClient.post('/api/v1/auth/login', { login, password, totp }),
  loginTotpOnly: (login: string, code: number) => apiClient.post('/api/v1/auth/login-totp-only', { login, code }),
  forgot: (login: string) => apiClient.post('/api/v1/auth/forgot', { login }),
  reset: (tokenOrTemp: string, newPassword: string) => apiClient.post('/api/v1/auth/reset', { tokenOrTemp, newPassword }),
}

export const CepAPI = {
  lookup: (cep: string) => apiClient.get(`/api/v1/cep/${cep}`),
  getCep: (cep: string) => apiClient.get(`/api/v1/cep/${cep}`)
}


