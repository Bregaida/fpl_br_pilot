import { api } from './api'

export const AuthAPI = {
  register: (payload: any) => api.post('/api/v1/auth/register', payload),
  verify2fa: (email: string, code: number) => api.post('/api/v1/auth/2fa/verify', { email, code }),
  login: (login: string, password: string, totp?: number) => api.post('/api/v1/auth/login', { login, password, totp }),
  loginTotpOnly: (login: string, code: number) => api.post('/api/v1/auth/login-totp-only', { login, code }),
  forgot: (login: string) => api.post('/api/v1/auth/forgot', { login }),
  reset: (tokenOrTemp: string, newPassword: string) => api.post('/api/v1/auth/reset', { tokenOrTemp, newPassword }),
}

export const CepAPI = {
  lookup: (cep: string) => api.get(`/api/v1/cep/${cep}`)
}


