import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'
import AirportsPage from '../page'

// Mock data
const mockAirports = {
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
    {
      icao: 'SBSP',
      iata: 'CGH',
      name: 'Congonhas Airport',
      city: 'São Paulo',
      country: 'Brazil',
      latitude: -23.6267,
      longitude: -46.6553,
      elevation: 2631,
      timezone: 'America/Sao_Paulo',
      type: 'medium_airport',
      status: 'operational',
      continent: 'SA',
      scheduledService: true,
      gpsCode: 'SBSP',
    },
  ],
  total: 2,
}

// Setup mock server
const server = setupServer(
  rest.get('*/airports/search', (req, res, ctx) => {
    const query = req.url.searchParams.get('query') || '';
    const page = parseInt(req.url.searchParams.get('page') || '1');
    const pageSize = parseInt(req.url.searchParams.get('pageSize') || '10');
    
    // Filter airports based on search query
    const filteredAirports = mockAirports.data.filter(airport => {
      const searchStr = `${airport.icao} ${airport.iata} ${airport.name} ${airport.city} ${airport.country}`.toLowerCase();
      return searchStr.includes(query.toLowerCase());
    });
    
    // Pagination
    const startIndex = (page - 1) * pageSize;
    const paginatedAirports = filteredAirports.slice(startIndex, startIndex + pageSize);
    
    return res(
      ctx.delay(150), // Simulate network delay
      ctx.json({
        data: paginatedAirports,
        total: filteredAirports.length,
        page,
        pageSize,
        totalPages: Math.ceil(filteredAirports.length / pageSize),
      })
    );
  }),
  rest.get('*/airports/:icao', (req, res, ctx) => {
    const { icao } = req.params;
    const airport = mockAirports.data.find(a => a.icao === icao);
    
    if (!airport) {
      return res(
        ctx.status(404),
        ctx.json({ message: 'Airport not found' })
      );
    }
    
    return res(
      ctx.json({
        ...airport,
        runways: [
          {
            id: '1',
            airportIcao: icao,
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
            airportIcao: icao,
            type: 'TWR',
            description: 'Tower',
            frequencyMhz: 118.1,
          },
        ],
        notams: [
          {
            id: '1',
            icaoId: icao,
            notamNumber: 'A1234/24',
            type: 'NEW',
            fir: 'SBBS',
            notamCode: 'RWY',
            trafficType: ['IFR', 'VFR'],
            purpose: ['OPERATIONAL SIGNIFICANCE'],
            scope: ['AERODROME'],
            location: icao,
            validFrom: '2408011200',
            validUntil: '2408011800',
            body: 'RWY 09R/27L CLSD DUE MAINTENANCE',
            created: '2024-07-31T12:00:00Z',
          },
        ],
        charts: [
          {
            id: '1',
            airportIcao: icao,
            chartName: 'AERODROME CHART',
            chartCode: `AD-2.${icao}-1`,
            category: 'AIRPORT',
            icaoAirportCode: icao,
            icaoAirportName: airport.name.toUpperCase(),
            iataCountryCode: airport.country.slice(0, 2).toUpperCase(),
            section: 'AERODROME',
            fileExtension: 'pdf',
            fileUrl: `https://example.com/charts/${icao.toLowerCase()}-ad2.pdf`,
            fileSize: 1024 * 1024,
            fileDate: '2024-01-01T00:00:00Z',
            pdfName: `AD-2.${icao}-1`,
            pdfType: 'AERODROME CHART',
            pdfSize: '1.0 MB',
            pdfDate: '2024-01-01',
            pdfUrl: `https://example.com/charts/${icao.toLowerCase()}-ad2.pdf`,
            created: '2024-01-01T00:00:00Z',
            lastUpdated: '2024-01-01T00:00:00Z',
          },
        ],
      })
    );
  })
)

// Mock the useSearchParams hook
const mockPush = vi.fn();
const mockSearchParams = new URLSearchParams();

vi.mock('next/navigation', () => ({
  useRouter: () => ({
    push: mockPush,
    replace: vi.fn(),
    prefetch: vi.fn(),
  }),
  useSearchParams: () => ({
    get: (key: string) => mockSearchParams.get(key),
    has: (key: string) => mockSearchParams.has(key),
    forEach: (callback: (value: string, key: string) => void) => mockSearchParams.forEach(callback),
    toString: () => mockSearchParams.toString(),
  }),
  usePathname: () => '/airports',
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
  <MemoryRouter>
    <QueryClientProvider client={queryClient}>
      {children}
    </QueryClientProvider>
  </MemoryRouter>
)

describe('Airports Page', () => {
  beforeAll(() => server.listen())
  afterEach(() => {
    server.resetHandlers()
    queryClient.clear()
  })
  afterAll(() => server.close())

  it('renders the airports page with search and quick actions', async () => {
    render(<AirportsPage />, { wrapper })
    
    // Check if the search input is rendered
    const searchInput = screen.getByPlaceholderText('Search airports by name, city, or ICAO/IATA')
    expect(searchInput).toBeInTheDocument()
    
    // Check if quick action buttons are rendered
    expect(screen.getByRole('button', { name: /major airports/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /international airports/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /nearby airports/i })).toBeInTheDocument()
    
    // Wait for data to load
    await waitFor(() => {
      expect(screen.getByText('SBGR')).toBeInTheDocument()
      expect(screen.getByText('SBSP')).toBeInTheDocument()
    })
    const searchInput = screen.getByPlaceholderText(/search by airport name, city, or code/i)
    expect(searchInput).toBeInTheDocument()
    
    // Check if the search button is rendered
    const searchButton = screen.getByRole('button', { name: /search/i })
    expect(searchButton).toBeInTheDocument()
    
    // Check if the tabs are rendered
    expect(screen.getByText(/search airports/i)).toBeInTheDocument()
    expect(screen.getByText(/airport details/i)).toBeInTheDocument()
    expect(screen.getByText(/charts/i)).toBeInTheDocument()
    expect(screen.getByText(/notams/i)).toBeInTheDocument()
  })

  it('searches for airports and displays results', async () => {
    render(<AirportsPage />, { wrapper })
    
    // Type in the search input
    const searchInput = screen.getByPlaceholderText(/search by airport name, city, or code/i)
    fireEvent.change(searchInput, { target: { value: 'GRU' } })
    
    // Click the search button
    const searchButton = screen.getByRole('button', { name: /search/i })
    fireEvent.click(searchButton)
    
    // Wait for the results to load
    await waitFor(() => {
      expect(screen.getByText(/guarulhos international airport/i)).toBeInTheDocument()
      expect(screen.getByText(/são paulo, brazil/i)).toBeInTheDocument()
    })
  })

  it('displays airport details when an airport is selected', async () => {
    render(<AirportsPage />, { wrapper })
    
    // Type in the search input
    const searchInput = screen.getByPlaceholderText(/search by airport name, city, or code/i)
    fireEvent.change(searchInput, { target: { value: 'GRU' } })
    
    // Click the search button
    const searchButton = screen.getByRole('button', { name: /search/i })
    fireEvent.click(searchButton)
    
    // Wait for the results to load and click on the first result
    await waitFor(() => {
      const airportCard = screen.getByText(/guarulhos international airport/i)
      fireEvent.click(airportCard.closest('tr')!)
    })
    
    // Check if the details tab is active
    await waitFor(() => {
      expect(screen.getByText(/airport information/i)).toBeInTheDocument()
      expect(screen.getByText(/elevation: 2459 ft/i)).toBeInTheDocument()
    })
  })

  it('handles API errors gracefully', async () => {
    // Override the default handler for this test
    server.use(
      rest.get('*/airports/search', (req, res, ctx) => {
        return res(ctx.status(500), ctx.json({ message: 'Internal Server Error' }))
      })
    )
    
    render(<AirportsPage />, { wrapper })
    
    // Type in the search input
    const searchInput = screen.getByPlaceholderText(/search by airport name, city, or code/i)
    fireEvent.change(searchInput, { target: { value: 'GRU' } })
    
    // Click the search button
    const searchButton = screen.getByRole('button', { name: /search/i })
    fireEvent.click(searchButton)
    
    // Check if the error message is displayed
    await waitFor(() => {
      expect(screen.getByText(/error loading airports/i)).toBeInTheDocument()
    })
  })
})
