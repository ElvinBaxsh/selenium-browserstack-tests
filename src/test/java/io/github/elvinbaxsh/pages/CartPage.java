package io.github.elvinbaxsh.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CartPage extends BasePage {

    private static final By ITEM_NAMES = testId("inventory-item-name");
    private static final By CHECKOUT = testId("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public List<String> itemNames() {
        return driver.findElements(ITEM_NAMES).stream().map(WebElement::getText).toList();
    }

    public List<String> waitForItems(int count) {
        wait.until(ExpectedConditions.numberOfElementsToBe(ITEM_NAMES, count));
        return itemNames();
    }

    public CartPage removeItem(String productName) {
        By item = By.xpath("//div[@data-test='inventory-item'][.//div[text()='" + productName + "']]");
        click(By.xpath("//div[@data-test='inventory-item'][.//div[text()='" + productName + "']]//button"));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(item));
        return this;
    }

    public void reload() {
        driver.navigate().refresh();
    }

    public CheckoutPage checkout() {
        click(CHECKOUT);
        waitForUrl("/checkout-step-one.html");
        return new CheckoutPage(driver);
    }
}
