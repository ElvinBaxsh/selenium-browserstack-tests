package io.github.elvinbaxsh.tests.ui;

import static io.github.elvinbaxsh.data.TestData.*;
import static org.testng.Assert.*;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.elvinbaxsh.core.BaseTest;
import io.github.elvinbaxsh.pages.CartPage;
import io.github.elvinbaxsh.pages.InventoryPage;
import io.github.elvinbaxsh.pages.LoginPage;

public class CartTest extends BaseTest {

    private InventoryPage inventory;

    @BeforeMethod
    public void login() {
        inventory = new LoginPage(driver()).loginAs(STANDARD_USER, PASSWORD);
        assertTrue(inventory.isLoaded());
    }

    @Test(description = "Adding products updates the cart badge")
    public void addingProductsUpdatesBadge() {
        inventory.addToCart(BACKPACK);
        assertEquals(inventory.cartBadge(), "1");
        inventory.addToCart(BIKE_LIGHT);
        assertEquals(inventory.cartBadge(), "2");
    }

    @Test(description = "Removing a product from the list updates the badge")
    public void removingProductHidesBadge() {
        inventory.addToCart(BACKPACK).removeFromCart(BACKPACK);
        assertFalse(inventory.isCartBadgeShown(), "Cart badge should disappear when the cart is empty");
    }

    @Test(description = "Cart shows the added products")
    public void cartShowsAddedProducts() {
        CartPage cart = inventory.addToCart(BACKPACK).addToCart(BIKE_LIGHT).openCart();
        assertEquals(cart.waitForItems(2), List.of(BACKPACK, BIKE_LIGHT));
    }

    @Test(description = "Product can be removed inside the cart")
    public void productCanBeRemovedInCart() {
        CartPage cart = inventory.addToCart(BACKPACK).addToCart(BIKE_LIGHT).openCart();
        cart.removeItem(BACKPACK);
        assertEquals(cart.itemNames(), List.of(BIKE_LIGHT));
    }

    @Test(description = "Cart items stay after page reload")
    public void cartKeptAfterReload() {
        CartPage cart = inventory.addToCart(BACKPACK).openCart();
        cart.reload();
        assertEquals(cart.waitForItems(1), List.of(BACKPACK));
    }
}
