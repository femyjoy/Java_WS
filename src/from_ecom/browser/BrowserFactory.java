package from_ecom.browser;

import PageObjects.helper.PageHelper;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class BrowserFactory {

    public static WebBrowser create() {
        if (TestContext.getBrowser() != null) {
            return TestContext.getBrowser();
        }

        if (TestContext.isMobileJourney()) {
            DesiredCapabilities capabillities = DesiredCapabilities.firefox();
            capabillities.setCapability("platform", Platform.LINUX);
            capabillities.setJavascriptEnabled(true);
            capabillities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("general.useragent.override", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us) AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0 Mobile/7A341 Safari/528.16");
            capabillities.setCapability("firefox_profile", profile);
            TestContext.setDriver(new FirefoxDriver(capabillities));
            // mobile screen size
            TestContext.getDriver().manage().window().setSize(new Dimension(320, 800));
        } else {
            DesiredCapabilities capabillities = DesiredCapabilities.firefox();
            capabillities.setCapability("version", TestContext.getBrowserVersion());
            capabillities.setCapability("platform", Platform.LINUX);
            capabillities.setJavascriptEnabled(true);
            capabillities.setCapaobility(CapabilityType.ACCEPT_SSL_CERTS, true);
            TestContext.setDriver(new FirefoxDriver(capabillities));
            TestContext.getDriver().manage().window().maximize();
        }
        return TestContext.getBrowser();
    }

    public PageHelper currentPage() {
        return new PageHelper(TestContext.getDriver());
    }

    public WebBrowser browser() {
        return create();
    }
}
