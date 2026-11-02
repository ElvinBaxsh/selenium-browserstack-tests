import { Builder, Browser, WebDriver } from 'selenium-webdriver';
import chrome from 'selenium-webdriver/chrome';
import edge from 'selenium-webdriver/edge';
import firefox from 'selenium-webdriver/firefox';
import { config } from './config';

const BROWSERSTACK_HUB = 'https://hub-cloud.browserstack.com/wd/hub';

// Linux CI runners need these for Chromium-based browsers (Chrome, Edge) to start
const CI_ARGS = ['--no-sandbox', '--disable-dev-shm-usage'];

/** Creates a local browser (drivers are resolved by Selenium Manager) or a BrowserStack remote browser. */
export function createDriver(testName: string): Promise<WebDriver> {
  return config.onBrowserStack ? createRemote(testName) : createLocal();
}

async function createLocal(): Promise<WebDriver> {
  switch (config.browser) {
    case 'firefox': {
      const options = new firefox.Options();
      if (config.headless) options.addArguments('-headless');
      return new Builder().forBrowser(Browser.FIREFOX).setFirefoxOptions(options).build();
    }
    case 'edge': {
      const options = new edge.Options();
      if (config.headless) options.addArguments('--headless=new');
      if (config.onCi) options.addArguments(...CI_ARGS);
      options.addArguments('--window-size=1280,800', '--no-first-run');
      // Edge on Linux can wait forever for the full "load" event; DOM ready is enough
      // because every page action waits for its own element
      options.setPageLoadStrategy('eager');
      return new Builder().forBrowser(Browser.EDGE).setEdgeOptions(options).build();
    }
    default: {
      const options = new chrome.Options();
      if (config.headless) options.addArguments('--headless=new');
      if (config.onCi) options.addArguments(...CI_ARGS);
      // Turns off Chrome's "change your password" pop-up, which blocks clicks after login
      options.setUserPreferences({
        credentials_enable_service: false,
        'profile.password_manager_enabled': false,
        'profile.password_manager_leak_detection': false,
      });
      options.addArguments('--window-size=1280,800', '--guest');
      return new Builder().forBrowser(Browser.CHROME).setChromeOptions(options).build();
    }
  }
}

async function createRemote(testName: string): Promise<WebDriver> {
  if (!config.browserStackUser || !config.browserStackKey) {
    throw new Error('Set BROWSERSTACK_USERNAME and BROWSERSTACK_ACCESS_KEY to run on BrowserStack');
  }

  return new Builder()
    .usingServer(BROWSERSTACK_HUB)
    .withCapabilities({
      browserName: config.browser,
      'bstack:options': {
        os: config.os,
        osVersion: config.osVersion,
        projectName: 'Selenium BrowserStack Tests',
        buildName: config.buildName,
        sessionName: testName,
        userName: config.browserStackUser,
        accessKey: config.browserStackKey,
      },
    })
    .build();
}
