package io.github.elvinbaxsh.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class InventoryPage extends BasePage {

    private static final By TITLE = testId("title");
    private static final By ITEMS = testId("inventory-item");
    private static final By ITEM_NAMES = testId("inventory-item-name");
    private static final By ITEM_PRICES = testId("inventory-item-price");
    private static final By SORT = testId("product-sort-container");
    private static final By CART_BADGE = testId("shopping-cart-badge");
    private static final By CART_LINK = testId("shopping-cart-link");
    private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
    private static final By LOGOUT_LINK = testId("logout-sidebar-link");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        waitForUrl("/inventory.html");
        return text(TITLE).equals("Products");
    }

    public int productCount() {
        return allVisible(ITEMS).size();
    }

    // Each product card has one button: "Add to cart" or "Remove"
    private By productButton(String productName) {
        return By.xpath("//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and text()='"
                + productName + "']]//button");
    }

    public InventoryPage addToCart(String productName) {
        click(productButton(productName));
        wait.until(ExpectedConditions.textToBe(productButton(productName), "Remove"));
        return this;
    }

    public InventoryPage removeFromCart(String productName) {
        click(productButton(productName));
        wait.until(ExpectedConditions.textToBe(productButton(productName), "Add to cart"));
        return this;
    }

    public String cartBadge() {
        return text(CART_BADGE);
    }

    public boolean isCartBadgeShown() {
        return isPresent(CART_BADGE);
    }

    public InventoryPage sortBy(String value) {
        new Select(visible(SORT)).selectByValue(value);
        return this;
    }

    public List<String> productNames() {
        return allVisible(ITEM_NAMES).stream().map(WebElement::getText).toList();
    }

    public List<Double> productPrices() {
        return allVisible(ITEM_PRICES).stream().map(e -> toNumber(e.getText())).toList();
    }

    public ProductPage openProduct(String productName) {
        click(By.xpath("//div[@data-test='inventory-item-name' and text()='" + productName + "']"));
        return new ProductPage(driver);
    }

    public CartPage openCart() {
        click(CART_LINK);
        waitForUrl("/cart.html");
        return new CartPage(driver);
    }

    public LoginPage logout() {
        click(MENU_BUTTON);
        click(LOGOUT_LINK);
        return new LoginPage(driver);
    }
}
