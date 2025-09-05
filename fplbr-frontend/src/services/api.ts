import axios, { AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { ApiError, ApiResponse, FplForm, FplComposedResponse } from '@/types';
import { mockResponse } from './mockData';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080';

// Create axios instance with base URL and default headers
const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL.endsWith('/') ? API_BASE_URL.slice(0, -1) : API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// Request interceptor for adding auth token if available
api.interceptors.request.use(
  (config) => {
    // You can add auth token here if needed
    // const token = localStorage.getItem('token');
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for handling errors
api.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<any>>) => {
    // If the response has data with success: false, treat it as an error
    if (response.data && response.data.success === false) {
      return Promise.reject(response.data.error);
    }
    return response;
  },
  (error: AxiosError<ApiError>) => {
    // Handle network errors
    if (!error.response) {
      return Promise.reject({
        error: 'NETWORK_ERROR',
        message: 'Unable to connect to the server. Please check your internet connection.',
      });
    }

    // Handle HTTP errors
    const status = error.response.status;
    let errorMessage = 'An unexpected error occurred';

    if (status === 401) {
      errorMessage = 'Authentication required';
      // Handle unauthorized (e.g., redirect to login)
    } else if (status === 403) {
      errorMessage = 'You do not have permission to perform this action';
    } else if (status === 404) {
      errorMessage = 'The requested resource was not found';
    } else if (status >= 500) {
      errorMessage = 'A server error occurred. Please try again later.';
    }

    return Promise.reject({
      error: error.response.data?.error || 'UNKNOWN_ERROR',
      message: error.response.data?.message || errorMessage,
      details: error.response.data?.details,
    });
  }
);

const isBackendAvailable = async (): Promise<boolean> => {
  try {
    await axios.get(`${API_BASE_URL}/q/health`, {
      timeout: 2000 // 2 seconds timeout
    });
    return true;
  } catch (error) {
    console.warn('Backend not available, using mock data');
    return false;
  }
};

export const composeFpl = async (formData: FplForm): Promise<FplComposedResponse> => {
  const backendAvailable = await isBackendAvailable();
  
  if (!backendAvailable) {
    // Return mock data with the form data included
    return {
      ...mockResponse,
      form: formData,
      atsPreview: mockResponse.atsPreview.replace('TEST01', formData.item7.identificacaoAeronave || 'TEST01')
    };
  }

  try {
    const response = await api.post<FplComposedResponse>('/api/compose/fpl', formData);
    return response.data;
  } catch (error) {
    console.error('Error calling composeFpl:', error);
    // Return mock data on error as well
    return {
      ...mockResponse,
      form: formData,
      atsPreview: mockResponse.atsPreview.replace('TEST01', formData.item7.identificacaoAeronave || 'TEST01')
    };
  }
};

// Helper function to make API requests with proper typing
export async function apiRequest<T>(
  config: AxiosRequestConfig
): Promise<T> {
  try {
    const response = await api.request<ApiResponse<T>>(config);
    return response.data.data!;
  } catch (error) {
    // The error is already processed by the interceptor
    throw error;
  }
}

// Set auth token in the API client
export function setAuthToken(token?: string): void {
  if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common['Authorization'];
  }
}

export default api;
