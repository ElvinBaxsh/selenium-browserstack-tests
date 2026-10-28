package io.github.elvinbaxsh.tests.ui;

import static io.github.elvinbaxsh.data.TestData.*;
import static org.testng.Assert.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.elvinbaxsh.core.BaseTest;
import io.github.elvinbaxsh.pages.CheckoutPage;
import io.github.elvinbaxsh.pages.LoginPage;

public class CheckoutTest extends BaseTest {

    private CheckoutPage checkout;

    @BeforeMethod
    public void openCheckout() {
        checkout = new LoginPage(driver())
                .loginAs(STANDARD_USER, PASSWORD)
                .addToCart(BACKPACK)
                .addToCart(BOLT_SHIRT)
                .openCart()
                .checkout();
    }

    @Test(description = "User completes an order end to end")
    public void completeOrder() {
        String message = checkout.fillInfo(FIRST_NAME, LAST_NAME, POSTAL_CODE).finish().completeMessage();
        assertEquals(message, "Thank you for your order!");
    }

    @Test(description = "Order total = item prices + tax")
    public void totalIsCalculatedCorrectly() {
        checkout.fillInfo(FIRST_NAME, LAST_NAME, POSTAL_CODE);
        double itemsSum = checkout.itemPrices().stream().mapToDouble(Double::doubleValue).sum();

        assertEquals(checkout.subtotal(), itemsSum, 0.001);
        assertEquals(checkout.total(), checkout.subtotal() + checkout.tax(), 0.001);
    }

    @DataProvider(name = "missingFields")
    public Object[][] missingFields() {
        return new Object[][] {
            { "", LAST_NAME, POSTAL_CODE, "Error: First Name is required" },
            { FIRST_NAME, "", POSTAL_CODE, "Error: Last Name is required" },
            { FIRST_NAME, LAST_NAME, "", "Error: Postal Code is required" },
        };
    }

    @Test(dataProvider = "missingFields", description = "Required fields are validated")
    public void requiredFieldsAreValidated(String firstName, String lastName, String postalCode, String expectedError) {
        checkout.fillInfo(firstName, lastName, postalCode);
        assertEquals(checkout.errorMessage(), expectedError);
    }
}
