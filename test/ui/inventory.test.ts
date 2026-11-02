import assert from 'node:assert/strict';
import { useBrowser } from '../../src/core/browser';
import { InventoryPage } from '../../src/pages/inventoryPage';
import { LoginPage } from '../../src/pages/loginPage';
import { BACKPACK, PASSWORD, STANDARD_USER } from '../../src/testData';

describe('Inventory', () => {
  const browser = useBrowser();
  let inventory: InventoryPage;

  beforeEach(async () => {
    inventory = await new LoginPage(browser.driver).loginAs(STANDARD_USER, PASSWORD);
    assert.ok(await inventory.isLoaded());
  });

  it('shows 6 products', async () => {
    assert.equal(await inventory.productCount(), 6);
  });

  it('sorts by name Z to A', async () => {
    await inventory.sortBy('za');
    const names = await inventory.productNames();
    assert.deepEqual(names, [...names].sort().reverse());
  });

  it('sorts by price low to high', async () => {
    await inventory.sortBy('lohi');
    const prices = await inventory.productPrices();
    assert.deepEqual(prices, [...prices].sort((a, b) => a - b));
  });

  it('sorts by price high to low', async () => {
    await inventory.sortBy('hilo');
    const prices = await inventory.productPrices();
    assert.deepEqual(prices, [...prices].sort((a, b) => b - a));
  });

  it('opens the product details page from the list', async () => {
    const product = await inventory.openProduct(BACKPACK);
    assert.equal(await product.productName(), BACKPACK);
    assert.match(await product.currentUrl(), /\/inventory-item\.html\?id=/);
  });
});
