import { describe, it, before, after } from 'mocha';
import { Builder, By, until, WebDriver } from 'selenium-webdriver';
import chrome from 'selenium-webdriver/chrome';
import { expect } from 'chai';

// Increase Mocha timeout for Selenium tests
const TEST_TIMEOUT_MS = 30000;

describe('Airports Page - Selenium Tests', function() {
  this.timeout(TEST_TIMEOUT_MS);
  
  let driver: WebDriver;
  
  before(async function() {
    // Set up Chrome options
    const options = new chrome.Options();
    
    // Run in headless mode for CI environments
    if (process.env.CI === 'true') {
      options.addArguments('--headless=new');
      options.addArguments('--disable-gpu');
      options.addArguments('--no-sandbox');
      options.addArguments('--disable-dev-shm-usage');
    }
    
    // Set window size
    options.addArguments('--window-size=1920,1080');
    
    // Initialize the WebDriver
    driver = await new Builder()
      .forBrowser('chrome')
      .setChromeOptions(options)
      .build();
      
    // Set implicit wait
    await driver.manage().setTimeouts({ implicit: 5000 });
  });
  
  after(async function() {
    // Quit the WebDriver
    if (driver) {
      await driver.quit();
    }
  });
  
  it('should load the airports page', async function() {
    // Navigate to the airports page
    await driver.get('http://localhost:3000/airports');
    
    // Check if the page title is correct
    const title = await driver.getTitle();
    expect(title).to.include('Airports | FPL-BR Pilot');
    
    // Check if search input is visible
    const searchInput = await driver.findElement(
      By.css('input[placeholder="Search airports by name, city, or ICAO/IATA"]')
    );
    expect(await searchInput.isDisplayed()).to.be.true;
    
    // Check if quick action cards are visible
    const quickActionCards = await driver.findElements(
      By.xpath('//h3[contains(text(), "Quick Actions")]/following-sibling::div//button')
    );
    expect(quickActionCards.length).to.be.greaterThan(0);
  });
  
  it('should search for an airport and display results', async function() {
    // Navigate to the airports page
    await driver.get('http://localhost:3000/airports');
    
    // Find and type in the search input
    const searchInput = await driver.findElement(
      By.css('input[placeholder="Search airports by name, city, or ICAO/IATA"]')
    );
    await searchInput.sendKeys('GRU');
    
    // Wait for results to load (you might need to adjust the selector based on your actual implementation)
    await driver.wait(
      until.elementLocated(By.css('table tbody tr')),
      10000,
      'Timed out waiting for search results'
    );
    
    // Check if results are displayed
    const results = await driver.findElements(By.css('table tbody tr'));
    expect(results.length).to.be.greaterThan(0);
    
    // Check if the expected airport is in the results
    const resultText = await results[0].getText();
    expect(resultText).to.include('SBGR');
    expect(resultText).to.include('Guarulhos International Airport');
  });
  
  it('should show airport details when clicking on a result', async function() {
    // Navigate to the airports page
    await driver.get('http://localhost:3000/airports');
    
    // Find and type in the search input
    const searchInput = await driver.findElement(
      By.css('input[placeholder="Search airports by name, city, or ICAO/IATA"]')
    );
    await searchInput.sendKeys('GRU');
    
    // Wait for results and click the first result
    const firstResult = await driver.wait(
      until.elementLocated(By.css('table tbody tr')),
      10000
    );
    await firstResult.click();
    
    // Wait for the details view to load
    await driver.wait(
      until.elementLocated(By.css('h1')),
      10000,
      'Timed out waiting for airport details'
    );
    
    // Check if the airport name is displayed
    const airportName = await driver.findElement(By.css('h1')).getText();
    expect(airportName).to.include('Guarulhos International Airport');
    
    // Check if the tabs are visible
    const tabs = await driver.findElements(By.css('[role="tab"]'));
    expect(tabs.length).to.be.greaterThan(0);
    
    // Verify tab names
    const tabNames = await Promise.all(tabs.map(tab => tab.getText()));
    expect(tabNames).to.include.members(['Overview', 'Runways', 'Frequencies', 'NOTAMs', 'Charts']);
  });
  
  it('should handle search with no results', async function() {
    // Navigate to the airports page
    await driver.get('http://localhost:3000/airports');
    
    // Search for a non-existent airport
    const searchInput = await driver.findElement(
      By.css('input[placeholder="Search airports by name, city, or ICAO/IATA"]')
    );
    await searchInput.sendKeys('NONEXISTENT123');
    
    // Wait for the no results message
    const noResults = await driver.wait(
      until.elementLocated(By.xpath('//*[contains(text(), "No airports found")]')),
      5000,
      'Timed out waiting for no results message'
    );
    
    expect(await noResults.isDisplayed()).to.be.true;
  });
});
