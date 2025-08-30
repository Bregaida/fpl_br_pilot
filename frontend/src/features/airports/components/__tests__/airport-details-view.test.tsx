import { render, screen, waitFor } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { AirportDetailsView } from '../airport-details-view'
import { vi } from 'vitest'

// Mock data
const mockAirportDetails = {
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
}

// Setup mock server
const server = setupServer(
  rest.get('*/airports/SBGR', (req, res, ctx) => {
    return res(ctx.json(mockAirportDetails))
  })
)

// Mock the useSearchParams hook
vi.mock('next/navigation', () => ({
  useRouter: vi.fn(),
  useSearchParams: vi.fn(() => ({
    get: vi.fn(),
  })),
  usePathname: vi.fn(),
}))

// Mock the window.matchMedia function
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})

// Set up the test environment
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
    },
  },
})

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <QueryClientProvider client={queryClient}>
    {children}
  </QueryClientProvider>
)

describe('AirportDetailsView', () => {
  beforeAll(() => server.listen())
  afterEach(() => {
    server.resetHandlers()
    queryClient.clear()
  })
  afterAll(() => server.close())

  it('renders airport details', async () => {
    render(<AirportDetailsView icao="SBGR" />, { wrapper })

    // Check if loading state is shown initially
    expect(screen.getByText(/loading/i)).toBeInTheDocument()

    // Wait for data to load
    await waitFor(() => {
      // Check if airport name is displayed
      expect(screen.getByText(/guarulhos international airport/i)).toBeInTheDocument()
      
      // Check if tabs are rendered
      expect(screen.getByText(/overview/i)).toBeInTheDocument()
      expect(screen.getByText(/runways/i)).toBeInTheDocument()
      expect(screen.getByText(/frequencies/i)).toBeInTheDocument()
      expect(screen.getByText(/notams/i)).toBeInTheDocument()
      expect(screen.getByText(/charts/i)).toBeInTheDocument()
    })
  })

  it('displays airport information in the overview tab', async () => {
    render(<AirportDetailsView icao="SBGR" />, { wrapper })

    // Wait for data to load
    await waitFor(() => {
      // Check airport information
      expect(screen.getByText(/location/i)).toBeInTheDocument()
      expect(screen.getByText(/elevation: 2459 ft/i)).toBeInTheDocument()
      expect(screen.getByText(/timezone: america/sao paulo/i)).toBeInTheDocument()
      
      // Check links
      expect(screen.getByText(/official website/i)).toHaveAttribute('href', mockAirportDetails.homeLink)
      expect(screen.getByText(/wikipedia/i)).toHaveAttribute('href', mockAirportDetails.wikipediaLink)
    })
  })

  it('displays runways information', async () => {
    render(<AirportDetailsView icao="SBGR" />, { wrapper })

    // Wait for data to load and click on runways tab
    await waitFor(() => {
      fireEvent.click(screen.getByText(/runways/i))
      
      // Check if runway information is displayed
      expect(screen.getByText(/runway 09r/27l/i)).toBeInTheDocument()
      expect(screen.getByText(/12,014 ft × 148 ft/i)).toBeInTheDocument()
      expect(screen.getByText(/surface: asph-g/i)).toBeInTheDocument()
    })
  })

  it('displays frequencies information', async () => {
    render(<AirportDetailsView icao="SBGR" />, { wrapper })

    // Wait for data to load and click on frequencies tab
    await waitFor(() => {
      fireEvent.click(screen.getByText(/frequencies/i))
      
      // Check if frequency information is displayed
      expect(screen.getByText(/tower/i)).toBeInTheDocument()
      expect(screen.getByText(/118.100/i)).toBeInTheDocument()
    })
  })

  it('displays NOTAMs information', async () => {
    render(<AirportDetailsView icao="SBGR" />, { wrapper })

    // Wait for data to load and click on NOTAMs tab
    await waitFor(() => {
      fireEvent.click(screen.getByText(/notams/i))
      
      // Check if NOTAM information is displayed
      expect(screen.getByText(/a1234/24/i)).toBeInTheDocument()
      expect(screen.getByText(/rwy 09r/27l clsd due maintenance/i)).toBeInTheDocument()
    })
  })

  it('displays charts information', async () => {
    render(<AirportDetailsView icao="SBGR" />, { wrapper })

    // Wait for data to load and click on charts tab
    await waitFor(() => {
      fireEvent.click(screen.getByText(/charts/i))
      
      // Check if chart information is displayed
      expect(screen.getByText(/aerodrome chart/i)).toBeInTheDocument()
      expect(screen.getByText(/1.0 mb/i)).toBeInTheDocument()
    })
  })
})
