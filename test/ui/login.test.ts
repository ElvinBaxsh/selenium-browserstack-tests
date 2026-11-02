import assert from 'node:assert/strict';
import { useBrowser } from '../../src/core/browser';
import { config } from '../../src/core/config';
import { LoginPage } from '../../src/pages/loginPage';
import { LOCKED_OUT_USER, PASSWORD, STANDARD_USER } from '../../src/testData';

describe('Login', () => {
  const browser = useBrowser();
  let loginPage: LoginPage;

  beforeEach(() => {
    loginPage = new LoginPage(browser.driver);
  });

  it('standard user can log in', async () => {
    const inventory = await loginPage.loginAs(STANDARD_USER, PASSWORD);
    assert.ok(await inventory.isLoaded(), 'Products page should open after login');
  });

  it('locked out user sees an error', async () => {
    await loginPage.loginAs(LOCKED_OUT_USER, PASSWORD);
    assert.equal(await loginPage.errorMessage(), 'Epic sadface: Sorry, this user has been locked out.');
  });

  const invalidLogins = [
    { title: 'wrong password', username: STANDARD_USER, password: 'wrong_password', error: 'Epic sadface: Username and password do not match any user in this service' },
    { title: 'missing username', username: '', password: PASSWORD, error: 'Epic sadface: Username is required' },
    { title: 'missing password', username: STANDARD_USER, password: '', error: 'Epic sadface: Password is required' },
  ];

  for (const { title, username, password, error } of invalidLogins) {
    it(`shows an error for ${title}`, async () => {
      await loginPage.loginAs(username, password);
      assert.equal(await loginPage.errorMessage(), error);
    });
  }

  it('inventory page is not reachable without login', async () => {
    await browser.driver.get(`${config.baseUrl}/inventory.html`);
    assert.equal(await loginPage.errorMessage(), "Epic sadface: You can only access '/inventory.html' when you are logged in.");
  });

  it('user can log out', async () => {
    const inventory = await loginPage.loginAs(STANDARD_USER, PASSWORD);
    const afterLogout = await inventory.logout();
    assert.ok(await afterLogout.isDisplayed(), 'Login page should be shown after logout');
    assert.equal(await browser.driver.getCurrentUrl(), `${config.baseUrl}/`);
  });
});
