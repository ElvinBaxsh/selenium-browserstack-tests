/** Run settings, read from environment variables. Example: BROWSER=firefox HEADLESS=false npm run test:ui */
export const config = {
  baseUrl: 'https://www.saucedemo.com',
  apiUrl: 'https://automationexercise.com/api',

  // chrome, firefox, edge or safari
  browser: (process.env.BROWSER ?? 'chrome').toLowerCase(),
  headless: process.env.HEADLESS !== 'false',
  onCi: process.env.CI !== undefined,

  // "local" runs on this machine, "browserstack" runs in the BrowserStack cloud
  onBrowserStack: process.env.GRID === 'browserstack',
  os: process.env.BS_OS ?? 'Windows',
  osVersion: process.env.BS_OS_VERSION ?? '11',
  buildName: process.env.BS_BUILD_NAME ?? 'selenium-browserstack-tests',
  browserStackUser: process.env.BROWSERSTACK_USERNAME ?? '',
  browserStackKey: process.env.BROWSERSTACK_ACCESS_KEY ?? '',
};
