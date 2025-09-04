import 'dotenv/config';
import express, { type Request, Response, NextFunction } from 'express';
import cors from 'cors';
import morgan from 'morgan';
import { createClient } from 'redis';
import { MetarTaf } from 'metar-taf-parser';
import axios, { AxiosError } from 'axios';
import { z } from 'zod';
import routes from './routes';

// Environment variables validation
const envSchema = z.object({
  BACKEND_BASE_URL: z.string().url().default('http://localhost:8080'),
  PORT: z.string().default('3001'),
  NODE_ENV: z.enum(['development', 'production', 'test']).default('development'),
  ALLOWED_ORIGINS: z.string().default('http://localhost:3000,http://localhost:5173'),
  HTTP_TIMEOUT: z.string().default('10000'),
  HTTP_MAX_RETRIES: z.string().default('2'),
  CACHE_TTL: z.string().default('600'),
  REDIS_URL: z.string().optional(),
});

type EnvVars = z.infer<typeof envSchema>;

declare global {
  namespace NodeJS {
    interface ProcessEnv extends EnvVars {}
  }
}

// Initialize environment variables
const env = envSchema.parse(process.env);

// Create Express app
const app = express();

// Middleware
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true }));
app.use(cors({
  origin: env.ALLOWED_ORIGINS.split(','),
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization', 'X-Requested-With'],
  credentials: true,
  maxAge: 86400, // 24 hours
  preflightContinue: false,
  optionsSuccessStatus: 204
}));

// Request logging
app.use(morgan(env.NODE_ENV === 'production' ? 'combined' : 'dev'));

// Health check endpoint
app.get('/health', (req, res) => {
  res.status(200).json({ status: 'ok', timestamp: new Date().toISOString() });
});

// API routes
app.use('/', routes);

// Create HTTP client with retry logic
const http = axios.create({
  baseURL: env.BACKEND_BASE_URL,
  timeout: parseInt(env.HTTP_TIMEOUT, 10),
});

// Retry interceptor
http.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const { config, response } = error;
    const maxRetries = parseInt(env.HTTP_MAX_RETRIES, 10);
    
    if (!config || !config.retryCount) {
      config.retryCount = 0;
    }

    // Only retry on network errors or 5xx responses
    if (
      !response && 
      config.retryCount < maxRetries &&
      (!error.response || (error.response.status >= 500 && error.response.status < 600))
    ) {
      const delay = Math.pow(2, config.retryCount) * 1000 + Math.random() * 1000;
      config.retryCount++;
      
      await new Promise(resolve => setTimeout(resolve, delay));
      return http(config);
    }
    
    return Promise.reject(error);
  }
);

// 404 Handler
app.use((req: Request, res: Response) => {
  res.status(404).json({
    success: false,
    error: {
      error: 'NOT_FOUND',
      message: `Cannot ${req.method} ${req.path}`
    },
    timestamp: new Date().toISOString()
  });
});

// Error handling middleware
app.use((err: Error, _req: Request, res: Response, _next: NextFunction) => {
  console.error('Unhandled error:', err);
  res.status(500).json({
    error: 'Internal Server Error',
    message: env.NODE_ENV === 'development' ? err.message : 'Something went wrong',
  });
});

// Start server
const port = parseInt(env.PORT, 10) || 3001;
const server = app.listen(port, '0.0.0.0', () => {
  console.log(`[BFF] Server running on http://localhost:${port}`);
  console.log(`[BFF] Environment: ${env.NODE_ENV}`);
  console.log(`[BFF] Backend URL: ${env.BACKEND_BASE_URL}`);
});

// Graceful shutdown
process.on('SIGTERM', () => {
  console.log('SIGTERM received. Shutting down gracefully...');
  server.close(() => {
    console.log('Server closed');
    process.exit(0);
  });
});

process.on('unhandledRejection', (reason, promise) => {
  console.error('Unhandled Rejection at:', promise, 'reason:', reason);
});

process.on('uncaughtException', (error) => {
  console.error('Uncaught Exception:', error);
  process.exit(1);
});

export default app;
