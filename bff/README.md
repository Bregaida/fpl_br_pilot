# FPL-BR BFF (Backend for Frontend)

BFF service for FPL-BR application that aggregates data from multiple backend services.

## Features

- Flight Plan Composition (PVC/PVS)
- Aerodrome data and charts
- METAR/TAF data with decoding
- NOTAMs retrieval
- In-memory caching

## Setup

1. Install dependencies:
   ```bash
   npm install
   ```
2. Configure `.env` from `.env.example`
3. Start development server:
   ```bash
   npm run dev
   ```

## API Endpoints

- `GET /health` - Health check
- `POST /api/compose/fpl` - Compose flight plan
- `GET /api/lookup/aerodromo?icao=XXXX&dof=YYYYMMDD` - Get aerodrome data
- `GET /api/briefing?icao=XXXX` - Get METAR/TAF data
- `GET /api/notams?icao=XXXX` - Get NOTAMs

## Environment Variables

- `BACKEND_BASE_URL` - Base URL for backend services
- `PORT` - BFF server port (default: 3001)
- `NODE_ENV` - Environment (development/production)
- `ALLOWED_ORIGINS` - CORS allowed origins
- `HTTP_TIMEOUT` - Request timeout in ms
- `CACHE_TTL` - Cache TTL in seconds

## Development

- `npm run dev` - Start dev server
- `npm run build` - Build for production
- `npm start` - Start production server
- `npm run lint` - Run linter
