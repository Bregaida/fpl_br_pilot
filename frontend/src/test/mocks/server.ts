import { setupServer } from 'msw/node';
import { rest } from 'msw';

const handlers = [
  // Mock airports search
  rest.get('*/airports/search', (req, res, ctx) => {
    const query = req.url.searchParams.get('query') || '';
    
    // Mock response based on query
    if (query.toLowerCase() === 'gru') {
      return res(
        ctx.status(200),
        ctx.json({
          data: [
            {
              icao: 'SBGR',
              iata: 'GRU',
              name: 'Guarulhos International Airport',
              city: 'São Paulo',
              country: 'Brazil',
              latitude: -23.4356,
              longitude: -46.4731,
              elevation: 2459,
              timezone: 'America/Sao_Paulo',
              type: 'large_airport',
              status: 'operational',
              continent: 'SA',
              scheduledService: true,
              gpsCode: 'SBGR',
            },
          ],
          total: 1,
        })
      );
    }
    
    return res(
      ctx.status(200),
      ctx.json({
        data: [],
        total: 0,
      })
    );
  }),

  // Mock airport details
  rest.get('*/airports/:icao', (req, res, ctx) => {
    const { icao } = req.params;
    
    if (icao === 'SBGR') {
      return res(
        ctx.status(200),
        ctx.json({
          icao: 'SBGR',
          iata: 'GRU',
          name: 'Guarulhos International Airport',
          city: 'São Paulo',
          country: 'Brazil',
          latitude: -23.4356,
          longitude: -46.4731,
          elevation: 2459,
          timezone: 'America/Sao_Paulo',
          type: 'large_airport',
          status: 'operational',
          continent: 'SA',
          municipality: 'Guarulhos',
          scheduledService: true,
          gpsCode: 'SBGR',
          localCode: 'GRU',
          homeLink: 'https://www.gru.com.br',
          wikipediaLink: 'https://en.wikipedia.org/wiki/São_Paulo–Guarulhos_International_Airport',
          runways: [
            {
              id: '1',
              airportIcao: 'SBGR',
              lengthFt: 12014,
              widthFt: 148,
              surface: 'ASPH-G',
              lighted: true,
              closed: false,
              leIdent: '09R',
              leHeadingDegT: 89.8,
              leDisplacedThresholdFt: 0,
              heIdent: '27L',
              heHeadingDegT: 269.8,
              heDisplacedThresholdFt: 0,
            },
          ],
          frequencies: [
            {
              id: '1',
              airportIcao: 'SBGR',
              type: 'TWR',
              description: 'Tower',
              frequencyMhz: 118.1,
            },
          ],
          notams: [
            {
              id: '1',
              icaoId: 'SBGR',
              notamNumber: 'A1234/24',
              type: 'NEW',
              fir: 'SBBS',
              notamCode: 'RWY',
              trafficType: ['IFR', 'VFR'],
              purpose: ['OPERATIONAL SIGNIFICANCE'],
              scope: ['AERODROME'],
              location: 'SBGR',
              validFrom: '2408011200',
              validUntil: '2408011800',
              body: 'RWY 09R/27L CLSD DUE MAINTENANCE',
              created: '2024-07-31T12:00:00Z',
            },
          ],
          charts: [
            {
              id: '1',
              airportIcao: 'SBGR',
              chartName: 'AERODROME CHART',
              chartCode: 'AD-2.SBGR-1',
              category: 'AIRPORT',
              icaoAirportCode: 'SBGR',
              icaoAirportName: 'GUARULHOS/INTL',
              iataCountryCode: 'BR',
              section: 'AERODROME',
              fileExtension: 'pdf',
              fileUrl: 'https://example.com/charts/sbgr-ad2.pdf',
              fileSize: 1024 * 1024,
              fileDate: '2024-01-01T00:00:00Z',
              pdfName: 'AD-2.SBGR-1',
              pdfType: 'AERODROME CHART',
              pdfSize: '1.0 MB',
              pdfDate: '2024-01-01',
              pdfUrl: 'https://example.com/charts/sbgr-ad2.pdf',
              created: '2024-01-01T00:00:00Z',
              lastUpdated: '2024-01-01T00:00:00Z',
            },
          ],
        })
      );
    }
    
    return res(
      ctx.status(404),
      ctx.json({
        message: 'Airport not found',
      })
    );
  }),
];

export const server = setupServer(...handlers);
