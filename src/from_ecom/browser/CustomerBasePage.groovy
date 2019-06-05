package from_ecom.browser
import PageObjects.helper.PageHelper
import PageObjects.helper.PagePredicateBuilder
import PageObjects.helper.ShopComponentFactory
import PageObjects.helper.Waiter
import com.google.common.base.Predicate
import framework.browser.BrowserFactory
import framework.browser.WebBrowser
import groovy.json.JsonSlurper
import org.junit.Assert
import org.openqa.selenium.*
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.support.ui.ExpectedCondition

import static PageObjects.helper.PagePredicateBuilder.pageIsReady
import static framework.browser.JavaScriptFactory.*

@SuppressWarnings("LoopStatementThatDoesntLoop")
public abstract class CustomerBasePage {
    private final RemoteWebDriver driver;
    private final String expectedTitle;
    private final String url;
    private final String angularApp
    private final PageHelper helper;
    protected final Waiter waiter;
    protected final ShopComponentFactory componentFactory;

    public CustomerBasePage(angularApp) {
        this.angularApp = angularApp;
        driver = browser().driver();
        this.expectedTitle = "O2 |" + ("".equals("titleXXX") ? "" : " " + "titleXXX");
        this.url = ""; //url.endsWith("/") ? url : url + "/";
        helper = browser().currentPageHelper();
        waiter = new Waiter(driver);
        componentFactory = new ShopComponentFactory(driver);
        waitUntil(pageIsReady())
        if(angularApp){
            browser().executeJavaScript(injectAngularStateServiceIn(angularApp));
        }
        waitUntilPageIsReady();
        browser().executeJavaScript(disableJQueryAnimations());
    }

    public CustomerBasePage() {
        this(null);
    }

    protected final void waitUntilPageIsReady() {
        if(angularApp){
            browser().executeJavaScript(updateStateWhenAngularIsDone(angularApp))
        }
        waitUntil(conditionsForPageToBeReady());
    }

    protected final void waitUntil(PagePredicateBuilder predicateBuilder) {
        Predicate<WebDriver> predicate = predicateBuilder.build();
        waiter.waitUntil(predicate);
    }

    protected PagePredicateBuilder conditionsForPageToBeReady() {
        return pageIsReady()
                .whenBodyIsLoaded()
    }

    protected boolean isElementPresent(By locator) {
        try {
            helper().findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    protected void waitForSelector(By selector) {
        long TIMEOUT = 10000L;
        long endTime = System.currentTimeMillis() + TIMEOUT;
        while (System.currentTimeMillis() < endTime) {
            if (isElementPresent(selector)) {
                return;
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                break;
            }
        }
        Assert.fail("Timed out waiting for " + selector.toString());
    }

    protected final WebBrowser browser() {
        return new BrowserFactory().browser();
    }

    protected final PageHelper helper() {
        return helper
    }

    protected final JavascriptExecutor javascriptExecutor(){
        (JavascriptExecutor)driver
    }

    @Deprecated
    protected final List<WebElement> findElements(By by) {
        return helper().findElements(by);
    }

    @Deprecated
    protected final WebElement findElement(By by) {
        return helper().findElement(by);
    }

    @Deprecated
    protected final <T> T waitUntil(ExpectedCondition<T> condition) {
        return helper().waitUntil(condition)
    }

    protected String currentURL() {
        return driver.currentUrl
    }

    protected RemoteWebDriver getDriver() {
        return driver;
    }

    protected scrollToTop() {
        javascriptExecutor().executeScript("scroll(0, 0)")
    }

    protected boolean containsGivenJSLink(String expectedJSLink){
        return driver.findElements(By.tagName("script")).any{
            it -> it.getAttribute("src") == expectedJSLink
        };
    }
    protected boolean containsGivenCSSLink(String expectedCSSLink){
        return driver.findElements(By.tagName("link")).any{
            it -> it.getAttribute("href") == expectedCSSLink
        };
    }

    protected deleteCookies() {
        browser().cookies().deleteAll()
    }

    protected String findCookieValue(String key) {
        def map = browser().cookies().get("customer")
        new JsonSlurper().parseText(map.substring(1, map.length() - 1).replaceAll("\\\\", ""))[key]
    }
}
