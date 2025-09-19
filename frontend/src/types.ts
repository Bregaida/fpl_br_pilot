export type FplMode = 'PVC' | 'PVS';

export interface FplForm {
  modo: FplMode;
  item7: { 
    identificacaoAeronave: string; 
    indicativoChamada?: boolean 
  };
  item8: { 
    regrasVoo: 'IFR' | 'VFR' | 'Y' | 'Z'; 
    tipoVoo: 'G' | 'S' | 'N' | 'M' | 'X' 
  };
  item9: { 
    numero?: number; 
    tipoAeronave: string; 
    catTurbulencia: 'L' | 'M' | 'H' | 'J' 
  };
  item10A: Record<string, boolean>;
  item10B: Record<string, boolean>;
  item13: { 
    aerodromoPartida: string; 
    hora: string 
  };
  item15: { 
    velocidadeCruzeiro: string; 
    nivel: string; 
    rota: string 
  };
  item16: { 
    aerodromoDestino: string; 
    eetTotal: string; 
    alternado1?: string; 
    alternado2?: string 
  };
  item18: { 
    dof: string; 
    rmk?: string 
  };
  item19: {
    autonomia: string; 
    pob: number;
    radioEmergencia: { 
      U?: boolean; 
      V?: boolean; 
      E?: boolean 
    };
    sobrevivencia: { 
      S?: boolean; 
      P?: boolean; 
      D?: boolean; 
      M?: boolean; 
      J?: boolean 
    };
    coletes: { 
      J?: boolean; 
      L?: boolean; 
      F?: boolean; 
      U?: boolean; 
      V?: boolean 
    };
    botes: { 
      D?: boolean; 
      numero?: number; 
      capacidade?: number; 
      C?: boolean; 
      cor?: string 
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

export interface CartaAerodromo {
  tipo: 'VAC' | 'IAC' | 'SID' | 'STAR' | 'ROTA';
  titulo: string;
  versao?: string;
  pdf: string;
}

export interface AerodromoInfo {
  icao: string;
  nome?: string;
  coord?: { 
    lat: number; 
    lon: number 
  };
  elev?: number;
  cartas: CartaAerodromo[];
  sun?: { 
    sunrise: string; 
    sunset: string; 
    fonte: string 
  };
}

export interface FplComposedResponse {
  atsPreview: string;
  form: FplForm;
  origem: AerodromoInfo;
  destino: AerodromoInfo;
  meteo: {
    [icao: string]: {
      raw: { 
        metar?: string; 
        taf?: string; 
        updatedAt?: string 
      };
      decoded: { 
        metar?: any; 
        taf?: any 
      };
    };
  };
  notams: {
    [icao: string]: Array<{ 
      id: string; 
      from?: string; 
      to?: string; 
      texto: string 
    }>;
  };
}

export interface ApiError {
  error: string;
  message: string;
  details?: any;
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: ApiError;
  timestamp: string;
}
