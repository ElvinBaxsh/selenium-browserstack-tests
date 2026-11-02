import { BasePage, testId } from './basePage';
import { InventoryPage } from './inventoryPage';

const USERNAME = testId('username');
const PASSWORD = testId('password');
const LOGIN_BUTTON = testId('login-button');
const ERROR = testId('error');

export class LoginPage extends BasePage {
  async loginAs(username: string, password: string) {
    await this.type(USERNAME, username);
    await this.type(PASSWORD, password);
    await this.click(LOGIN_BUTTON);
    return new InventoryPage(this.driver);
  }

  errorMessage() {
    return this.text(ERROR);
  }

  async isDisplayed() {
    return (await this.visible(LOGIN_BUTTON)).isDisplayed();
  }
}
