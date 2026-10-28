package io.github.elvinbaxsh.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME = testId("username");
    private static final By PASSWORD = testId("password");
    private static final By LOGIN_BUTTON = testId("login-button");
    private static final By ERROR = testId("error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public InventoryPage loginAs(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);
        return new InventoryPage(driver);
    }

    public String errorMessage() {
        return text(ERROR);
    }

    public boolean isDisplayed() {
        return visible(LOGIN_BUTTON).isDisplayed();
    }
}
