package from_ecom.browser;

import PageObjects.WebdriverConcrete.*;
import PageObjects.WebdriverConcrete.NewUpgradeOptions.UpgradeOptionsPage;
import PageObjects.helper.PageHelper;
import framework.Configuration;
import framework.utils.TestData.TestUserDetails;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import static PageObjects.WebdriverConcrete.PageObjectFactory.CreateWebDriverPageObject;
import static framework.utils.ShopContext.BrowsingShop;
import static framework.utils.ShopContext.UpgradeShop;

public class WebBrowser {

    private final RemoteWebDriver driver;

    public WebBrowser(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void close() {
        // TODO FIXME Hugh : return browser to pool
        cookies().deleteAll();
    }

    public RemoteWebDriver driver() {
        return this.driver;
    }

    public void navigateTo(final String url) {
        driver.get(url);
    }

    public DeviceDetailsPage navigateToDetailsPageForFallBackUrl(final String url) {
        driver.get(url);
        return new DeviceDetailsPage(driver);
    }

    public void quitBrowser() {
        TestContext.quitBrowser();
    }

    public ShopPage goToShopHomePage() {
        return ShopPage.navigateToShopPage(driver());
    }

    public MyOffersPage goToMyOffersePageFor(String deviceType) {
        return MyOffersPage.navigateToMyOffersPageFor(driver(), deviceType);
    }

    public ShopPage goToShopHomePageForUser(TestUserDetails user) {
        return ShopPage.navigateToShopPageForUser(driver(),user);
    }

    public PayMonthlySimOnlyPage navigateToSimplicityPage() {
        ShopPage.navigateToSimplicityPage(driver());
        return new PayMonthlySimOnlyPage();
    }

    public ShopErrorPage goToShopErrorPage() {
        driver.get(Configuration.hostUrl + BrowsingShop.path + "phone/");
        return new ShopErrorPage(driver);
    }


    public DeviceDetailsPage navigateToDeviceDetailsPage(String brand, String model) {
        return DeviceDetailsPage.navigateTo(brand, model, false, driver());
    }

    public DeviceDetailsPage navigateToDeviceDetailsPageWithSubType(String brand, String model, String subType) {
        return DeviceDetailsPage.navigateToWithSubType(brand, model, subType, false, driver());
    }

    public DeviceDetailsPage navigateToDeviceDetailsPage(String brand, String model, boolean payG) {
        return DeviceDetailsPage.navigateTo(brand, model, payG, driver());
    }

    public DeviceDetailsPage deepLinkToDeviceDetailsPage(String deviceType, String brand, String model, String parm){
        return DeviceDetailsPage.deepLinkTo(deviceType,brand, model, parm, driver());
    }
    
    public DeviceListingsPage navigateToDeviceListingsPageFor(String deviceType) {
        return DeviceListingsPage.navigateTo(deviceType, driver());
    }

    public AccessoryHomePage navigateToAccessoryHomePage() {
        return AccessoryHomePage.navigateToAccessoryHomePage(driver());
    }

    public DeviceComparisonPage navigateToDeviceComparisonPageFor(String deviceType,String contractType, String devicesToCompare) {
        return DeviceComparisonPage.navigateToDeviceComparisonPage(deviceType,contractType, devicesToCompare, driver());
    }
    public DeviceListingsPage navigateToDeviceListingsBrandPageFor(String brand) {
        return DeviceListingsPage.navigateToBrandPage(brand, driver());
    }

    public DeviceListingsPage deepLinkNavigateToDeviceListingsPage(String relativeUrl) {
        return DeviceListingsPage.navigateToDeepLinkUrl(relativeUrl, driver());
    }

    public FreeSimPage navigateToFreeSimPage() {
        return FreeSimPage.navigateToFreeSimPage(driver());
    }

    public FreeSimPage navigateToFreeSimDeliveryPageWithoutPlanId() {
        return FreeSimDeliveryDetailsPage.navigateToFreeSimDeliveryPageWithoutPlanId(driver());
    }
    public FreeSimPage navigateToFreeSimCongratulationsPage() {
        return FreeSimDeliveryDetailsPage.navigateToFreeSimCongratulationsPage(driver());
    }

    public FreeSimPage navigateToFreeSimPageWithDeepLink(String deepLink) {
        return FreeSimDeliveryDetailsPage.navigateToFreeSimPageWithDeepLink(driver(), deepLink);
    }
    public UpgradeOptionsPage navigateToUpgradeOptionsPage() {
        driver.get(Configuration.hostUrl + UpgradeShop.path);
        return new UpgradeOptionsPage();
    }

    public DeviceComparisonPage compareDevicesFor(String deviceType,String contractType, String devicesToCompare){
        return DeviceListingsPage.compareDevices(deviceType,contractType,devicesToCompare,driver());
    }


    public void switchTo(String frameName) {
        driver.switchTo().frame(frameName);
    }

    public void switchTo(WebElement element) {
        driver.switchTo().frame(element);
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public void refresh() {
        driver.navigate().refresh();
    }

    @Deprecated
    public void browserBack() {
        driver.navigate().back();
    }

    public void takeBrowserScreenShot(String fileName) {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        try {
            FileUtils.copyFile(scrFile, new File(fileName + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takeWholeScreenShot(String fileName) {
        fileName = System.getProperty("user.dir") + "/seleniumscreenshots/" + fileName + ".jpg";
        try {
            Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage img = new Robot().createScreenCapture(rect);
            File output = new File(fileName);
            File parent = output.getParentFile();
            if(!parent.exists()) parent.mkdirs();
            ImageIO.write(img, "jpg", output);
            System.out.println("Saved screenshot to " + fileName);
        } catch (IOException|AWTException e) {
            e.printStackTrace();
        }
    }

    public void dumpPageSource(PrintStream outputStream) {
        outputStream.println(driver.getPageSource());
    }

    public PageHelper currentPageHelper() {
        return new PageHelper(driver);
    }

    public CookieHelper cookies() {
        return new CookieHelper(driver);
    }

    public Object executeJavaScript(String javaScript) {
        return driver.executeScript(javaScript);
    }

    public <T> T clickOnBackButton(Class<T> expectedPageToBeOn) {
        browserBack();
        return CreateWebDriverPageObject(expectedPageToBeOn);
    }

    public CustomerBasePage clickOnBackButton(CustomerBasePage page) {
        browserBack();
        scrollUp();
        return page;
    }

    private void scrollUp() {
        executeJavaScript("scroll(0, -250);");
    }
}
