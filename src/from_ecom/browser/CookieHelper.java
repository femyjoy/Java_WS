package from_ecom.browser;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.remote.RemoteWebDriver;

public class CookieHelper {
    private final RemoteWebDriver driver;

    public CookieHelper(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public boolean isPresent(String name) {
        boolean isPresent = false;
        for (Cookie cookie : driver.manage().getCookies()) {
            if (cookie.getName().equals(name) && !cookie.getValue().isEmpty()) {
                isPresent = true;
            }
        }
        return isPresent;
    }

    public void delete(String cookieName) {
        driver.manage().deleteCookieNamed(cookieName);
    }

    public void deleteAll() {
        driver.manage().deleteAllCookies();
    }

    public String get(String name) {
        for (Cookie cookie : driver.manage().getCookies()) {
            if (cookie.getName().equals(name) && !cookie.getValue().isEmpty()) {
                return cookie.getValue();
            }
        }
        throw new IllegalArgumentException("Cookie not found! : " + name);
    }
}
