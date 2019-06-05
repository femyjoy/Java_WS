package from_ecom.browser

import PageObjects.WebdriverConcrete.NewUpgradeOptions.DeviceTile
import PageObjects.WebdriverConcrete.Widgets.EShop.DeviceDetails.DeviceDetailsAttachments
import PageObjects.WebdriverConcrete.Widgets.EShop.DeviceDetails.FeaturesSection
import PageObjects.WebdriverConcrete.Widgets.EShop.DeviceDetails.Header.*
import PageObjects.WebdriverConcrete.Widgets.EShop.DeviceDetails.RecommendedTariffSection
import PageObjects.WebdriverConcrete.Widgets.EShop.InPageNavBar
import PageObjects.WebdriverConcrete.Widgets.EShop.O2RecycleSection
import PageObjects.helper.PagePredicateBuilder
import framework.Configuration
import framework.browser.TestContext
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import uk.co.o2.seleniumHtmlFramework.elements.Button
import uk.co.o2.seleniumHtmlFramework.elements.Link

import java.util.NoSuchElementException

import static framework.utils.ShopContext.BrowsingShop
import static org.openqa.selenium.By.*

public class DeviceDetailsPage extends CustomerBasePage {
    private final WebDriver driver
    private final O2RecycleSection o2RecycleSection

    public DeviceDetailsPage(WebDriver driver) {
        super("shopApp");
        this.driver = driver
        this.o2RecycleSection = new O2RecycleSection(helper().findElement(By.cssSelector("[data-qa-o2-recycle-section]")), driver)
        conditionsForPageToBeReady()
    }


    @Override
    protected PagePredicateBuilder conditionsForPageToBeReady() {
        return super.conditionsForPageToBeReady().whenBodyIsLoaded().whenAngularIsDone().upgradingMsisdnInfoIsLoaded().angularDigestCompleted()
    }

    O2RecycleSection getO2RecycleSection() {
        o2RecycleSection
    }


    String getPageTitle() {
        driver.getTitle()
    }

    public static DeviceDetailsPage navigateToWithSubType(String brand, String model,  String subType, boolean payG, WebDriver driver) {

        def url = "${Configuration.hostUrl}${BrowsingShop.path}$subType/${canonicalise(brand)}/${canonicalise(model)}/"
        if (payG) {
            url = url + "#contractType=payasyougo"
        }
        driver.get(url)
        driver.navigate().refresh()

        return new DeviceDetailsPage(driver);
    }

    public static DeviceDetailsPage navigateTo(String brand, String model, boolean payG, WebDriver driver) {

        return navigateToWithSubType(brand, model, "phones", payG, driver);
    }


    public static DeviceDetailsPage deepLinkTo(String deviceType, String brand, String model, String urlParams, WebDriver driver) {
        def url = "${Configuration.hostUrl}${BrowsingShop.path}${deviceType}/${canonicalise(brand)}/${canonicalise(model)}/#" + urlParams
        driver.get(url)
        driver.navigate().refresh()
        return new DeviceDetailsPage(driver)
    }

    // START Interaction methods
    public DeviceDetailsPage viewReviews() {
        ratings().viewReviews(); // FIXME TODO what to show here?
        return this;
    }

    public DeviceDetailsPage viewProductInfo() {
        ratings().viewProductInfo(); // FIXME TODO what to show here?
        return this;
    }

    public DeviceDetailsPage selectColourByName(String name) {
        variants().selectColourByName(name);
        waitUntilPageIsReady();
        return this;
    }

    public DeviceDetailsPage selectColourByIndex(int index) {
        variants().selectColourByIndex(index);
        waitUntilPageIsReady();
        return this;
    }

    public DeviceDetailsPage selectColourByIndexInFloatingBar(int index) {
        variants().selectColourByIndexFloatingBar(index);
        waitUntilPageIsReady();
        return this;
    }

    public DeviceDetailsPage selectCapacityByName(capacityName) {
        variants().selectCapacityByName(capacityName)
        waitUntilPageIsReady()
        return this;
    }


    public DeviceDetailsPage selectWifi() {
        variants().selectWifi();
        waitUntilPageIsReady();
        return this;
    }

    public DeviceDetailsPage select4G() {
        variants().select4G();
        waitUntilPageIsReady();
        return this;
    }

    public DeviceDetailsPage viewImageGallery() {
        media().viewImageGallery();
        return this;
    }

    public DeviceDetailsPage playVideo() {
        media().playVideo();
        return this;
    }

    public DeviceDetailsPage view360() {
        media().view360();
        return this;
    }

    public DeviceDetailsPage selectRecommendedTariff() {
        sectionHeaders().
            return this;
    }


    public TariffsAndExtrasPage viewTariffs() {
        Button button = continueToNextPageButton();
        button.click()
        return new TariffsAndExtrasPage()
    }

    public TariffsAndExtrasPage viewRecommendedTariff() {
        Button button = selectPhoneAndRecommendedTariff();
        button.click()
        return new TariffsAndExtrasPage()
    }

    public DeviceDetailsPage expandFeatures() {
        features().expand()
        return new DeviceDetailsPage(driver)
    }

    public DeviceDetailsPage collapseFeatures() {
        features().collapse()
        return new DeviceDetailsPage(driver)
    }

    public BasketPage addToBasket() {
        Button button = continueToNextPageButton();
        button.click()
        new BasketPage()
    }

    public ContinueOrAbandonCheckoutPage addToBasketExpectingContinueOrAbandonCheckoutPage() {
        Button button = continueToNextPageButton();
        button.click()
        new ContinueOrAbandonCheckoutPage()
    }

    public TariffsAndExtrasPage getTariffAndExtraPageWithAddToBasket() {
        Button button = continueToNextPageButton();
        button.click()
        new TariffsAndExtrasPage()
    }

    public UpgradeSignInPage clickSignIn() {
        Link link = upgradeSignIn();
        link.click();
        return new UpgradeSignInPage();
    }
    // END Interaction methods

    // TODO FIXME Hugh : this doesn't belong on page object, it is asserting therefore should be in the matcher
    public boolean verifyingTheDetailsOfTheSpecificationSectionBeforeExpandingAreVisible(String detailsOfTheSection) {
        return driver.findElement(cssSelector("[data-qa-specifications]")).getText().contains(detailsOfTheSection)
    }

    // TODO FIXME Hugh : this doesn't belong on page object, it is asserting therefore should be in the matcher
    public boolean verifyingTheSortingAfterExpanding(String[] detailsTitleExpected) {
        List<WebElement> allTitles = driver.findElements(cssSelector("[data-qa-specifications-details-title]"))
        int match = 0
        int i = 0
        for (WebElement we : allTitles) {
            if (we.getText().equals(detailsTitleExpected[i])) {
                match++
                i++
            } else
                throw new IllegalArgumentException(we.getText() + " is not equal to " + detailsTitleExpected[i])
        }
        if (match.equals(detailsTitleExpected.size()))
            return true
        else
            return false
    }

    // TODO FIXME Hugh : this doesn't belong on page object, it is asserting therefore should be in the matcher
    public boolean verifyingTheTechSpecDetailsExpandOnClick() {
        driver.findElement(cssSelector("[data-qa-tech-spec-details-title]")).click()
        return driver.findElement(cssSelector("[data-qa-tech-spec-details-content]")).isDisplayed()
    }

    // TODO FIXME Hugh : this doesn't belong on page object, it is asserting therefore should be in the matcher
    public verifyingTheTechSpecExpandOnClickInMobile(String detailsOfTheSection) {
        driver.findElement(cssSelector("[data-qa-specifications]")).click()
        return VerifyingTheDetailsOfTheSpecificationSectionBeforeExpandingAreVisible(detailsOfTheSection)
    }

    // TODO FIXME Hugh : this doesn't belong on page object, it is asserting therefore should be in the matcher
    public boolean withExpectedNoOfSectionHeaders() {
        return driver.findElements(cssSelector("[data-qa-details-section-header]")).size().equals(driver.findElements(cssSelector("[data-qa-section-header]")).size())
    }

    // START Page elements
    InPageNavBar getNavBar() {
        return new InPageNavBar(driver.findElement(cssSelector("[data-qa-navbar]")));
    }

    protected RatingsSection ratings() {
        WebElement ratings = driver.findElement(cssSelector("[data-qa-ratings-section]"));
        return new RatingsSection(ratings);
    }

    protected CostSection costs() {
        WebElement costs = driver.findElement(cssSelector("[data-qa-costs-section]"));
        return new CostSection(costs);
    }

    protected VariantSection variants() {
        WebElement variants = driver.findElement(cssSelector("[data-qa-variants-section]"));
        return new VariantSection(variants);
    }

    public DeviceDetailsPage selectFloatingBarColourByIndex(int index) {
        Thread.sleep(5)
        driver.findElements(cssSelector("[data-qa-floatingbar-colour]")).get(1).click()
        driver.findElements(cssSelector(".selectboxit-options.selectboxit-list")).get(1).findElements(cssSelector(".selectboxit-option")).get(index).click()
        return this
    }

    public DeviceDetailsPage selectFloatingBarCapacityByIndex(int index) {
        Thread.sleep(5)
        driver.findElements(cssSelector("[data-qa-floatingbar-capacity]")).get(1).click()
        driver.findElements(cssSelector(".selectboxit-options.selectboxit-list")).get(2).findElements(cssSelector(".selectboxit-option")).get(index).click()
        return this
    }

    protected SectionHeaders sectionHeaders(){
        WebElement sectionHeaders = driver.findElement(cssSelector("[data-qa-device-details]"))
        return new SectionHeaders(sectionHeaders)
    }

    public DeviceDetailsPage clickRecommendedTariff() {
        driver.findElements(cssSelector("[data-qa-section-header]")).get(4).findElement(cssSelector(" p a")).click()
        waitUntilPageIsReady();
        return this
    }

    public DeviceDetailsPage clickToTop() {
        driver.findElement(cssSelector(".go-to-top p a")).click();
        waitUntilPageIsReady();
        return this;
    }


    public DeviceDetailsPage clickToOverviewLink()
    {
        driver.findElements(cssSelector("[data-qa-section-header]")).get(0).findElement(cssSelector(" p a")).click()
        waitUntilPageIsReady();
        return this;

    }

    public DeviceDetailsPage clickToTechnicalSpecificationLink()
    {
        driver.findElements(cssSelector("[data-qa-section-header]")).get(1).findElement(cssSelector(" p a")).click()
        waitUntilPageIsReady();
        return this;

    }


    public DeviceDetailsPage clickToReviewsLink()
    {
        driver.findElements(cssSelector("[data-qa-section-header]")).get(2).findElement(cssSelector(" p a")).click()
        waitUntilPageIsReady();
        return this;

    }

    public DeviceDetailsPage clickToWhyO2Link()
    {
        driver.findElements(cssSelector("[data-qa-section-header]")).get(3).findElement(cssSelector(" p a")).click()
        waitUntilPageIsReady();
        return this;

    }


    public MediaSection media() {
        WebElement media = driver.findElement(cssSelector("[data-qa-media-section]"));
        return new MediaSection(media);
    }

    protected static DeviceDetailsAttachments attachments() {
        return new DeviceDetailsAttachments(null); // FIXME TODO implement;
    }

    public FeaturesSection features() {
        return new FeaturesSection(driver.findElement(cssSelector("[data-qa-features]")), componentFactory);
    }

    public RecommendedTariffSection recommendedTariff() {
        return new RecommendedTariffSection(driver.findElement(cssSelector("[data-qa-recommended-tariff]")), componentFactory)
    }

    public String getDeviceName() {
        return driver.findElement(cssSelector("[data-qa-device-title]")).text;
    }

    public StockStatusSection stockStatus() {
        WebElement stockStatus = driver.findElement(cssSelector("[data-qa-stock-section]"));
        return new StockStatusSection(stockStatus);
    }
    public boolean isOverwrittenOverviewSectionDisplayed() {
        return driver.findElement(cssSelector("[data-qa-overwritten-overview]")).isDisplayed()
    }

    public SocialMediaSection socialMedia() {

        WebElement socialmedia = driver.findElement(cssSelector("[data-qa-social-media-section]"));
        return new SocialMediaSection(socialmedia);
    }

    protected boolean canSignIn() {
        return driver.findElements(cssSelector("[data-qa-upgrade-sign-in]")).size() > 0;
    }

    protected Button continueToNextPageButton() {
        return componentFactory.createButtonAllowMissing(cssSelector("[data-qa-view-tariffs]"))
    }

    protected Button selectPhoneAndRecommendedTariff() {
        return componentFactory.createButton(cssSelector("[data-qa-add-phone-to-basket]"))
    }

    // TODO FIXME Hugh : use ButtonMatcher
    protected String getTextInCallToAction() {
        return driver.findElement(cssSelector("[data-qa-view-tariffs]")).text
    }

    protected Link upgradeSignIn() {
        return componentFactory.createLink(cssSelector("[data-qa-upgrade-sign-in]"))
    }

    protected Link facebook() {
        return componentFactory.createLink(cssSelector("[data-qa-Link-Facebook]"))
    }

    protected String getCanonicalLinkSuffix() {
        return driver.findElement(cssSelector("head link[rel='canonical']")).getAttribute("href")
                .replace(Configuration.hostUrl, "")
                .replaceAll("(shop\\/|upgrade\\/store\\/|upgrade\\/)", "")
                .replaceAll("(#.*|\\?.*)", "")
    }

    private static canonicalise(value) {
        value.toLowerCase().replace(" ", "-")
    }
    // END Page elements

    // START Matcher methods

    protected boolean urlContains(String url) {
        driver.currentUrl.contains(url)
    }

    protected boolean hasNoVariantSelectors() {
        try {
            variants()
            false
        } catch (org.openqa.selenium.NoSuchElementException ignore) {
            true
        }
    }

    protected boolean hasNoFeatures() {
        try{
           features()
           false
        } catch (org.openqa.selenium.NoSuchElementException ignored){
            true
        }

    }

    protected boolean hasNoPricingData() {
        try {
            costs()
            false
        } catch (org.openqa.selenium.NoSuchElementException ignore) {
            true
        }
    }

    protected boolean hasNoRecommendedTariff() {
        try {
            recommendedTariff()
            false
        } catch (org.openqa.selenium.NoSuchElementException ignore) {
            true
        }
    }

    protected boolean hasNoLongerDeviceAvailableText() {
        String noLongerText = driver.findElement(cssSelector("[data-qa-EndOfLife-text]")).getText();
        if (noLongerText == "No longer available")
            return true;
        else
            throw new NoSuchElementException(noLongerText + " is not displayed for this device")

    }

    protected boolean hasGetUpdateButton() {
        String buttonText = driver.findElement(cssSelector("[data-qa-ComingSoon-Button-GetUpdate]")).getText();
        if (buttonText == "Get update")
            return true;
        else
            throw new NoSuchElementException(buttonText + " is not displayed for this device")

    }


    protected boolean ClickGetUpdateButton() {
        String winHandleBefore = driver.getWindowHandle();

        driver.findElement(cssSelector("[data-qa-ComingSoon-Button-GetUpdate]")).click();

        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }

        if (driver.getCurrentUrl().equals("https://www.o2.co.uk/getupdates")) {
            return true;
            driver.close();
        } else {
            throw new NoSuchElementException(driver.getTitle() + " is not displayed on the Page")
        }
        driver.switchTo().window(winHandleBefore);
    }


    protected boolean hasGetUpdateLink() {
        String linkText = driver.findElement(cssSelector("[data-qa-ComingSoon-Link-GetUpdates]")).getText();
        if (linkText == "Get Updates")
            return true;
        else
            throw new NoSuchElementException(linkText + " is not displayed for this device")

    }


    protected boolean ClickGetUpdateLink() {
        String winHandleBefore = driver.getWindowHandle();

        driver.findElement(linkText("Get Updates")).click()

        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }

        if (driver.getCurrentUrl().equals("https://www.o2.co.uk/getupdates")) {
            return true;
            driver.close();
        } else {
            throw new NoSuchElementException(driver.getTitle() + " is not displayed on the Page")
        }
        driver.switchTo().window(winHandleBefore);
    }


    protected boolean hasNoCallToAction() {
        !continueToNextPageButton().isDisplayed()
    }

    public boolean noDetailFeatures() {

        try{
            driver.findElement(cssSelector("[data-qa-features-details-title]"))
            return false
        } catch (org.openqa.selenium.NoSuchElementException ignored){
            return true
        }
    }

    public boolean isWhyO2SectionDisplayed() {
        return driver.findElement(cssSelector("[data-qa-why-o2-section]")).isDisplayed()
    }

    public boolean isWhyO2SectionNotDisplayed() {
        return driver.findElements(cssSelector("[data-qa-why-o2-section]")).size() == 0
    }

    public boolean isCaveatSectionDisplayed() {
        return driver.findElement(cssSelector("[data-qa-caveat-section]")).isDisplayed()
    }

    public boolean isCaveatSectionNotDisplayed() {
        return driver.findElements(cssSelector("[data-qa-caveat-section]")).size() == 0
    }

    public boolean isRelatedProductsSectionDisplayed() {
        return driver.findElement(cssSelector("[qa-related-devices-section]")).isDisplayed()
    }

    public boolean isRelatedProductsSectionNotDisplayed() {
        return driver.findElements(cssSelector("[qa-related-devices-section]")).size() == 0
    }

    public int noOfRelatedProducts() {
        return driver.findElements(By.id("qa-relatedDevicesTile")).size()
    }

    public DeviceTile findRelatedProduct(int index) {
        def tiles = driver.findElements(By.id("qa-relatedDevicesTile"))
        if (tiles.size() < index + 1) {
            throw new RuntimeException("could not find tile at $index , total displayed tiles size:" + tiles.size());
        }
        WebElement indexedRelaterProducts = tiles.get(index)
        new DeviceTile(indexedRelaterProducts)
    }


    public DeviceDetailsPage selectModel( String model){
        o2RecycleSection.selectModel(model)
        this
    }
    public DeviceDetailsPage selectMake( String make){
        o2RecycleSection.selectMake(make)
        this
    }
    public boolean validatePriceTag(String priceTag){
        new WebDriverWait(TestContext.getDriver(), 120).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-qa-recycle-price]")));
        return  priceTag.equalsIgnoreCase(driver.findElement(cssSelector("[data-qa-recycle-price]")).text)

    }

    public int noOfImagesDisplayedInO2Section() {
        return driver.findElements(cssSelector("[data-qa-why-o2-section-images]")).size()
    }

    public WebElement[] actualTitles(){
        return driver.findElements(cssSelector("[data-qa-why-o2-section-titles]"))
    }

    public Link whyO2SectionLinks(int indexOfLink){
        return componentFactory.createLinkAt(indexOfLink ,cssSelector("[data-qa-why-o2-section-links]"))
    }

    public boolean isReviewsSectionDisplayed(){
        return driver.findElement(cssSelector("[data-qa-details-section-reviews]")).isDisplayed()
    }

    public DeviceDetailsPage waitForToggle(){
        WebElement element = driver.findElement(cssSelector("[data-qa-features-details-title]"))
        WebDriverWait wait = new WebDriverWait(driver, 20)
        wait.until(ExpectedConditions.textToBePresentInElement(element, "See features"))
        return this
    }


    public boolean hasProductInfoLink() {
        String linkText = driver.findElement(cssSelector("[data-qa-link-productInformation]")).getText();
        if (linkText == "Product information")
            return true;
        else
            throw new NoSuchElementException(" Product information link is not displayed for this device")
    }

    public DeviceDetailsPage ClickOnProductInfoLink() {
        driver.findElement(cssSelector("[data-qa-link-productInformation]")).click()
        return this
    }

    public boolean isFacebookLinkDisplayed() {
        return driver.findElement(cssSelector("[data-qa-Link-Facebook]")).isDisplayed()
    }


    public boolean isTwitterLinkDisplayed() {
        return driver.findElement(cssSelector("[data-qa-Link-Twitter]")).isDisplayed()
    }

    public boolean isGoogleLinkDisplayed() {
        return driver.findElement(cssSelector("[data-qa-Link-Google]")).isDisplayed()
    }

    public void closeImageGallery() {
        driver.findElement(cssSelector("[data-qa-media-gallery-close]")).click()
    }

    public int getNumberOfGalleryImages() {
        return driver.findElements(cssSelector("[data-qa-media-gallery-images]")).size()
    }

    public DeviceListingsPage back() {
        new DeviceListingsPage(driver.navigate().back())
    }

    public boolean isCnCLinkDisplayed() {
        try {
            return driver.findElement(By.className("cnc-overlay")).displayed
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isAlsoAvailableOnPayNGoMessageDisplayed() {
        try {
            return driver.findElement(By.className("price-post")).isDisplayed()
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public DeviceDetailsPage openCheckStockInStoreOverlay() {
        driver.findElement(id("checkStoreStock")).click()
        this
    }

    def closeCheckStockInStoreOverlay() {
        driver.findElement(By.className("boxclose")).click()
        scrollToTop()
        this
    }

    public String clickSeeWhenYouCanCollectLink(int index){
        WebDriverWait waiter = new WebDriverWait(driver, 5);
        WebElement storeTileWrapper = driver.findElement(id("storeTilesWrapper"));

        waiter.until( ExpectedConditions.presenceOfElementLocated(By.className("tile")));
        List<WebElement> storeElements = storeTileWrapper.findElements(By.className("tile"))
        WebElement store = storeElements.get(index)
        store.findElement(By.className("collectionDateAnchor")).click()
        WebElement collectionDateElement = store.findElement(By.className("collectionDate"))

        waiter.until(ExpectedConditions.textToBePresentInElement(collectionDateElement, "Collect from:"));
        collectionDateElement.text
    }

    public String getCheckStockInStoreLinkText() {
        driver.findElement(id("checkStoreStock")).text
    }

    public String getSearchTerm() {
        driver.findElement(id("storePostcode")).getAttribute("value");;
    }

    public String getCollectionInfo() {
        WebDriverWait waiter = new WebDriverWait(driver, 10);
        waiter.until( ExpectedConditions.presenceOfElementLocated(cssSelector(".collection-info")));
        return driver.findElement(cssSelector(".collection-info")).getText()
    }

    public String hasNoCollectionInfo() {
        return driver.findElements(cssSelector(".collection-info")).size() == 0
    }

    public boolean isCollectionInfoContains(String info) {
        WebDriverWait waiter = new WebDriverWait(driver, 5);
        waiter.until( ExpectedConditions.presenceOfElementLocated(cssSelector(".collection-info")));
        WebElement collectionInfoElement = driver.findElement(cssSelector(".collection-info"));
        waiter.until(ExpectedConditions.textToBePresentInElement(collectionInfoElement, info));
        collectionInfoElement.text.contains(info)
    }

    public DeviceDetailsPage searchStoresIn(String searchTerm) {
        driver.findElement(id("storePostcode")).clear();
        driver.findElement(id("storePostcode")).sendKeys(searchTerm);
        driver.findElement(id("store-postcode-submit")).click();
        Thread.sleep(1000)
        this
    }

    public DeviceDetailsPage selectStoreByIndex(int index) {
        WebDriverWait waiter = new WebDriverWait(driver, 5);
        waiter.until( ExpectedConditions.presenceOfElementLocated(id("storeTilesWrapper")) );

        WebElement storeTileWrapper = driver.findElement(id("storeTilesWrapper"));
        waiter.until( ExpectedConditions.presenceOfElementLocated(By.className("tile")));
        List<WebElement> storeElements = storeTileWrapper.findElements(By.className("tile"))
        storeElements.get(index).findElement(By.className("selectStore")).click()
        this
    }

    //commenting earlier method as 'update' button is not required now.
    public DeviceDetailsPage waitUntilStoreIsSelected(){
        Thread.sleep(5000);
        this
    }

    public boolean isStoreAvailableForClickAndCollectNow(WebElement storeElement) {
        String collectionDate = storeElement.findElement(By.className("collectionDate")).text
        ['Collect: TODAY', 'Collect: TOMORROW', 'Collect: NEXT OPENING DAY'].contains(collectionDate)
    }

    public List<WebElement> getStoreTileElements() {
        WebDriverWait waiter = new WebDriverWait(driver, 5);
        //waiter.until( ExpectedConditions.presenceOfElementLocated(By.id("storeTilesWrapper")) );

        WebElement storeTileWrapper = driver.findElement(id("storeTilesWrapper"));
        waiter.until(ExpectedConditions.presenceOfElementLocated(By.className("tile")));
        List<WebElement> storeElements = storeTileWrapper.findElements(By.className("tile"))
        storeElements
    }

    public boolean isStoreAvailableForClickAndCollect(WebElement storeElement) {
        storeElement.findElement(By.className("collectionDateAnchor")).isDisplayed()
    }

    public boolean isStoreAvailableForPreOrderClickAndCollect(WebElement storElement) {
        String collectionDate = storElement.findElement(By.className("collectionDate")).text
        collectionDate.equalsIgnoreCase('Delivery on 14th February')
    }

    public boolean isStoreAvailableForDelayedDeliveryClickAndCollect(WebElement storeElement) {
        String collectionDate = storeElement.findElement(By.className("collectionDate")).text
        collectionDate.equalsIgnoreCase('Collect in: Up to 3 weeks')
    }

    public boolean isStoreNotEligibleForCollection(WebElement storeElement) {
        String collectionDate = storeElement.findElement(By.className("collectionDate")).text
        collectionDate.equalsIgnoreCase('Not available for collection')
    }

    public boolean isBackToPhonesLinkExists() {
        try {
            WebElement link = driver.findElement(By.linkText("Back to phones"))
            return link.isDisplayed() && link.getAttribute("href").contains("/upgrade/phones")
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isBackToTabletsLinkExists() {
        try {
            WebElement link = driver.findElement(By.linkText("Back to tablets"))

            return link.isDisplayed() && link.getAttribute("href").contains("/upgrade/tablets")
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isBackToSmartwatchesLinkExists() {
        try {
            WebElement link = driver.findElement(By.linkText("Back to smartwatches"))

            return link.isDisplayed() && link.getAttribute("href").contains("/upgrade/smartwatches")
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isBackToMobileBroadbandLinkExists() {
        try {
            WebElement link = driver.findElement(By.linkText("Back to mobile broadband"))

            return link.isDisplayed() && link.getAttribute("href").contains("/upgrade/mobile-broadband")
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isUpgradingMsisdnDisplayed(String msisdn) {
        try {
            return driver.findElement(By.xpath("//div[@class='upgrading-msisdn']/div/p")).text.contains("Upgrading ${msisdn}")
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    def getSelectedStoreId() {
        WebDriverWait waiter = new WebDriverWait(driver, 5);
        waiter.until( ExpectedConditions.presenceOfElementLocated(By.className("selectedStore")));
        return driver.findElement(cssSelector(".selectedStore > div")).getAttribute("id")
    }

    def hasNoSelectedStore() {
        WebDriverWait waiter = new WebDriverWait(driver, 5,50);
        waiter.until( ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".selectedStore")));
        return driver.findElements(cssSelector(".selectedStore")).size() == 0
    }

    boolean getIsCNCNowMessageVisible() {
        try{
            WebDriverWait waiter = new WebDriverWait(driver, 1);
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.className("cncnowCheckoutConfirmMessage")));
            return driver.findElement(By.className("cncnowCheckoutConfirmMessage")).isDisplayed();
        }catch (TimeoutException e){
            return false;
        }
    }

    String getFindStoreErrorMessage() {
        WebDriverWait waiter = new WebDriverWait(driver, 5)
        waiter.until( ExpectedConditions.presenceOfElementLocated(id("storePostcode-error")))
        return driver.findElement(id("storePostcode-error")).text
    }


    def goToBasket() {
        waitForAddToBasketToolTip()
        driver.findElement(By.cssSelector("#basketToolTip [data-qa-gotobasket-link]")).click();
        new BasketPage()
    }

    def goToBasketExpectingCheckoutInProgressPage() {
        waitForAddToBasketToolTip()
        driver.findElement(By.cssSelector("#basketToolTip [data-qa-gotobasket-link]")).click();
        new ContinueOrAbandonCheckoutPage()
    }

    private void waitForAddToBasketToolTip() {
        WebDriverWait waiter = new WebDriverWait(driver, 5)
        waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#basketToolTip [data-qa-gotobasket-link]")))
    }

    DeviceDetailsPage addAccessoryToBasket() {
        Button button = continueToNextPageButton();
        button.click()
        this
    }

    DeviceDetailsPage addWearableToBasket() {
        Button button = continueToNextPageButton();
        button.click()
        this
    }

    def DeviceDetailsPage waitForAddToBasketButtonTobeEnabled() {
        WebDriverWait wait = new WebDriverWait(driver, 5)
        WebElement element = driver.findElement(cssSelector("[data-qa-view-tariffs]"))
        wait.until(ExpectedConditions.elementToBeClickable (element))
        return this
    }

    def clearCookies(){
        driver.manage().deleteAllCookies();
    }

    def boolean errorTooltipVisible() {
        try{
            WebDriverWait waiter = new WebDriverWait(driver, 5)
            waitForAddToBasketToolTip()
            waiter.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-qa-error-message]")))
            true
        } catch (org.openqa.selenium.NoSuchElementException ignore) {
            false
        }

    }

    def String getToolTipMessage() {
        return driver.findElement(By.cssSelector("[data-qa-tooltip-content]")).getText()

    }

    public DeviceDetailsPage selectSize(String size) {
        variants().selectSizeByName(size);
        waitUntilPageIsReady();
        return this;
    }

    public boolean hasGoToBasketLink() {
        String linkText = driver.findElement(cssSelector("#basketToolTip [data-qa-gotobasket-link]")).getText();
        if (linkText == "Go to basket")
            return true;
        else
            throw new NoSuchElementException(" Go To Basket link is not displayed on Tooltip")
    }

    public boolean isBasketIconOnSubNavigationBarExist() {
        try {
            return driver.findElement(By.cssSelector("[data-qa-sub-nav-basket-icon]")).displayed;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public BasketPage clickOnBasketIconOnSubNavigationBar() {
        waitUntilPageIsReady()
        driver.findElement(By.cssSelector("[data-qa-sub-nav-basket-icon]")).click()
        return new BasketPage()
    }

    List availableCapacities() {
        driver.findElements(By.cssSelector("[data-qa-dimension-memory]")).collect {
            it -> it.text.trim()
        }
    }

    List availableColors() {
        driver.findElements(By.cssSelector("[data-qa-dimension-colour]")).collect {
            it -> it.text.trim()
        }
    }

    List availableSizes() {
        driver.findElements(By.cssSelector("[data-qa-dimension-size]")).collect {
            it -> it.text.trim()
        }
    }
    public boolean isAccessoryFloatingBarDisplayed() {
        try {
            return driver.findElement(By.className("docked-accessory-options-container")).isDisplayed() && driver.findElement(By.className("docked")).isDisplayed()
        }
        catch(org.openqa.selenium.NoSuchElementException e) {
            return false
        }
    }

    public boolean isDeviceFloatingBarDisplayed() {
        try {
            return driver.findElement(By.className("docked")).isDisplayed()
        }
        catch(org.openqa.selenium.NoSuchElementException e) {
            return false
        }
    }

    public TariffsAndExtrasPage viewTariffsByAvailability() {
        findElement(By.cssSelector(".price-post >a")).click()
        return new TariffsAndExtrasPage()
    }

    def waitUntilPageIsInDockMode(DeviceDetailsPage detailsPage) {
        detailsPage.waiter.waitUntil(PagePredicateBuilder.newCondition()
                .detailsPageDockerLoaded()
                .build())
    }

    public static String returnBaseURL() {
        return Configuration.hostUrl
    }

    DeviceListingsPage clickOnBackToDeviceLinkFor(String subType) {
        driver.findElement(By.linkText("Back to ${subType}")).click()
        return new DeviceListingsPage(driver)
    }

    protected String devicePromotionRibbonText() {
        driver.findElement(cssSelector("[data-qa-promotion-ribbon]")).getText();
    }

    protected boolean isPromotionRibbonExists() {
        try {
            driver.findElement(cssSelector("[data-qa-promotion-ribbon]"))
            true
        } catch (Exception e) {
            false
        }
    }

    def boolean isCountdownTimerDisplayed() {
        return driver.findElements(By.className("page-count-down-promo"))
    }

    String getCountdownTimerPromoMessage() {
        return helper().findElement(By.cssSelector(".message.headTxt")).text
    }

    String getDeliveryMessage(){
        helper().findElement(By.cssSelector(".status-message")).text
    }
}
