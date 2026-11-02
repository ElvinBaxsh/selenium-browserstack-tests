import { BasePage, testId, toNumber } from './basePage';

const FIRST_NAME = testId('firstName');
const LAST_NAME = testId('lastName');
const POSTAL_CODE = testId('postalCode');
const CONTINUE = testId('continue');
const FINISH = testId('finish');
const ERROR = testId('error');
const ITEM_PRICES = testId('inventory-item-price');
const SUBTOTAL = testId('subtotal-label');
const TAX = testId('tax-label');
const TOTAL = testId('total-label');
const COMPLETE_HEADER = testId('complete-header');

export class CheckoutPage extends BasePage {
  async fillInfo(firstName: string, lastName: string, postalCode: string) {
    if (firstName) await this.type(FIRST_NAME, firstName);
    if (lastName) await this.type(LAST_NAME, lastName);
    if (postalCode) await this.type(POSTAL_CODE, postalCode);
    await this.click(CONTINUE);
  }

  errorMessage() {
    return this.text(ERROR);
  }

  async itemPrices() {
    await this.waitForUrl('/checkout-step-two.html');
    return (await this.allTexts(ITEM_PRICES)).map(toNumber);
  }

  async subtotal() {
    return toNumber(await this.text(SUBTOTAL));
  }

  async tax() {
    return toNumber(await this.text(TAX));
  }

  async total() {
    return toNumber(await this.text(TOTAL));
  }

  async finish() {
    await this.click(FINISH);
  }

  completeMessage() {
    return this.text(COMPLETE_HEADER);
  }
}
