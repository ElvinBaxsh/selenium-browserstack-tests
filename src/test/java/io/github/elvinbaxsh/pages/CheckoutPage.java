package io.github.elvinbaxsh.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutPage extends BasePage {

    private static final By FIRST_NAME = testId("firstName");
    private static final By LAST_NAME = testId("lastName");
    private static final By POSTAL_CODE = testId("postalCode");
    private static final By CONTINUE = testId("continue");
    private static final By FINISH = testId("finish");
    private static final By ERROR = testId("error");
    private static final By ITEM_PRICES = testId("inventory-item-price");
    private static final By SUBTOTAL = testId("subtotal-label");
    private static final By TAX = testId("tax-label");
    private static final By TOTAL = testId("total-label");
    private static final By COMPLETE_HEADER = testId("complete-header");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutPage fillInfo(String firstName, String lastName, String postalCode) {
        if (!firstName.isEmpty()) type(FIRST_NAME, firstName);
        if (!lastName.isEmpty()) type(LAST_NAME, lastName);
        if (!postalCode.isEmpty()) type(POSTAL_CODE, postalCode);
        click(CONTINUE);
        return this;
    }

    public String errorMessage() {
        return text(ERROR);
    }

    public List<Double> itemPrices() {
        waitForUrl("/checkout-step-two.html");
        return allVisible(ITEM_PRICES).stream().map(e -> toNumber(e.getText())).toList();
    }

    public double subtotal() {
        return toNumber(text(SUBTOTAL));
    }

    public double tax() {
        return toNumber(text(TAX));
    }

    public double total() {
        return toNumber(text(TOTAL));
    }

    public CheckoutPage finish() {
        click(FINISH);
        return this;
    }

    public String completeMessage() {
        return text(COMPLETE_HEADER);
    }
}
