package io.github.elvinbaxsh.tests.ui;

import static io.github.elvinbaxsh.data.TestData.*;
import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.github.elvinbaxsh.core.BaseTest;
import io.github.elvinbaxsh.pages.InventoryPage;
import io.github.elvinbaxsh.pages.LoginPage;

public class InventoryTest extends BaseTest {

    private InventoryPage inventory;

    @BeforeMethod
    public void login() {
        inventory = new LoginPage(driver()).loginAs(STANDARD_USER, PASSWORD);
        assertTrue(inventory.isLoaded());
    }

    @Test(description = "Shows 6 products")
    public void showsSixProducts() {
        assertEquals(inventory.productCount(), 6);
    }

    @Test(description = "Sort by name Z to A")
    public void sortByNameDescending() {
        List<String> names = inventory.sortBy("za").productNames();
        List<String> expected = new ArrayList<>(names);
        expected.sort(Collections.reverseOrder());
        assertEquals(names, expected);
    }

    @Test(description = "Sort by price low to high")
    public void sortByPriceAscending() {
        List<Double> prices = inventory.sortBy("lohi").productPrices();
        List<Double> expected = new ArrayList<>(prices);
        expected.sort(Comparator.naturalOrder());
        assertEquals(prices, expected);
    }

    @Test(description = "Sort by price high to low")
    public void sortByPriceDescending() {
        List<Double> prices = inventory.sortBy("hilo").productPrices();
        List<Double> expected = new ArrayList<>(prices);
        expected.sort(Comparator.reverseOrder());
        assertEquals(prices, expected);
    }

    @Test(description = "Product details page opens from the list")
    public void productDetailsOpen() {
        String name = inventory.openProduct(BACKPACK).productName();
        assertEquals(name, BACKPACK);
        assertTrue(inventory.currentUrl().contains("/inventory-item.html?id="));
    }
}
