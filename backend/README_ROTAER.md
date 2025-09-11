# Módulo ROTAER - Sistema de Ingestão e Processamento

## Visão Geral

O módulo ROTAER implementa um pipeline completo para processamento de dados do ROTAER (Registro de Aeródromos), incluindo parsing, normalização, validação e persistência de informações de aeródromos brasileiros.

## Arquitetura

### Componentes Principais

1. **RotaerParser** - Extrai dados do texto do ROTAER usando regex robustas
2. **RotaerNormalizer** - Converte formatos diversos para o formato canônico
3. **RotaerValidator** - Aplica regras de validação e gera warnings
4. **RotaerMapper** - Converte dados do ROTAER para o modelo de domínio
5. **RotaerService** - Orquestra o pipeline completo
6. **RotaerIntegrationService** - Integra com o sistema existente
7. **RotaerResource** - Endpoint REST para ingestão

### Pipeline de Processamento

```
Texto ROTAER → Parse → Normalize → Validate → Map → Persist
```

## Endpoints Disponíveis

### 1. Health Check
```
GET /api/v1/rotaer/health
```

### 2. Ingestão Individual
```
POST /api/v1/rotaer/ingest?dryRun=true|false
Content-Type: application/json

{
  "icao": "SBGL",
  "conteudo": "SBGL GALEÃO\n1 GALEÃO\n2 RIO DE JANEIRO\n3 RJ\n4 AD\n5 INTL\n6 PUB\n7 INFRAERO\n8 15 KM N DE RIO DE JANEIRO\n9 UTC-3\n10 VFR IFR\n11 L21,L23,L26\n12 Aeroporto Internacional do Rio de Janeiro - Galeão\n13 FIR\n14 Jurisdição"
}
```

### 3. Ingestão em Lote
```
POST /api/v1/rotaer/ingest/batch?dryRun=true|false
Content-Type: application/json

{
  "aerodromos": {
    "SBGL": "conteudo do ROTAER para SBGL",
    "SBSP": "conteudo do ROTAER para SBSP"
  }
}
```

### 4. Validação Apenas
```
POST /api/v1/rotaer/validate
Content-Type: application/json

{
  "icao": "SBGL",
  "conteudo": "conteudo do ROTAER"
}
```

### 5. Estatísticas
```
GET /api/v1/rotaer/stats
```

## Formato de Dados

### Entrada (Texto ROTAER)
O sistema espera texto no formato padrão do ROTAER com blocos numerados:

```
SBGL GALEÃO
1 GALEÃO
2 RIO DE JANEIRO
3 RJ
4 AD
5 INTL
6 PUB
7 INFRAERO
8 15 KM N DE RIO DE JANEIRO
9 UTC-3
10 VFR IFR
11 L21,L23,L26
12 Aeroporto Internacional do Rio de Janeiro - Galeão
13 FIR
14 Jurisdição

RWY 15/33 3180x47 ASPH 73/F/B/X/T
RWY 10/28 3000x45 CONC 70/F/B/X/T

TWR TORRE GALEÃO 118.000,118.200
GND SOLO GALEÃO 121.900
ATIS ATIS GALEÃO 127.200

ILS/DME IGL 110.3 2249.77S/04314.20W
VOR/DME ILM 113.1 2250.00S/04315.00W
NDB PCX 375 2248.00S/04313.00W

COMBUSTÍVEL: TF,PF
MANUTENÇÃO: S1,S2,S3,S4,S5
RFFS: CAT CIVIL 10 CAT MIL 9
MET: CMA 1,2 CMM 1,2 TEL (21) 3398-3057
AIS: TEL (21) 3398-3016 MIL (21) 2101-6417

RMK: Aeroporto internacional com operações 24h...

INFOTEMP:
F0838/2018 03/09/18 20:02 01/12/18 23:59 AD CLSD RWY 15/33 MAINT
```

### Saída (JSON Canônico)
```json
{
  "aerodromo": {
    "nome": "GALEÃO",
    "icao": "SBGL",
    "municipio": "RIO DE JANEIRO",
    "uf": "RJ",
    "coords_arp": {
      "lat_dms": "22 48 36S",
      "lon_dms": "043 15 02W",
      "lat_dd": -22.810000,
      "lon_dd": -43.250556
    },
    "elevacao_m": 0,
    "elevacao_ft": 0,
    "tipo": "AD",
    "categoria": "INTL",
    "utilizacao": "PUB",
    "administrador": "INFRAERO",
    "distancia_direcao_cidade": {
      "km": 15,
      "direcao": "N"
    },
    "fuso": "UTC-3",
    "operacao": "VFR IFR",
    "luzes_aerodromo": ["L21", "L23", "L26"],
    "observacoes_gerais": "Aeroporto Internacional do Rio de Janeiro - Galeão",
    "fir": "FIR",
    "jurisdicao": "Jurisdição"
  },
  "pistas": [
    {
      "designadores": ["15", "33"],
      "dimensoes": {
        "comprimento_m": 3180,
        "largura_m": 47
      },
      "piso": "ASPH",
      "pcn": "73/F/B/X/T",
      "luzes": ["L14A", "L15", "L12A", "L19A", "L11A", "L10"],
      "meht": {
        "cabeceira": "15",
        "valor_ft": 71.0
      }
    }
  ],
  "comunicacoes": [
    {
      "orgaos": "TWR",
      "indicativo": "TORRE GALEÃO",
      "frequencias_mhz": [118.000, 118.200],
      "horario_operacao": "H24",
      "emergencia": false,
      "tipo": "ATS"
    }
  ],
  "rdonav": [
    {
      "tipo": "ILS/DME",
      "ident": "IGL",
      "freq": 110.3,
      "coords": {
        "lat_dms": "2249.77S",
        "lon_dms": "04314.20W",
        "lat_dd": -22.8295,
        "lon_dd": -43.2361
      },
      "pista_associada": "15",
      "cat_ils": "II"
    }
  ],
  "servicos": {
    "combustivel": ["TF", "PF"],
    "manutencao": ["S1", "S2", "S3", "S4", "S5"],
    "rffs": {
      "cat_civil": 10,
      "cat_mil": 9
    },
    "met": {
      "cma": ["1", "2"],
      "cmm": ["1", "2"],
      "telefones": ["(21) 3398-3057"]
    },
    "ais": {
      "telefones": ["(21) 3398-3016"],
      "mil": ["(21) 2101-6417"]
    },
    "rmk": "Aeroporto internacional com operações 24h...",
    "compl": [
      {
        "ref": 1,
        "texto": "MEHT: a. RWY 15 - 72FT b. RWY 33 - 71FT"
      }
    ]
  },
  "infotemp": [
    {
      "id": "F0838/2018",
      "inicio": "2018-09-03T20:02:00-03:00",
      "fim": "2018-12-01T23:59:00-03:00",
      "texto": "AD CLSD RWY 15/33 MAINT",
      "natureza": "F"
    }
  ],
  "fonte": {
    "documento": "ROTAER",
    "icao": "SBGL",
    "versao": "texto",
    "capturado_em": "2025-09-10T18:30:00-03:00"
  }
}
```

## Validações Implementadas

### Coordenadas
- Formato DMS: `22 48 36S / 043 15 02W`
- Formato compacto: `2249.77S/04314.20W`
- Conversão para decimal com precisão ≥ 1e-6
- Validação de limites do Brasil

### Códigos ICAO
- Formato: 4 letras maiúsculas
- Validação de códigos brasileiros (iniciados com S)

### Frequências
- VHF: 108.000–136.975 MHz
- NDB: 200.0–1750.0 MHz
- Detecção automática de frequência de emergência (121.5)

### PCN
- Formato: `NN/(R|F)/(A|B|C|D)/(W|X|Y|Z)/(T|U)`
- Validação de componentes

### L-codes
- Formato: `L[1-35][A-Z]?(angulo)?`
- Suporte a ângulos: `L9(2.95)`

### INFOTEMP
- ID: `[FRCM]NNNN/NNNN`
- Datas: `DD/MM/YY HH:MM`
- Natureza: F (fechamento), R (restrição), M (modificação), C (correção)

## Integração com Sistema Existente

O módulo ROTAER foi integrado com o sistema existente através do `RotaerIntegrationService`, que:

1. Substitui o `RotaerPdfParser` antigo
2. Mantém compatibilidade com o `RotaerIngestionService` existente
3. Mapeia dados do ROTAER para o modelo de domínio `Aerodromo`
4. Preserva todas as funcionalidades existentes

## Testes

### Testes Unitários
- `RotaerNormalizerTest` - Testa normalização de coordenadas, dimensões, PCN, etc.
- `RotaerResourceTest` - Testa endpoints REST

### Testes de Integração
- Validação end-to-end com dados reais do SBGL
- Teste de dry-run vs persistência
- Validação de warnings e relatórios

## Como Usar

### 1. Ingestão Manual
```bash
curl -X POST "http://localhost:8080/api/v1/rotaer/ingest?dryRun=true" \
  -H "Content-Type: application/json" \
  -d '{"icao": "SBGL", "conteudo": "SBGL GALEÃO\n1 GALEÃO\n2 RIO DE JANEIRO\n3 RJ"}'
```

### 2. Validação
```bash
curl -X POST "http://localhost:8080/api/v1/rotaer/validate" \
  -H "Content-Type: application/json" \
  -d '{"icao": "SBGL", "conteudo": "conteudo do ROTAER"}'
```

### 3. Estatísticas
```bash
curl http://localhost:8080/api/v1/rotaer/stats
```

## Configurações

### Dry Run
- `dryRun=true`: Processa sem persistir no banco
- `dryRun=false`: Processa e persiste no banco

### Logs
- Nível INFO: Resumo do processamento
- Nível DEBUG: Detalhes por campo
- Warnings: Lista de problemas encontrados

## Próximos Passos

1. **Melhorar Parser**: Refinar regex para capturar mais dados do PDF
2. **Cache**: Implementar cache para dados processados
3. **Monitoramento**: Adicionar métricas de performance
4. **Validação Avançada**: Implementar validações específicas por tipo de aeródromo
5. **API Externa**: Integrar com APIs do DECEA para dados em tempo real

## Troubleshooting

### Erro 400 Bad Request
- Verificar formato JSON da requisição
- Validar se ICAO e conteúdo estão presentes

### Warnings de Validação
- Revisar formato dos dados de entrada
- Verificar se coordenadas estão no formato correto
- Validar códigos ICAO e frequências

### Problemas de Persistência
- Verificar conexão com banco de dados
- Validar se repositório está configurado corretamente
- Verificar logs para erros específicos
