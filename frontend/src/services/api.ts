const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

class ApiClient {
  private readonly baseURL: string;

  constructor(baseURL: string) {
    this.baseURL = baseURL;
  }

  async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const url = `${this.baseURL}${endpoint}`;
    
    const defaultOptions: RequestInit = {
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include', // Para cookies
    };

    const config = { ...defaultOptions, ...options };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.error || `HTTP ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('API Error:', error);
      throw error;
    }
  }

  // TOTP Endpoints
  async prepareTOTP(email: string) {
    return this.request<{
      totpSessionId: string;
      qrPngBase64: string;
      otpauthUrl: string;
      secretMasked: string;
    }>('/api/v1/auth/2fa/setup', { 
      method: 'POST',
      body: JSON.stringify({ email })
    });
  }

  async verifyTOTP(totpSessionId: string, codigo: string) {
    return this.request<{ ok: boolean }>('/api/v1/auth/2fa/verify-setup', {
      method: 'POST',
      body: JSON.stringify({ totpSessionId, code: codigo }),
    });
  }

  // Duplicate Check Endpoints
  async checkDuplicate(data: { email?: string; cpf?: string }) {
    return this.request<{ ok: boolean }>('/api/v1/auth/check-duplicate', { 
      method: 'POST', 
      body: JSON.stringify(data) 
    });
  }

  // Auth Endpoints
  async register(data: RegisterData) {
    return this.request<{
      id: string;
      totpEnabled: boolean;
      backupCodes?: string[];
    }>('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async loginPassword(email: string, senha: string) {
    return this.request<{ accessToken: string }>('/api/auth/login-password', {
      method: 'POST',
      body: JSON.stringify({ email, senha }),
    });
  }

  async loginTOTP(email: string, codigo: string) {
    return this.request<{ accessToken: string }>('/api/auth/login-totp', {
      method: 'POST',
      body: JSON.stringify({ email, codigo }),
    });
  }

  async refreshToken() {
    return this.request<{ accessToken: string }>('/api/auth/token/refresh', {
      method: 'POST',
    });
  }

  async logout() {
    return this.request<{ ok: boolean }>('/api/auth/logout', {
      method: 'POST',
    });
  }

  // User Endpoints
  async getCurrentUser() {
    return this.request<{
      id: string;
      email: string;
      nomeCompleto: string;
      totpEnabled: boolean;
      createdAt: string;
    }>('/api/users/me');
  }

  // Password Endpoints
  async changePassword(data: {
    currentPassword?: string;
    totpCode?: string;
    backupCode?: string;
    newPassword: string;
  }) {
    return this.request<{ ok: boolean }>('/api/password/change', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async initPasswordReset(email: string, totpCode?: string, backupCode?: string) {
    return this.request<{ resetTokenDev: string }>('/api/password/reset/init-totp', {
      method: 'POST',
      body: JSON.stringify({ email, totpCode, backupCode }),
    });
  }

  async confirmPasswordReset(token: string, newPassword: string) {
    return this.request<{ ok: boolean }>('/api/password/reset/confirm', {
      method: 'POST',
      body: JSON.stringify({ token, newPassword }),
    });
  }

  // Método genérico para POST
  async post<T>(endpoint: string, data: any): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  // Método genérico para GET
  async get<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'GET',
    });
  }

  // Flightplan Endpoints
  async getAiswebFull(icao: string) {
    return this.request<any>(`/api/aisweb/full/${icao}`);
  }

  async getSubmissionView(id: string) {
    return this.request<{ data: any }>(`/api/v1/flightplans/${id}`);
  }

  async listSubmissions(params: { limit?: number }) {
    const query = new URLSearchParams();
    if (params.limit) query.append('limit', params.limit.toString());
    const queryString = query.toString();
    let endpoint = '/api/v1/flightplans';
    if (queryString) {
      endpoint = `/api/v1/flightplans?${queryString}`;
    }
    return this.request<{ data: any[] }>(endpoint);
  }
}

export const apiClient = new ApiClient(API_BASE_URL);

// Função apiRequest para compatibilidade com código existente
export async function apiRequest<T>(options: {
  method: 'GET' | 'POST' | 'PUT' | 'DELETE';
  url: string;
  data?: any;
  params?: Record<string, string>;
}): Promise<T> {
  const { method, url, data, params } = options;
  
  // Construir URL com parâmetros de query
  let endpoint = url;
  if (params) {
    const query = new URLSearchParams(params);
    const queryString = query.toString();
    if (queryString) {
      endpoint = `${url}?${queryString}`;
    }
  }
  
  const requestOptions: RequestInit = {
    method,
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
  };
  
  if (data && (method === 'POST' || method === 'PUT')) {
    requestOptions.body = JSON.stringify(data);
  }
  
  return apiClient.request<T>(endpoint, requestOptions);
}

// FlightplanAPI para compatibilidade com código existente
export const FlightplanAPI = {
  getAiswebFull: (icao: string) => apiClient.getAiswebFull(icao),
  getSubmissionView: (id: string) => apiClient.getSubmissionView(id),
  listSubmissions: (params: { limit?: number }) => apiClient.listSubmissions(params),
};

// Types
export interface RegisterData {
  nomeCompleto: string;
  email: string;
  telefone: string;
  tipoLicenca: string;
  cpf: string;
  dataNascimento: string;
  empresaEscola: string;
  cep: string;
  numero?: string;
  logradouro: string;
  bairro: string;
  cidade: string;
  uf: string;
  complemento?: string;
  senha: string;
  totpSessionId?: string;
}