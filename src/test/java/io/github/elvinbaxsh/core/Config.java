package io.github.elvinbaxsh.core;

/**
 * Run settings, read from -D system properties or environment variables.
 * Example: mvnw test -Dbrowser=firefox -Dheadless=false
 */
public final class Config {

    public static final String BASE_URL = "https://www.saucedemo.com";
    public static final String API_URL = "https://automationexercise.com/api";

    private Config() {
    }

    /** chrome, firefox, edge or safari */
    public static String browser() {
        return get("browser", "chrome").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "true"));
    }

    /** "local" runs on this machine, "browserstack" runs in the BrowserStack cloud. */
    public static boolean onBrowserStack() {
        return get("grid", "local").equalsIgnoreCase("browserstack");
    }

    public static String os() {
        return get("os", "Windows");
    }

    public static String osVersion() {
        return get("osVersion", "11");
    }

    public static String browserStackUser() {
        return get("BROWSERSTACK_USERNAME", "");
    }

    public static String browserStackKey() {
        return get("BROWSERSTACK_ACCESS_KEY", "");
    }

    public static String buildName() {
        return get("buildName", "selenium-browserstack-tests");
    }

    private static String get(String key, String fallback) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) {
            value = System.getenv(key);
        }
        return value == null || value.isBlank() ? fallback : value;
    }
}
