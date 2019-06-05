package from_ecom.browser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class PageHelper {
    private final RemoteWebDriver driver;

    public PageHelper(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public Tags tags() {
        return new Tags(driver);
    }

    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }

    public void get(String url) {
        driver.get(url);
    }

    public WebElement findElement(By by) {
        return driver.findElement(by);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    public String getHeaderText() {
        return findElement(By.tagName("h1")).getText();
    }

    public boolean isTextDisplaying(String text) {
        return findElement(By.tagName("body")).getText().contains(text);
    }

    @Deprecated //Use waiter instead
    public <T> T waitUntil(ExpectedCondition<T> condition) {
        return new WebDriverWait(driver, 3).until(condition);
    }

    @Deprecated //Use waiter instead
    public <T> T waitUntil(ExpectedCondition<T> condition, long timeOutInSeconds) {
        return new WebDriverWait(driver, timeOutInSeconds).until(condition);
    }


}
