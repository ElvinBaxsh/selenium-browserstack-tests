# Selenium BrowserStack Tests

[![Selenium Tests](https://github.com/ElvinBaxsh/selenium-browserstack-tests/actions/workflows/selenium.yml/badge.svg)](https://github.com/ElvinBaxsh/selenium-browserstack-tests/actions/workflows/selenium.yml)

UI and API test automation with **Selenium WebDriver**, **Java**, **TestNG** and **RestAssured**, with cross-browser runs on **BrowserStack**.

- **UI tests:** [Sauce Demo](https://www.saucedemo.com), an e-commerce demo store
- **API tests:** [Automation Exercise](https://automationexercise.com/api_list) public REST API
- **Browsers:** Chrome, Firefox and Edge on every push; Windows 11 (Chrome, Edge) and macOS (Firefox, Safari) on BrowserStack

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

- **Page Object Model:** locators and actions live in `pages/`, tests read like user steps (`loginAs(...).addToCart(...).openCart()`)
- **Explicit waits only:** every action waits for its element with `WebDriverWait`, no `Thread.sleep`
- **Parallel runs:** a `ThreadLocal` driver gives every test its own browser
- **Data-driven tests:** TestNG `@DataProvider` for validation cases
- **One codebase, two targets:** `DriverFactory` starts a local browser or a BrowserStack remote browser from the same tests
- **BrowserStack dashboard:** every session is named after its test and marked passed or failed
- **On failure:** a screenshot is saved to `target/screenshots/`
- **API tests:** RestAssured
- **CI:** GitHub Actions runs local browsers and API tests on each push and pull request; BrowserStack runs on manual start and once a week

## Run locally

Only Java 17 is needed: the Maven Wrapper downloads Maven, and Selenium Manager downloads the browser drivers.

```bash
./mvnw test                                        # all tests in headless Chrome
./mvnw test -Dsuite=testng-ui.xml -Dbrowser=firefox
./mvnw test -Dsuite=testng-api.xml                 # API tests only
./mvnw test -Dheadless=false                       # watch the browser
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

## Run on BrowserStack

```bash
export BROWSERSTACK_USERNAME=your_username
export BROWSERSTACK_ACCESS_KEY=your_access_key
./mvnw test -Dsuite=testng-browserstack.xml -Dgrid=browserstack -Dbrowser=safari -Dos="OS X" -DosVersion=Sonoma
```

In GitHub Actions the keys are stored as repository secrets `BROWSERSTACK_USERNAME` and `BROWSERSTACK_ACCESS_KEY`.

## Tech stack

Java 17 · Selenium WebDriver 4 · TestNG · RestAssured · Maven · BrowserStack · GitHub Actions
