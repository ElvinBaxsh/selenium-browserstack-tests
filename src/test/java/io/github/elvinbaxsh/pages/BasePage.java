package io.github.elvinbaxsh.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/** Shared helpers: every action waits for the element first (explicit waits, no Thread.sleep). */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected static By testId(String id) {
        return By.cssSelector("[data-test='" + id + "']");
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> allVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement element = visible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String text(By locator) {
        return visible(locator).getText();
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    public void waitForUrl(String part) {
        wait.until(ExpectedConditions.urlContains(part));
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    // "Item total: $39.98" -> 39.98
    protected static double toNumber(String text) {
        return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
    }
}
