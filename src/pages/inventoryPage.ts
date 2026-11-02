import { By } from 'selenium-webdriver';
import { Select } from 'selenium-webdriver/lib/select';
import { BasePage, testId, toNumber } from './basePage';
import { CartPage } from './cartPage';
import { LoginPage } from './loginPage';
import { ProductPage } from './productPage';

const TITLE = testId('title');
const ITEMS = testId('inventory-item');
const ITEM_NAMES = testId('inventory-item-name');
const ITEM_PRICES = testId('inventory-item-price');
const SORT = testId('product-sort-container');
const CART_BADGE = testId('shopping-cart-badge');
const CART_LINK = testId('shopping-cart-link');
const MENU_BUTTON = By.id('react-burger-menu-btn');
const LOGOUT_LINK = testId('logout-sidebar-link');

// Each product card has one button: "Add to cart" or "Remove"
const productButton = (productName: string) =>
  By.xpath(`//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and text()='${productName}']]//button`);

export class InventoryPage extends BasePage {
  async isLoaded() {
    await this.waitForUrl('/inventory.html');
    return (await this.text(TITLE)) === 'Products';
  }

  async productCount() {
    return (await this.allVisible(ITEMS)).length;
  }

  async addToCart(productName: string) {
    await this.click(productButton(productName));
    await this.waitForText(productButton(productName), 'Remove');
  }

  async removeFromCart(productName: string) {
    await this.click(productButton(productName));
    await this.waitForText(productButton(productName), 'Add to cart');
  }

  cartBadge() {
    return this.text(CART_BADGE);
  }

  isCartBadgeShown() {
    return this.isPresent(CART_BADGE);
  }

  async sortBy(value: string) {
    await new Select(await this.visible(SORT)).selectByValue(value);
  }

  productNames() {
    return this.allTexts(ITEM_NAMES);
  }

  async productPrices() {
    return (await this.allTexts(ITEM_PRICES)).map(toNumber);
  }

  async openProduct(productName: string) {
    await this.click(By.xpath(`//div[@data-test='inventory-item-name' and text()='${productName}']`));
    return new ProductPage(this.driver);
  }

  async openCart() {
    await this.click(CART_LINK);
    await this.waitForUrl('/cart.html');
    return new CartPage(this.driver);
  }

  async logout() {
    await this.click(MENU_BUTTON);
    await this.click(LOGOUT_LINK);
    return new LoginPage(this.driver);
  }
}
