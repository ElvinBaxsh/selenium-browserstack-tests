package io.github.elvinbaxsh.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductPage extends BasePage {

    private static final By NAME = testId("inventory-item-name");
    private static final By BACK_BUTTON = testId("back-to-products");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public String productName() {
        visible(BACK_BUTTON);
        return text(NAME);
    }
}
