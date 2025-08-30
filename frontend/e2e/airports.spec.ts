import { test, expect } from '@playwright/test';

test.describe('Airports Page', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/airports');
  });

  test('should load the airports page', async ({ page }) => {
    // Check if the page title is correct
    await expect(page).toHaveTitle(/Airports | FPL-BR Pilot/);
    
    // Check if the search input is visible
    const searchInput = page.getByPlaceholder('Search airports by name, city, or ICAO/IATA');
    await expect(searchInput).toBeVisible();
    
    // Check if quick action cards are visible
    await expect(page.getByText('Major Airports')).toBeVisible();
    await expect(page.getByText('International Airports')).toBeVisible();
    await expect(page.getByText('Nearby Airports')).toBeVisible();
  });

  test('should search for an airport', async ({ page }) => {
    // Mock the API response
    await page.route('**/airports/search*', route => {
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          data: [{
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
          }],
          total: 1,
        }),
      });
    });

    // Type in the search input
    const searchInput = page.getByPlaceholder('Search airports by name, city, or ICAO/IATA');
    await searchInput.fill('GRU');
    
    // Wait for the results to load
    const resultsTable = page.getByRole('table');
    await expect(resultsTable).toBeVisible();
    
    // Check if the airport is in the results
    await expect(page.getByText('SBGR')).toBeVisible();
    await expect(page.getByText('Guarulhos International Airport')).toBeVisible();
    await expect(page.getByText('São Paulo, Brazil')).toBeVisible();
  });

  test('should show airport details when clicking on a result', async ({ page }) => {
    // Mock the search API response
    await page.route('**/airports/search*', route => {
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          data: [{
            icao: 'SBGR',
            iata: 'GRU',
            name: 'Guarulhos International Airport',
            city: 'São Paulo',
            country: 'Brazil',
          }],
          total: 1,
        }),
      });
    });

    // Mock the airport details API response
    await page.route('**/airports/SBGR', route => {
      route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
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
        }),
      });
    });

    // Search for an airport
    const searchInput = page.getByPlaceholder('Search airports by name, city, or ICAO/IATA');
    await searchInput.fill('GRU');
    
    // Click on the first result
    const firstResult = page.getByRole('row').filter({ hasText: 'SBGR' }).first();
    await firstResult.click();
    
    // Check if the airport details are shown
    await expect(page.getByRole('heading', { name: 'Guarulhos International Airport' })).toBeVisible();
    await expect(page.getByText('SBGR / GRU')).toBeVisible();
    
    // Check if tabs are visible
    await expect(page.getByRole('tab', { name: 'Overview' })).toBeVisible();
    await expect(page.getByRole('tab', { name: 'Runways' })).toBeVisible();
    await expect(page.getByRole('tab', { name: 'Frequencies' })).toBeVisible();
    await expect(page.getByRole('tab', { name: 'NOTAMs' })).toBeVisible();
    await expect(page.getByRole('tab', { name: 'Charts' })).toBeVisible();
  });
});
