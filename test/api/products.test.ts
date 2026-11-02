import assert from 'node:assert/strict';
import { config } from '../../src/core/config';

/**
 * automationexercise.com always answers with HTTP 200 and puts the real status in "responseCode".
 * It also sends JSON with a text/html header, so the body is parsed manually.
 */
async function api(method: string, path: string, form?: Record<string, string>) {
  const response = await fetch(`${config.apiUrl}${path}`, {
    method,
    body: form && new URLSearchParams(form),
  });
  assert.equal(response.status, 200);
  return JSON.parse(await response.text());
}

describe('Products API', () => {
  it('GET /productsList returns all products', async () => {
    const body = await api('GET', '/productsList');
    assert.equal(body.responseCode, 200);
    assert.ok(body.products.length > 0);
    for (const product of body.products) {
      for (const key of ['id', 'name', 'price', 'brand', 'category']) assert.ok(key in product, `Missing ${key}`);
      assert.match(product.price, /^Rs\. \d+$/);
    }
  });

  it('POST /productsList is not supported', async () => {
    const body = await api('POST', '/productsList');
    assert.equal(body.responseCode, 405);
    assert.equal(body.message, 'This request method is not supported.');
  });

  it('GET /brandsList returns brands', async () => {
    const body = await api('GET', '/brandsList');
    assert.equal(body.responseCode, 200);
    assert.ok(body.brands.length > 0);
    assert.ok(body.brands[0].brand);
  });

  it('POST /searchProduct finds matching products', async () => {
    const body = await api('POST', '/searchProduct', { search_product: 'top' });
    assert.equal(body.responseCode, 200);
    assert.ok(body.products.length > 0);
    for (const product of body.products) {
      const text = `${product.name} ${product.category.category}`.toLowerCase();
      assert.ok(text.includes('top'), `Search result should match 'top': ${text}`);
    }
  });

  it('POST /searchProduct without parameter returns 400', async () => {
    const body = await api('POST', '/searchProduct');
    assert.equal(body.responseCode, 400);
    assert.equal(body.message, 'Bad request, search_product parameter is missing in POST request.');
  });

  it('POST /verifyLogin with unknown user returns 404', async () => {
    const body = await api('POST', '/verifyLogin', { email: 'no-such-user@example.com', password: 'wrong' });
    assert.equal(body.responseCode, 404);
    assert.equal(body.message, 'User not found!');
  });

  it('POST /verifyLogin without email returns 400', async () => {
    const body = await api('POST', '/verifyLogin', { password: 'wrong' });
    assert.equal(body.responseCode, 400);
    assert.equal(body.message, 'Bad request, email or password parameter is missing in POST request.');
  });

  it('DELETE /verifyLogin is not supported', async () => {
    const body = await api('DELETE', '/verifyLogin');
    assert.equal(body.responseCode, 405);
    assert.equal(body.message, 'This request method is not supported.');
  });
});
