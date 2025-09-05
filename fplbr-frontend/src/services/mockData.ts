import { FplComposedResponse, FplForm, AerodromoInfo } from '@/types';

export const mockAerodromeInfo: AerodromoInfo = {
  icao: 'SBGR',
  nome: 'Aeroporto Internacional de Guarulhos',
  coord: { lat: -23.4257, lon: -46.4819 },
  elev: 750,
  sun: {
    sunrise: '06:30',
    sunset: '17:45',
    fonte: 'INMET'
  },
  cartas: [
    { tipo: 'VAC', titulo: 'Carta de Aproximação Visual', pdf: 'SBGR_VAC.pdf' },
    { tipo: 'IAC', titulo: 'Carta de Aproximação por Instrumentos', pdf: 'SBGR_IAC.pdf' }
  ]
};

export const mockFormData: FplForm = {
  modo: 'PVC',
  item7: {
    identificacaoAeronave: 'PTOSP',
    indicativoChamada: false,
  },
  item8: {
    regrasVoo: 'VFR',
    tipoVoo: 'G',
  },
  item9: {
    numero: 1,
    tipoAeronave: 'BL8',
    catTurbulencia: 'L',
  },
  item10A: {
    S: true,
    D: true,
    B: true,
  },
  item10B: {
    E: true,
    S: true,
  },
  item13: {
    aerodromoPartida: 'SBMT',
    hora: '1200',
  },
  item15: {
    velocidadeCruzeiro: 'N0120',
    nivel: 'F080',
    rota: 'DCT',
  },
  item16: {
    aerodromoDestino: 'SBBP',
    eetTotal: '0030',
    alternado1: 'SBJD',
    alternado2: 'SBSP',
  },
  item18: {
    dof: '04092025',
    rmk: 'RMK/REA DCT',
  },
  item19: {
    autonomia: '0230',
    pob: 4,
    radioEmergencia: {
      U: true,
      V: true,
      E: true
    },
    sobrevivencia: {
      D: true,
      J: false,
    },
    coletes: {
      J: true,
      L: true,
    },
    botes: {
      D: true,
      numero: 1,
      capacidade: 6,
      C: true,
      cor: 'Vermelho e Branco'
    },
    infoAdicionais: {
      corMarcaANV: 'Vermelho e Branco com estrelas brancas',
      pilotoEmComando: 'Bregaida',
      codAnac1: '178799',
      telefone: '11999998888',
    },
  },
};

export const mockResponse: FplComposedResponse = {
  atsPreview: `(FPL-TEST01-IS
- BL8/M-SDIE/HB1/S
- SBMT1230
- G0120F080 DCT
  SBBP0100 SBJD SBSP
  DOF/250904
  REG/PTOSP
  EET/0230
  OPR/Eduardo Bregaida
  RMK/REA DCT`,
  form: mockFormData,
  origem: mockAerodromeInfo,
  destino: {
    ...mockAerodromeInfo,
    icao: 'SBMT',
    nome: 'Aeroporto de Campo de Marte',
    coord: { lat: -23.6267, lon: -46.6553 },
    elev: 802,
    sun: {
      sunrise: '06:32',
      sunset: '17:47',
      fonte: 'INMET'
    }
  },
  meteo: {
    SBGR: {
      raw: {
        metar: 'SBGR 041400Z 10008KT 9999 FEW020 SCT100 22/18 Q1016=',
        taf: 'TAF SBGR 041100Z 0412/0512 10008KT 9999 FEW020 SCT100',
        updatedAt: new Date().toISOString()
      },
      decoded: {
        metar: {
          wind: '10008KT',
          visibility: '9999',
          weather: [],
          clouds: ['FEW020', 'SCT100'],
          temperature: 22,
          dewpoint: 18,
          qnh: 1016
        },
        taf: {
          validFrom: '2025-09-04T12:00:00.000Z',
          validTo: '2025-09-05T12:00:00.000Z',
          wind: '10008KT',
          visibility: '9999',
          weather: [],
          clouds: ['FEW020', 'SCT100']
        }
      }
    }
  },
  notams: {
    SBGR: [
      {
        id: 'A1234/25',
        from: '2509011200',
        to: '2512312359',
        texto: 'RWY 09R/27L CLSD DUE TO MAINTENANCE'
      },
      {
        id: 'A1234/25',
        from: '2509011200',
        to: '2512312359',
        texto: 'RWY 09R/27L CLSD DUE TO MAINTENANCE'
      }
    ]
  }
};
