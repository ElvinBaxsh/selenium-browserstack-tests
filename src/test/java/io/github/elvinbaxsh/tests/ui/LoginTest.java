package io.github.elvinbaxsh.tests.ui;

import static io.github.elvinbaxsh.data.TestData.*;
import static org.testng.Assert.*;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.elvinbaxsh.core.BaseTest;
import io.github.elvinbaxsh.core.Config;
import io.github.elvinbaxsh.pages.LoginPage;

public class LoginTest extends BaseTest {

    @Test(description = "Standard user can log in")
    public void standardUserCanLogIn() {
        boolean loaded = new LoginPage(driver()).loginAs(STANDARD_USER, PASSWORD).isLoaded();
        assertTrue(loaded, "Products page should open after login");
    }

    @Test(description = "Locked out user sees an error")
    public void lockedOutUserSeesError() {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.loginAs(LOCKED_OUT_USER, PASSWORD);
        assertEquals(loginPage.errorMessage(), "Epic sadface: Sorry, this user has been locked out.");
    }

    @DataProvider(name = "invalidLogins")
    public Object[][] invalidLogins() {
        return new Object[][] {
            { STANDARD_USER, "wrong_password", "Epic sadface: Username and password do not match any user in this service" },
            { "", PASSWORD, "Epic sadface: Username is required" },
            { STANDARD_USER, "", "Epic sadface: Password is required" },
        };
    }

    @Test(dataProvider = "invalidLogins", description = "Invalid credentials and required fields are validated")
    public void invalidLoginShowsError(String username, String password, String expectedError) {
        LoginPage loginPage = new LoginPage(driver());
        loginPage.loginAs(username, password);
        assertEquals(loginPage.errorMessage(), expectedError);
    }

    @Test(description = "Inventory page is not reachable without login")
    public void inventoryRequiresLogin() {
        driver().get(Config.BASE_URL + "/inventory.html");
        LoginPage loginPage = new LoginPage(driver());
        assertEquals(loginPage.errorMessage(),
                "Epic sadface: You can only access '/inventory.html' when you are logged in.");
    }

    @Test(description = "User can log out")
    public void userCanLogOut() {
        LoginPage loginPage = new LoginPage(driver()).loginAs(STANDARD_USER, PASSWORD).logout();
        assertTrue(loginPage.isDisplayed(), "Login page should be shown after logout");
        assertEquals(driver().getCurrentUrl(), Config.BASE_URL + "/");
    }
}
