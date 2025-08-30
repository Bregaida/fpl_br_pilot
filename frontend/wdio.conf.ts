import { join } from 'path'
import { config as baseConfig } from '@wdio/runner'
import type { Options } from '@wdio/types'

export const config: Options.Testrunner = {
  ...baseConfig,
  runner: 'local',
  autoCompileOpts: {
    autoCompile: true,
    tsNodeOpts: {
      project: './tsconfig.json',
      transpileOnly: true
    }
  },
  specs: [
    './e2e/selenium/**/*.test.ts'
  ],
  maxInstances: 5,
  capabilities: [{
    maxInstances: 5,
    browserName: 'chrome',
    'goog:chromeOptions': {
      args: ['--headless', '--disable-gpu', '--window-size=1920,1080']
    }
  }],
  logLevel: 'warn',
  bail: 0,
  baseUrl: 'http://localhost:3000',
  waitforTimeout: 10000,
  connectionRetryTimeout: 120000,
  connectionRetryCount: 3,
  services: [
    ['chromedriver', {
      chromedriverCustomPath: join(process.cwd(), 'node_modules/.bin/chromedriver')
    }]
  ],
  framework: 'mocha',
  reporters: [
    'spec',
    ['junit', {
      outputDir: 'test-results',
      outputFileFormat: function() {
        return 'selenium-junit.xml';
      }
    }]
  ],
  mochaOpts: {
    ui: 'bdd',
    timeout: 60000
  },
  before: async function() {
    await browser.setWindowSize(1920, 1080);
  },
  afterTest: async function(test, context, { error, result, duration, passed, retries }) {
    if (error) {
      await browser.takeScreenshot();
    }
  }
}

export { config }
