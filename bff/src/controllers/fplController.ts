import { Request, Response } from 'express';
import { z } from 'zod';
import axios from 'axios';
import { MetarTaf } from 'metar-taf-parser';
import { 
  FplForm, 
  FplComposedResponse, 
  ApiResponse, 
  MeteoData, 
  NotamInfo, 
  AerodromoInfo 
} from '../types';

// Zod schema for FPL form validation
const fplFormSchema = z.object({
  modo: z.enum(['PVC', 'PVS']),
  item7: z.object({
    identificacaoAeronave: z.string().min(2).max(7),
    indicativoChamada: z.boolean().optional(),
  }),
  item8: z.object({
    regrasVoo: z.enum(['IFR', 'VFR', 'Y', 'Z']),
    tipoVoo: z.enum(['G', 'S', 'N', 'M', 'X']),
  }),
  item9: z.object({
    numero: z.number().int().positive().optional(),
    tipoAeronave: z.string().min(2).max(4),
    catTurbulencia: z.enum(['L', 'M', 'H', 'J']),
  }),
  item10A: z.record(z.boolean()),
  item10B: z.record(z.boolean()),
  item13: z.object({
    aerodromoPartida: z.string().length(4).regex(/^[A-Z]{4}$/),
    hora: z.string().regex(/^\d{4}$/),
  }),
  item15: z.object({
    velocidadeCruzeiro: z.string().regex(/^N\d{4}$/),
    nivel: z.string().regex(/^F\d{3}$/),
    rota: z.string().min(1),
  }),
  item16: z.object({
    aerodromoDestino: z.string().length(4).regex(/^[A-Z]{4}$/),
    eetTotal: z.string().regex(/^\d{4}$/),
    alternado1: z.string().length(4).regex(/^[A-Z]{4}$/).optional(),
    alternado2: z.string().length(4).regex(/^[A-Z]{4}$/).optional(),
  }),
  item18: z.object({
    dof: z.string().regex(/^\d{8}$/),
    rmk: z.string().optional(),
  }),
  item19: z.object({
    autonomia: z.string(),
    pob: z.number().int().nonnegative(),
    radioEmergencia: z.object({
      U: z.boolean().optional(),
      V: z.boolean().optional(),
      E: z.boolean().optional(),
    }),
    sobrevivencia: z.object({
      S: z.boolean().optional(),
      P: z.boolean().optional(),
      D: z.boolean().optional(),
      M: z.boolean().optional(),
      J: z.boolean().optional(),
    }),
    coletes: z.object({
      J: z.boolean().optional(),
      L: z.boolean().optional(),
      F: z.boolean().optional(),
      U: z.boolean().optional(),
      V: z.boolean().optional(),
    }),
    botes: z.object({
      D: z.boolean().optional(),
      numero: z.number().int().positive().optional(),
      capacidade: z.number().int().positive().optional(),
      C: z.boolean().optional(),
      cor: z.string().optional(),
    }),
    infoAdicionais: z.object({
      corMarcaANV: z.string(),
      pilotoEmComando: z.string(),
      codAnac1: z.string(),
      codAnac2: z.string().optional(),
      telefone: z.string(),
    }),
  }),
});

// Cache for aerodrome data
const aerodromeCache = new Map<string, { data: AerodromoInfo; timestamp: number }>();
const CACHE_TTL = 10 * 60 * 1000; // 10 minutes in milliseconds

/**
 * Get aerodrome information with caching
 */
async function getAerodromeInfo(icao: string, dof: string): Promise<AerodromoInfo> {
  const cacheKey = `${icao}:${dof}`;
  const cached = aerodromeCache.get(cacheKey);
  
  // Return cached data if still valid
  if (cached && Date.now() - cached.timestamp < CACHE_TTL) {
    return cached.data;
  }

  try {
    const response = await axios.get(`${process.env.BACKEND_BASE_URL}/api/v1/aerodromos/${icao}?dof=${dof}`);
    const aerodromeData: AerodromoInfo = {
      icao,
      nome: response.data.nome,
      coord: response.data.coord,
      elev: response.data.elev,
      cartas: response.data.cartas || [],
      sun: response.data.sun,
    };
    
    // Update cache
    aerodromeCache.set(cacheKey, {
      data: aerodromeData,
      timestamp: Date.now(),
    });
    
    return aerodromeData;
  } catch (error) {
    console.error(`Error fetching aerodrome data for ${icao}:`, error);
    return { icao, cartas: [] }; // Return minimal valid data
  }
}

/**
 * Get METAR/TAF data
 */
async function getMeteoData(icao: string): Promise<MeteoData> {
  try {
    const response = await axios.get(`${process.env.BACKEND_BASE_URL}/api/v1/meteorologia/${icao}/briefing`);
    
    let metarDecoded;
    let tafDecoded;
    
    try {
      if (response.data.metar) {
        metarDecoded = MetarTaf.parse(response.data.metar);
      }
      if (response.data.taf) {
        tafDecoded = MetarTaf.parse(response.data.taf);
      }
    } catch (parseError) {
      console.error('Error parsing METAR/TAF:', parseError);
      // Continue with raw data even if parsing fails
    }
    
    return {
      raw: {
        metar: response.data.metar,
        taf: response.data.taf,
        updatedAt: response.data.updatedAt || new Date().toISOString(),
      },
      decoded: {
        metar: metarDecoded,
        taf: tafDecoded,
      },
    };
  } catch (error) {
    console.error(`Error fetching meteo data for ${icao}:`, error);
    return { raw: {}, decoded: {} };
  }
}

/**
 * Get NOTAMs for an aerodrome
 */
async function getNotams(icao: string): Promise<NotamInfo[]> {
  try {
    const response = await axios.get(`${process.env.BACKEND_BASE_URL}/api/v1/ais/${icao}/notams`);
    return Array.isArray(response.data) 
      ? response.data 
      : response.data.notams || [];
  } catch (error) {
    console.error(`Error fetching NOTAMs for ${icao}:`, error);
    return [];
  }
}

/**
 * Get ATS preview from backend
 */
async function getAtsPreview(form: FplForm): Promise<string> {
  try {
    const response = await axios.post(
      `${process.env.BACKEND_BASE_URL}/api/v1/fpl/preview`,
      form
    );
    return response.data.ats || response.data;
  } catch (error) {
    console.error('Error getting ATS preview:', error);
    return 'Error generating ATS preview';
  }
}

/**
 * Compose FPL with all related data
 */
export async function composeFpl(form: FplForm): Promise<FplComposedResponse> {
  // Validate form data
  const validationResult = fplFormSchema.safeParse(form);
  if (!validationResult.success) {
    throw new Error(`Invalid form data: ${validationResult.error.message}`);
  }

  const { aerodromoPartida: origemIcao, hora } = form.item13;
  const { aerodromoDestino: destinoIcao, eetTotal } = form.item16;
  const { dof } = form.item18;

  // Fetch all data in parallel
  const [origem, destino, meteoOrigem, meteoDestino, notamsOrigem, notamsDestino, atsPreview] = await Promise.all([
    getAerodromeInfo(origemIcao, dof),
    getAerodromeInfo(destinoIcao, dof),
    getMeteoData(origemIcao),
    getMeteoData(destinoIcao),
    getNotams(origemIcao),
    getNotams(destinoIcao),
    getAtsPreview(form),
  ]);

  // Build the response
  const response: FplComposedResponse = {
    atsPreview,
    form,
    origem,
    destino,
    meteo: {
      [origemIcao]: meteoOrigem,
      [destinoIcao]: meteoDestino,
    },
    notams: {
      [origemIcao]: notamsOrigem,
      [destinoIcao]: notamsDestino,
    },
  };

  return response;
}

/**
 * POST /api/compose/fpl - Main endpoint for FPL composition
 */
export async function postComposeFpl(req: Request, res: Response) {
  try {
    const form = req.body as FplForm;
    const result = await composeFpl(form);
    
    const response: ApiResponse<FplComposedResponse> = {
      success: true,
      data: result,
      timestamp: new Date().toISOString(),
    };
    
    res.json(response);
  } catch (error) {
    console.error('Error in postComposeFpl:', error);
    
    const response: ApiResponse<null> = {
      success: false,
      error: {
        error: 'FPL_COMPOSITION_ERROR',
        message: error instanceof Error ? error.message : 'Unknown error',
        details: process.env.NODE_ENV === 'development' ? error : undefined,
      },
      timestamp: new Date().toISOString(),
    };
    
    res.status(500).json(response);
  }
}

/**
 * GET /api/health - Health check endpoint
 */
export function getHealth(_req: Request, res: Response) {
  const response: ApiResponse<{ status: string }> = {
    success: true,
    data: { status: 'ok' },
    timestamp: new Date().toISOString(),
  };
  res.json(response);
}
