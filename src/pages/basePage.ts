import { By, Locator, WebDriver, WebElement, error, until } from 'selenium-webdriver';

const TIMEOUT = 10_000;

export const testId = (id: string) => By.css(`[data-test='${id}']`);

// "Item total: $39.98" -> 39.98
export const toNumber = (text: string) => Number(text.replace(/[^0-9.]/g, ''));

/** Shared helpers: every action waits for the element first (explicit waits, no sleeps). */
export abstract class BasePage {
  constructor(protected readonly driver: WebDriver) {}

  // Runs a check again until it gives a result. React can replace an element between
  // finding it and using it, so "not there yet" and "stale" just mean: try again.
  protected waitFor<T>(check: () => Promise<T | false>, message: string): Promise<T> {
    return this.driver.wait(async () => {
      try {
        return await check();
      } catch (e) {
        if (e instanceof error.NoSuchElementError || e instanceof error.StaleElementReferenceError) return false;
        throw e;
      }
    }, TIMEOUT, message) as Promise<T>;
  }

  protected visible(locator: Locator): Promise<WebElement> {
    return this.waitFor(async () => {
      const element = await this.driver.findElement(locator);
      return (await element.isDisplayed()) && element;
    }, `Element not visible: ${locator}`);
  }

  protected allVisible(locator: Locator): Promise<WebElement[]> {
    return this.waitFor(async () => {
      const elements = await this.driver.findElements(locator);
      const shown = await Promise.all(elements.map((element) => element.isDisplayed()));
      return elements.length > 0 && shown.every(Boolean) && elements;
    }, `Elements not visible: ${locator}`);
  }

  protected async click(locator: Locator) {
    const element = await this.visible(locator);
    await this.driver.wait(until.elementIsEnabled(element), TIMEOUT);
    await element.click();
  }

  protected async type(locator: Locator, text: string) {
    const element = await this.visible(locator);
    await element.clear();
    await element.sendKeys(text);
  }

  protected async text(locator: Locator) {
    return (await this.visible(locator)).getText();
  }

  protected async allTexts(locator: Locator) {
    const elements = await this.allVisible(locator);
    return Promise.all(elements.map((element) => element.getText()));
  }

  protected async waitForText(locator: Locator, expected: string) {
    await this.waitFor(async () => (await this.driver.findElement(locator).getText()) === expected,
      `Text "${expected}" not found: ${locator}`);
  }

  protected async waitForCount(locator: Locator, count: number) {
    await this.waitFor(async () => (await this.driver.findElements(locator)).length === count,
      `Expected ${count} elements: ${locator}`);
  }

  protected async isPresent(locator: Locator) {
    return (await this.driver.findElements(locator)).length > 0;
  }

  async waitForUrl(part: string) {
    await this.driver.wait(until.urlContains(part), TIMEOUT);
  }

  currentUrl() {
    return this.driver.getCurrentUrl();
  }
}
