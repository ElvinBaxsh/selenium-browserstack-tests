package io.github.elvinbaxsh.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariOptions;

/** Creates a local browser (drivers are resolved by Selenium Manager) or a BrowserStack remote browser. */
public final class DriverFactory {

    private static final String BROWSERSTACK_HUB = "https://hub-cloud.browserstack.com/wd/hub";

    private DriverFactory() {
    }

    public static WebDriver create(String testName) {
        return Config.onBrowserStack() ? createRemote(testName) : createLocal();
    }

    private static WebDriver createLocal() {
        boolean headless = Config.headless();
        switch (Config.browser()) {
            case "firefox": {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) options.addArguments("-headless");
                return new FirefoxDriver(options);
            }
            case "edge": {
                EdgeOptions options = new EdgeOptions();
                if (headless) options.addArguments("--headless=new");
                options.addArguments("--window-size=1280,800");
                return new EdgeDriver(options);
            }
            default: {
                ChromeOptions options = new ChromeOptions();
                if (headless) options.addArguments("--headless=new");
                // Turns off Chrome's "change your password" pop-up, which blocks clicks after login
                options.setExperimentalOption("prefs", Map.of(
                        "credentials_enable_service", false,
                        "profile.password_manager_enabled", false,
                        "profile.password_manager_leak_detection", false));
                options.addArguments("--window-size=1280,800", "--guest");
                return new ChromeDriver(options);
            }
        }
    }

    private static WebDriver createRemote(String testName) {
        if (Config.browserStackUser().isEmpty() || Config.browserStackKey().isEmpty()) {
            throw new IllegalStateException("Set BROWSERSTACK_USERNAME and BROWSERSTACK_ACCESS_KEY to run on BrowserStack");
        }

        MutableCapabilities capabilities = switch (Config.browser()) {
            case "firefox" -> new FirefoxOptions();
            case "edge" -> new EdgeOptions();
            case "safari" -> new SafariOptions();
            default -> new ChromeOptions();
        };

        Map<String, Object> bstack = new HashMap<>();
        bstack.put("os", Config.os());
        bstack.put("osVersion", Config.osVersion());
        bstack.put("projectName", "Selenium BrowserStack Tests");
        bstack.put("buildName", Config.buildName());
        bstack.put("sessionName", testName);
        bstack.put("userName", Config.browserStackUser());
        bstack.put("accessKey", Config.browserStackKey());
        capabilities.setCapability("bstack:options", bstack);

        try {
            return new RemoteWebDriver(new URL(BROWSERSTACK_HUB), capabilities);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }
}
