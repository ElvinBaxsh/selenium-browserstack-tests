package io.github.elvinbaxsh.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/** Opens a fresh browser for every test and closes it afterwards. Safe for parallel runs. */
public abstract class BaseTest {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    protected WebDriver driver() {
        return DRIVER.get();
    }

    @BeforeMethod(alwaysRun = true)
    public void startBrowser(Method method) {
        String testName = getClass().getSimpleName() + " - " + method.getName();
        WebDriver driver = DriverFactory.create(testName);
        DRIVER.set(driver);
        driver.get(Config.BASE_URL);
    }

    @AfterMethod(alwaysRun = true)
    public void stopBrowser(ITestResult result) {
        WebDriver driver = DRIVER.get();
        if (driver == null) return;
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                saveScreenshot(driver, result.getMethod().getMethodName());
            }
            if (Config.onBrowserStack()) {
                markBrowserStackSession(driver, result);
            }
        } finally {
            driver.quit();
            DRIVER.remove();
        }
    }

    private void saveScreenshot(WebDriver driver, String name) {
        try {
            Path target = Path.of("target", "screenshots", getClass().getSimpleName() + "-" + name + ".png");
            Files.createDirectories(target.getParent());
            Path source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath();
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | RuntimeException e) {
            System.err.println("Could not save screenshot: " + e.getMessage());
        }
    }

    // Shows passed / failed on the BrowserStack dashboard
    private void markBrowserStackSession(WebDriver driver, ITestResult result) {
        boolean passed = result.getStatus() == ITestResult.SUCCESS;
        String reason = passed ? "Passed" : String.valueOf(result.getThrowable()).replace("\"", "'");
        if (reason.length() > 250) reason = reason.substring(0, 250);
        String script = String.format(
                "browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"%s\", \"reason\": \"%s\"}}",
                passed ? "passed" : "failed", reason);
        ((JavascriptExecutor) driver).executeScript(script);
    }
}
