import { By } from 'selenium-webdriver';
import { BasePage, testId } from './basePage';
import { CheckoutPage } from './checkoutPage';

const ITEM_NAMES = testId('inventory-item-name');
const CHECKOUT = testId('checkout');

const cartItem = (productName: string) =>
  By.xpath(`//div[@data-test='inventory-item'][.//div[text()='${productName}']]`);

export class CartPage extends BasePage {
  async waitForItems(count: number) {
    await this.waitForCount(ITEM_NAMES, count);
    return this.allTexts(ITEM_NAMES);
  }

  async removeItem(productName: string) {
    await this.click(By.xpath(`${(cartItem(productName) as By).value}//button`));
    await this.waitForCount(cartItem(productName), 0);
  }

  async reload() {
    await this.driver.navigate().refresh();
  }

  async checkout() {
    await this.click(CHECKOUT);
    await this.waitForUrl('/checkout-step-one.html');
    return new CheckoutPage(this.driver);
  }
}
