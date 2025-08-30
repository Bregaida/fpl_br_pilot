import type { Meta, StoryObj } from '@storybook/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { rest } from 'msw';
import { within, userEvent } from '@storybook/testing-library';
import { expect } from '@storybook/jest';
import { AirportsPage } from './page';

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
    },
  ],
  total: 2,
};

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
};

// Mock handlers
const handlers = [
  rest.get('*/airports/search', (req, res, ctx) => {
    return res(ctx.status(200), ctx.json(mockAirports));
  }),
  rest.get('*/airports/SBGR', (req, res, ctx) => {
    return res(ctx.status(200), ctx.json(mockAirportDetails));
  }),
];

// Create a custom test app with MSW
const TestApp = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

  return (
    <QueryClientProvider client={queryClient}>
      <AirportsPage />
    </QueryClientProvider>
  );
};

const meta: Meta<typeof TestApp> = {
  title: 'Pages/Airports',
  component: TestApp,
  parameters: {
    layout: 'fullscreen',
    msw: {
      handlers,
    },
  },
};

export default meta;
type Story = StoryObj<typeof TestApp>;

export const Default: Story = {};

export const SearchForAirport: Story = {
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);
    
    // Type in the search input
    const searchInput = await canvas.findByPlaceholderText('Search airports by name, city, or ICAO/IATA');
    await userEvent.type(searchInput, 'GRU', { delay: 100 });
    
    // Wait for results
    const resultRow = await canvas.findByText('SBGR');
    expect(resultRow).toBeInTheDocument();
    
    // Click on the result
    await userEvent.click(resultRow);
    
    // Check if airport details are shown
    const airportName = await canvas.findByText('Guarulhos International Airport');
    expect(airportName).toBeInTheDocument();
  },
};

export const ViewAirportDetails: Story = {
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);
    
    // Search for an airport
    const searchInput = await canvas.findByPlaceholderText('Search airports by name, city, or ICAO/IATA');
    await userEvent.type(searchInput, 'GRU', { delay: 100 });
    
    // Click on the first result
    const resultRow = await canvas.findByText('SBGR');
    await userEvent.click(resultRow);
    
    // Check if tabs are present
    const overviewTab = await canvas.findByRole('tab', { name: /overview/i });
    const runwaysTab = await canvas.findByRole('tab', { name: /runways/i });
    const frequenciesTab = await canvas.findByRole('tab', { name: /frequencies/i });
    const notamsTab = await canvas.findByRole('tab', { name: /notams/i });
    const chartsTab = await canvas.findByRole('tab', { name: /charts/i });
    
    expect(overviewTab).toBeInTheDocument();
    expect(runwaysTab).toBeInTheDocument();
    expect(frequenciesTab).toBeInTheDocument();
    expect(notamsTab).toBeInTheDocument();
    expect(chartsTab).toBeInTheDocument();
    
    // Test switching tabs
    await userEvent.click(runwaysTab);
    const runwayInfo = await canvas.findByText(/runway 09r\/27l/i);
    expect(runwayInfo).toBeInTheDocument();
    
    await userEvent.click(frequenciesTab);
    const frequencyInfo = await canvas.findByText(/tower/i);
    expect(frequencyInfo).toBeInTheDocument();
    
    await userEvent.click(notamsTab);
    const notamInfo = await canvas.findByText(/a1234\/24/i);
    expect(notamInfo).toBeInTheDocument();
    
    await userEvent.click(chartsTab);
    const chartInfo = await canvas.findByText(/aerodrome chart/i);
    expect(chartInfo).toBeInTheDocument();
  },
};

export const NoResults: Story = {
  parameters: {
    msw: {
      handlers: [
        rest.get('*/airports/search', (req, res, ctx) => {
          return res(ctx.status(200), ctx.json({ data: [], total: 0 }));
        }),
      ],
    },
  },
  play: async ({ canvasElement }) => {
    const canvas = within(canvasElement);
    
    // Search for a non-existent airport
    const searchInput = await canvas.findByPlaceholderText('Search airports by name, city, or ICAO/IATA');
    await userEvent.type(searchInput, 'NONEXISTENT123', { delay: 100 });
    
    // Check for no results message
    const noResults = await canvas.findByText(/no airports found/i);
    expect(noResults).toBeInTheDocument();
  },
};
