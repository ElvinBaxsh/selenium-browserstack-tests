import { BasePage, testId } from './basePage';

const NAME = testId('inventory-item-name');
const BACK_BUTTON = testId('back-to-products');

export class ProductPage extends BasePage {
  async productName() {
    await this.visible(BACK_BUTTON);
    return this.text(NAME);
  }
}
