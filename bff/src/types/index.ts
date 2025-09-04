// Flight Plan Modes
export type FplMode = 'PVC' | 'PVS';

// Flight Rules
export type FlightRules = 'IFR' | 'VFR' | 'Y' | 'Z';

// Flight Type
export type FlightType = 'G' | 'S' | 'N' | 'M' | 'X';

// Turbulence Category
export type TurbulenceCategory = 'L' | 'M' | 'H' | 'J';

// Chart Types
export type ChartType = 'VAC' | 'IAC' | 'SID' | 'STAR' | 'ROTA';

// Emergency Radio Types
export type EmergencyRadio = 'U' | 'V' | 'E';

// Survival Equipment Types
export type SurvivalEquipment = 'S' | 'P' | 'D' | 'M' | 'J';

// Life Jacket Types
export type LifeJacketType = 'J' | 'L' | 'F' | 'U' | 'V';

// FPL Form Interface
export interface FplForm {
  modo: FplMode;
  item7: {
    identificacaoAeronave: string;
    indicativoChamada?: boolean;
  };
  item8: {
    regrasVoo: FlightRules;
    tipoVoo: FlightType;
  };
  item9: {
    numero?: number;
    tipoAeronave: string;
    catTurbulencia: TurbulenceCategory;
  };
  item10A: Record<string, boolean>;
  item10B: Record<string, boolean>;
  item13: {
    aerodromoPartida: string;
    hora: string; // HHMM Zulu
  };
  item15: {
    velocidadeCruzeiro: string;
    nivel: string;
    rota: string;
  };
  item16: {
    aerodromoDestino: string;
    eetTotal: string;
    alternado1?: string;
    alternado2?: string;
  };
  item18: {
    dof: string; // YYYYMMDD
    rmk?: string;
  };
  item19: {
    autonomia: string;
    pob: number;
    radioEmergencia: {
      U?: boolean;
      V?: boolean;
      E?: boolean;
    };
    sobrevivencia: {
      S?: boolean;
      P?: boolean;
      D?: boolean;
      M?: boolean;
      J?: boolean;
    };
    coletes: {
      J?: boolean;
      L?: boolean;
      F?: boolean;
      U?: boolean;
      V?: boolean;
    };
    botes: {
      D?: boolean;
      numero?: number;
      capacidade?: number;
      C?: boolean;
      cor?: string;
    };
    infoAdicionais: {
      corMarcaANV: string;
      pilotoEmComando: string;
      codAnac1: string;
      codAnac2?: string;
      telefone: string;
    };
  };
}

// Aerodrome Information
export interface AerodromoInfo {
  icao: string;
  nome?: string;
  coord?: {
    lat: number;
    lon: number;
  };
  elev?: number;
  cartas: CartaAerodromo[];
  sun?: SunInfo;
}

// Airport Chart
export interface CartaAerodromo {
  tipo: ChartType;
  titulo: string;
  versao?: string;
  pdf: string;
}

// Sun Information
export interface SunInfo {
  sunrise: string;
  sunset: string;
  fonte: string;
}

// Meteorological Data
export interface MeteoData {
  raw: {
    metar?: string;
    taf?: string;
    updatedAt?: string;
  };
  decoded: {
    metar?: any; // Will be replaced with proper METAR type
    taf?: any;   // Will be replaced with proper TAF type
  };
}

// NOTAM Information
export interface NotamInfo {
  id: string;
  from?: string;
  to?: string;
  texto: string;
}

// API Response for FPL Composition
export interface FplComposedResponse {
  atsPreview: string;
  form: FplForm;
  origem: AerodromoInfo;
  destino: AerodromoInfo;
  meteo: Record<string, MeteoData>;
  notams: Record<string, NotamInfo[]>;
}

// Error Response
export interface ApiError {
  error: string;
  message: string;
  details?: any;
}

// API Response Wrapper
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: ApiError;
  timestamp: string;
}
