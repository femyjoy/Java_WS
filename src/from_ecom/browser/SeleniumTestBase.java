package from_ecom.browser;

import PageObjects.helper.PageHelper;
import PageObjects.helper.Tags;
import framework.Configuration;
import framework.browser.BrowserFactory;
import framework.browser.WebBrowser;
import org.testng.annotations.Listeners;

@Listeners(SeleniumTestListener.class)
public class SeleniumTestBase {

    public static String hostUrl = Configuration.server.contains("ref") ? "http://" + Configuration.server : Configuration.hostUrl ;
    public static String shopContext = Configuration.server.contains("ref") ? "/shop/" : "/upgrade/store/";

    public static final long TIME_OUT = 2000;

    private final BrowserFactory factory = new BrowserFactory();

    protected void beforeTestClassAction() {

    }

    protected void beforeTestAction() {
        TestContext.cleanContext();
    }

    protected void afterTestAction() {
    }

    protected void afterTestClassAction() {
        factory.browser().close();
    }

    protected final PageHelper currentPage() {
        return factory.currentPage();
    }

    protected final void quitBrowser() {
       browser().quitBrowser();
    }

    protected final WebBrowser browser() {
        return factory.browser();
    }

    protected final Tags tags() {
        return currentPage().tags();
    }

    public static String last(String string){
        String[] tokens = string.split("/");
        return tokens[tokens.length-1];
    }
}
