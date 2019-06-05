package from_ecom.browser;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static java.lang.System.currentTimeMillis;

public class Browser {
    private final RemoteWebDriver driver;
    private final BrowserContext context;
    private final CookieHelper cookieHelper;

    public Browser(RemoteWebDriver driver, BrowserContext context) {
        this.driver = driver;
        this.context = context;
        this.cookieHelper = new CookieHelper(driver);
    }

    public void navigateTo(String url) {
        driver.get(context.buildFullUrl(url));
    }

    public void reset() {
        cookieHelper.deleteAll();
    }

    public void close() {
        // FIXME return browser to browser pool
        driver.close();
    }

    public void takeScreenshot(String description) {
        // FIXME create a directory for all screenshots per session, rather than one global dumping ground for all sessions
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        try {
            String filename = description.replaceAll(" ", "_") + "-" + currentTimeMillis() + ".png";
            FileUtils.copyFile(screenshot, new File(context.screenshotPath() + filename));
        } catch (IOException e) {
        }
    }

    public void dumpPageSource(PrintStream outputStream) {
        outputStream.println(driver.getPageSource());
    }

}
