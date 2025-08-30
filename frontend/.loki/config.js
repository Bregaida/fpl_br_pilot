const { getChromePath } = require('chrome-launcher');

module.exports = {
  // Configuration for the test runner
  config: {
    // Browser settings
    chromeFlags: [
      '--headless',
      '--disable-gpu',
      '--no-sandbox',
      '--disable-dev-shm-usage',
      '--disable-setuid-sandbox',
      '--disable-web-security',
      '--disable-features=IsolateOrigins,site-per-process',
      '--disable-site-isolation-trials',
    ],
    // Viewport settings
    viewport: {
      width: 1280,
      height: 800,
      deviceScaleFactor: 1,
      isMobile: false,
      hasTouch: false,
      isLandscape: false,
    },
    // Storybook configuration
    storybookUrl: 'http://localhost:6006',
    chromeExecutablePath: getChromePath(),
    // Test configurations
    diffOptions: {
      threshold: 0.01,
      includeAA: false,
    },
    // Test retry settings
    retries: 2,
    // Output settings
    outputDir: '.loki/report',
    // Storybook knobs
    knobs: {},
    // Customize the Loki test context
    lokiContext: (testContext) => ({
      ...testContext,
      // Add any custom context here
    }),
  },
  // Stories to test
  stories: {
    // Include all stories by default
    include: ['**/*.stories.@(js|jsx|ts|tsx)'],
    // Exclude stories if needed
    exclude: ['**/node_modules/**', '**/dist/**'],
  },
  // Hooks
  hooks: {
    // Executed before each test
    beforeEach: async (page) => {
      // Add any setup code here
      await page.setViewportSize({
        width: 1280,
        height: 800,
      });
    },
    // Executed after each test
    afterEach: async (page) => {
      // Add any teardown code here
    },
  },
  // Customize the test runner
  testRunner: {
    // Customize the test runner options
    args: ['--config-dir=.loki'],
  },
  // Customize the image matching
  imageMatchOptions: {
    failureThreshold: 0.01,
    failureThresholdType: 'percent',
  },
  // Customize the test reporter
  reporter: [
    'console',
    ['html', { outputDir: '.loki/report' }],
    ['junit', { outputDir: '.loki/junit' }],
  ],
};
