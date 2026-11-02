# Selenium BrowserStack Tests

[![Selenium Tests](https://github.com/ElvinBaxsh/selenium-browserstack-tests/actions/workflows/selenium.yml/badge.svg)](https://github.com/ElvinBaxsh/selenium-browserstack-tests/actions/workflows/selenium.yml)

UI and API test automation with **Selenium WebDriver**, **TypeScript** and **Mocha**, ready for cross-browser runs on **BrowserStack**.

- **UI tests:** [Sauce Demo](https://www.saucedemo.com), an e-commerce demo store
- **API tests:** [Automation Exercise](https://automationexercise.com/api_list) public REST API
- **Browsers:** Chrome, Firefox and Edge on every push; Windows 11 (Chrome, Edge) and macOS (Firefox, Safari) configured for BrowserStack

The same scenarios are also automated with [Playwright](https://github.com/ElvinBaxsh/playwright-e2e-tests) and [Cypress](https://github.com/ElvinBaxsh/cypress-e2e-tests).

## What is covered

| Area | Scenarios |
|---|---|
| Login | valid login, locked out user, wrong password, required fields, protected page without login, logout |
| Products | product count, sort by name and price, product details page |
| Cart | add and remove products, cart badge, cart content, cart kept after reload |
| Checkout | full order flow, total = items + tax, required field validation |
| API | product and brand lists, product search, unsupported methods (405), missing parameters (400), unknown user (404) |

**30 test cases** in total: 22 UI and 8 API.

## Approach

- **Page Object Model:** locators and actions live in `pages/`, tests read like user steps (`loginAs(...)`, `addToCart(...)`, `openCart()`)
- **Explicit waits only:** every action waits for its element with `driver.wait`, no sleeps
- **Parallel runs:** Mocha runs test files in parallel and every test gets its own browser (`useBrowser()`)
- **Data-driven tests:** validation cases are generated from data tables
- **One codebase, two targets:** `driverFactory.ts` starts a local browser or a BrowserStack remote browser from the same tests
- **BrowserStack dashboard:** every session is named after its test and marked passed or failed
- **On failure:** a screenshot is saved to `reports/screenshots/`
- **API tests:** Node.js `fetch` with `node:assert`
- **CI:** GitHub Actions runs local browsers and API tests on each push and pull request; BrowserStack runs on manual start and once a week

## Run locally

Only Node.js is needed: Selenium Manager downloads the browser drivers.

```bash
npm install
npm test                # all tests in headless Chrome
npm run test:firefox    # UI tests in Firefox
npm run test:edge       # UI tests in Edge
npm run test:api        # API tests only
npm run test:headed     # watch the browser
```

## Run on BrowserStack

```bash
export BROWSERSTACK_USERNAME=your_username
export BROWSERSTACK_ACCESS_KEY=your_access_key
BROWSER=safari BS_OS="OS X" BS_OS_VERSION=Sonoma npm run test:browserstack
```

In GitHub Actions the keys are stored as repository secrets `BROWSERSTACK_USERNAME` and `BROWSERSTACK_ACCESS_KEY`.

## Tech stack

Selenium WebDriver 4 · TypeScript · Mocha · Node.js · BrowserStack · GitHub Actions
