import assert from 'node:assert/strict';
import { useBrowser } from '../../src/core/browser';
import { CheckoutPage } from '../../src/pages/checkoutPage';
import { LoginPage } from '../../src/pages/loginPage';
import { BACKPACK, BOLT_SHIRT, FIRST_NAME, LAST_NAME, PASSWORD, POSTAL_CODE, STANDARD_USER } from '../../src/testData';

describe('Checkout', () => {
  const browser = useBrowser();
  let checkout: CheckoutPage;

  beforeEach(async () => {
    const inventory = await new LoginPage(browser.driver).loginAs(STANDARD_USER, PASSWORD);
    await inventory.addToCart(BACKPACK);
    await inventory.addToCart(BOLT_SHIRT);
    const cart = await inventory.openCart();
    checkout = await cart.checkout();
  });

  it('user completes an order end to end', async () => {
    await checkout.fillInfo(FIRST_NAME, LAST_NAME, POSTAL_CODE);
    await checkout.finish();
    assert.equal(await checkout.completeMessage(), 'Thank you for your order!');
  });

  it('order total equals item prices plus tax', async () => {
    await checkout.fillInfo(FIRST_NAME, LAST_NAME, POSTAL_CODE);
    const itemsSum = (await checkout.itemPrices()).reduce((sum, price) => sum + price, 0);
    const subtotal = await checkout.subtotal();

    assert.ok(Math.abs(subtotal - itemsSum) < 0.001, `Subtotal ${subtotal}, items ${itemsSum}`);
    assert.ok(Math.abs((await checkout.total()) - (subtotal + (await checkout.tax()))) < 0.001);
  });

  const missingFields = [
    { field: 'first name', first: '', last: LAST_NAME, postal: POSTAL_CODE, error: 'Error: First Name is required' },
    { field: 'last name', first: FIRST_NAME, last: '', postal: POSTAL_CODE, error: 'Error: Last Name is required' },
    { field: 'postal code', first: FIRST_NAME, last: LAST_NAME, postal: '', error: 'Error: Postal Code is required' },
  ];

  for (const { field, first, last, postal, error } of missingFields) {
    it(`${field} is required`, async () => {
      await checkout.fillInfo(first, last, postal);
      assert.equal(await checkout.errorMessage(), error);
    });
  }
});
