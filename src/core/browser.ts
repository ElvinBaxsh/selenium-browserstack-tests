import { mkdir, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { WebDriver } from 'selenium-webdriver';
import { config } from './config';
import { createDriver } from './driverFactory';

/**
 * Opens a fresh browser for every test and closes it afterwards.
 * Call it first inside describe(), so the browser is ready for the other hooks.
 */
export function useBrowser() {
  const browser = { driver: undefined as unknown as WebDriver };

  beforeEach(async function () {
    browser.driver = await createDriver(this.currentTest!.fullTitle());
    await browser.driver.get(config.baseUrl);
  });

  afterEach(async function () {
    const { driver } = browser;
    if (!driver) return;
    try {
      const passed = this.currentTest!.state === 'passed';
      if (!passed) await saveScreenshot(driver, this.currentTest!.fullTitle());
      if (config.onBrowserStack) await markBrowserStackSession(driver, passed, this.currentTest!.err?.message);
    } finally {
      await driver.quit();
    }
  });

  return browser;
}

async function saveScreenshot(driver: WebDriver, name: string) {
  try {
    const dir = path.join('reports', 'screenshots');
    await mkdir(dir, { recursive: true });
    await writeFile(path.join(dir, `${name.replace(/[^\w-]+/g, '-')}.png`), await driver.takeScreenshot(), 'base64');
  } catch (error) {
    console.error('Could not save screenshot:', error);
  }
}

// Shows passed / failed on the BrowserStack dashboard
async function markBrowserStackSession(driver: WebDriver, passed: boolean, message = '') {
  const reason = passed ? 'Passed' : message.slice(0, 250);
  const command = { action: 'setSessionStatus', arguments: { status: passed ? 'passed' : 'failed', reason } };
  await driver.executeScript(`browserstack_executor: ${JSON.stringify(command)}`);
}
