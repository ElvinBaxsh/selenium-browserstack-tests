import assert from 'node:assert/strict';
import { useBrowser } from '../../src/core/browser';
import { InventoryPage } from '../../src/pages/inventoryPage';
import { LoginPage } from '../../src/pages/loginPage';
import { BACKPACK, BIKE_LIGHT, PASSWORD, STANDARD_USER } from '../../src/testData';

describe('Cart', () => {
  const browser = useBrowser();
  let inventory: InventoryPage;

  beforeEach(async () => {
    inventory = await new LoginPage(browser.driver).loginAs(STANDARD_USER, PASSWORD);
    assert.ok(await inventory.isLoaded());
  });

  it('adding products updates the cart badge', async () => {
    await inventory.addToCart(BACKPACK);
    assert.equal(await inventory.cartBadge(), '1');
    await inventory.addToCart(BIKE_LIGHT);
    assert.equal(await inventory.cartBadge(), '2');
  });

  it('removing a product from the list hides the badge', async () => {
    await inventory.addToCart(BACKPACK);
    await inventory.removeFromCart(BACKPACK);
    assert.equal(await inventory.isCartBadgeShown(), false, 'Cart badge should disappear when the cart is empty');
  });

  it('cart shows the added products', async () => {
    await inventory.addToCart(BACKPACK);
    await inventory.addToCart(BIKE_LIGHT);
    const cart = await inventory.openCart();
    assert.deepEqual(await cart.waitForItems(2), [BACKPACK, BIKE_LIGHT]);
  });

  it('product can be removed inside the cart', async () => {
    await inventory.addToCart(BACKPACK);
    await inventory.addToCart(BIKE_LIGHT);
    const cart = await inventory.openCart();
    await cart.removeItem(BACKPACK);
    assert.deepEqual(await cart.waitForItems(1), [BIKE_LIGHT]);
  });

  it('cart items stay after page reload', async () => {
    await inventory.addToCart(BACKPACK);
    const cart = await inventory.openCart();
    await cart.reload();
    assert.deepEqual(await cart.waitForItems(1), [BACKPACK]);
  });
});
