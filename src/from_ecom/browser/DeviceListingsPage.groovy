package from_ecom.browser

import PageObjects.WebdriverConcrete.NewUpgradeOptions.UpgradeOptionsPage
import PageObjects.WebdriverConcrete.Widgets.EShop.DeviceListings.*
import PageObjects.WebdriverConcrete.Widgets.EShop.DeviceSearchSection
import PageObjects.helper.PagePredicateBuilder
import framework.Configuration
import framework.utils.ShopContext
import org.openqa.selenium.By
import org.openqa.selenium.ElementNotVisibleException
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import uk.co.o2.seleniumHtmlFramework.elements.InputButton
import uk.co.o2.seleniumHtmlFramework.elements.Link
import uk.co.o2.seleniumHtmlFramework.elements.TextField
import org.openqa.selenium.NoSuchElementException

import static framework.utils.ShopContext.BrowsingShop
import static org.openqa.selenium.By.cssSelector
import static PageObjects.helper.PagePredicateBuilder.pageIsReady
public class DeviceListingsPage extends CustomerBasePage {
    private final WebDriver driver

    private DeviceSearchSection deviceSearchSection

    public DeviceListingsPage(WebDriver driver) {
        super("shopApp");
        this.driver = driver
        conditionsForPageToBeReady()
    }

    protected PagePredicateBuilder conditionsForPageToBeReady() {
        return super.conditionsForPageToBeReady()
            .whenAngularIsDone()
            .upgradingMsisdnInfoIsLoaded()
            .whenElementIsPresent(cssSelector("h1[data-qa-page-heading]"))
//            .angularDigestCompleted()
    }

    public static DeviceListingsPage navigateTo(String deviceType, WebDriver driver) {
        driver.get(Configuration.hostUrl + BrowsingShop.path + "${canonicalise(deviceType)}/" + (deviceType.equalsIgnoreCase("accessories") ? "all" : ""))
        return new DeviceListingsPage(driver)
    }

    public static DeviceListingsPage navigateToDeepLinkUrl(String relativeUrl, WebDriver driver) {
        driver.get(Configuration.hostUrl + BrowsingShop.path + "$relativeUrl")
        return new DeviceListingsPage(driver)
    }

    public static DeviceListingsPage navigateToBrandPage(String brand, WebDriver driver) {
        driver.get(Configuration.hostUrl + BrowsingShop.path + "brand/" + brand)
        return new DeviceListingsPage(driver)
    }

    // START Interaction methods
    public DeviceListingsPage searchFor(String criteria) {
        searchField().type(criteria)
        searchButton().click()
        return new DeviceListingsPage(driver)
    }

    // START Interaction methods
    public DeviceSearchSection typeCompatibleDeviceCriteria(String searchKeyword) {
        def compatibleDeviceFilterText = compatibleDeviceSearchField()
        compatibleDeviceFilterText.type(searchKeyword)
        if (!deviceSearchSection) {
            deviceSearchSection = new DeviceSearchSection(helper())
        }
        deviceSearchSection
    }

    public DeviceListingsPage sendKeysToCompatibleDeviceCriteria(Keys keys) {
        def compatibleDeviceFilterText = compatibleDeviceSearchField()
        compatibleDeviceFilterText.sendKeys(keys)
        this
    }

    public DeviceListingsPage viewPayMonthly() {
        payMonthlyTab().click()
        return new DeviceListingsPage(driver)
    }

    public DeviceListingsPage viewPayAsYouGo() {
        payAsYouGoTab().click()
        return new DeviceListingsPage(driver)
    }

    public DeviceListingsPage viewNonConnected() {
        deviceOnlyTab().click()
        return new DeviceListingsPage(driver)
    }

    public static DeviceComparisonPage compareDevices(String deviceType,String contractType, String devicesToCompare, WebDriver driver){
        driver.findElement(By.cssSelector("[data-qa-compare-devices]")).click()
        driver.get(Configuration.hostUrl + BrowsingShop.path + "compare/${deviceType}?contractType=${contractType}&devices="+"${devicesToCompare}")
        return new DeviceComparisonPage(driver)
    }
    public DeviceComparisonPage compareDevices(){
        helper().findElement(By.cssSelector("[data-qa-compare-devices]")).click()
        return new DeviceComparisonPage()
    }
    public DeviceListingsPage viewDeviceSubType(String subType) {
        clearFilterPanel()
        phoneFilter().selectSubType(subType)
        return new DeviceListingsPage(driver)
    }

    public DeviceListingsPage viewMultipleDeviceSubType(String subType1, String subType2) {
        clearFilterPanel()
        phoneFilter().selectMultipleSubTypes(subType1, subType2)
        return new DeviceListingsPage(driver)
    }
    public DeviceListingsPage filterByWithoutReset(DeviceFilter.Builder builder) {
        DeviceFilter filter = builder.build()
        phoneFilter().filterByWithoutReset(filter)
        waitUntilPageIsReady();
        return this
    }

    public DeviceListingsPage filterBy(DeviceFilter.Builder builder) {
        DeviceFilter filter = builder.build()
        phoneFilter().filterBy(filter)
        waitUntilPageIsReady();
        return this
    }

    public DeviceListingsPage clearFilterPanel() {
        phoneFilter().clearFilters()
        waitUntilPageIsReady()
        return this
    }

    public DeviceListingsPage openFilterPanel() {
        phoneFilter().openFilterPanel()
        waitUntilPageIsReady()
        return this
    }

    public DeviceListingsPage waitUntilPageBodyLoaded() {
        pageIsReady().whenBodyIsLoaded()
        return this;
    }

    public DeviceListingsPage closeFilterPanel() {
        phoneFilter().closeFilterPanel()
        return this
    }

    public DeviceListingsPage openSortPanel() {
        sortByHeader().openFilterPanel()
    }

    public DeviceListingsPage closeSortPanel() {
        sortByHeader().closeFilterPanel()
        return this
    }

    public DeviceListingsPage sortBy(Sorting.Builder builder) {
        sortByHeader().openFilterPanel()
        sortByOptions().sortBy(builder.build())
        waitUntilPageIsReady();
        return this
    }

    public DeviceListingsPage previousPage() {
        pagination().previous()
        waitUntilPageIsReady();
        return this
    }

    public DeviceListingsPage nextPage() {
        pagination().next()
        waitUntilPageIsReady();
        return this
    }

    public DeviceListingsPage showAll() {
        try {
            showAllPagination().viewAll()
            waitUntilPageIsReady();
        } catch (ElementNotVisibleException ignore) {
        }
        return this
    }

    public String returnCurrentURL() {
        return driver.getCurrentUrl()
    }

    public static String returnBaseURL() {
        return Configuration.hostUrl
    }

    private static canonicalise(value) {
        value?.toLowerCase()?.replace(" ", "-")
    }

    public clickNonLinkElement() {
        driver.findElement(By.cssSelector(".shop")).click()
    }

    public clickOnPromotionalTile() {
        showAll()
        driver.findElement(By.cssSelector(".banner-tile")).click()
    }

    public showAllTiles() {
        showAll()
    }

    // END Interaction methods

    // START Page elements
    protected TextField searchField() {
        return componentFactory.createTextField(By.cssSelector("input.ng-valid"))
    }

    protected TextField compatibleDeviceSearchField() {
        componentFactory.createTextField(By.id("compatibleDeviceSearch"))
    }



    public PhoneFilterSection filters() {
        return new PhoneFilterSection(driver.findElement(By.cssSelector("[data-qa-filters]")))
    }

    protected InputButton searchButton() {
        return componentFactory.createInputButton(By.cssSelector("input[data-qa-searchbutton]"))
    }

    ContractTypeTab payMonthlyTab() {
        return componentFactory.createContractTypeTab(By.cssSelector("[data-qa-tab-contract-type='paymonthly']"))
    }

    ContractTypeTab payAsYouGoTab() {
        return componentFactory.createContractTypeTab(By.cssSelector("[data-qa-tab-contract-type='payasyougo']"))
    }

    ContractTypeTab deviceOnlyTab() {
        return componentFactory.createContractTypeTab(By.cssSelector("[data-qa-tab-contract-type='nonconnected']"))
    }

    protected boolean isPaginationSectionVisible() {
        try {
            return driver.findElement(By.cssSelector(".pagination .page-navigation")).isDisplayed()
        }
        catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    protected List<ContractTypeTab> getContractTypeTabs() {
        List tabs = new ArrayList()
        ["paymonthly", "payasyougo", "nonconnected"].each {
            try {
                tabs.add(componentFactory.createContractTypeTab(By.cssSelector("[data-qa-tab-contract-type='" + it + "']")))
            } catch (IllegalArgumentException e) {
                //Tab doesn't exist
            }
        }
        return tabs
    }

    protected Link filterPhonesTab() {
        return componentFactory.createLink(By.cssSelector("[data-qa-filterby-section]"))
    }

    protected Link sortByTab() {
        return componentFactory.createLink(By.cssSelector("[data-qa-sortby-section]"))
    }

    public PhoneFilterSection phoneFilter() {
        WebElement filterPanel = driver.findElement(By.cssSelector('.filters'))
        return new PhoneFilterSection(filterPanel)
    }

    protected SortByOptions sortFilterPhones() {
        WebElement section = driver.findElement(By.cssSelector('.filters'))
        return new SortByOptions(section)
    }

    protected SortByHeader sortByHeader() {
        WebElement section = driver.findElement(By.cssSelector('[data-qa-sortby-section]'))
        return new SortByHeader(section)
    }

    protected SortByOptions sortByOptions() {
        WebElement section = driver.findElement(By.cssSelector('[data-qa-sortby-options]'))
        new SortByOptions(section)
    }

    protected List<Tile> getAllTiles() {
        return driver.findElements(By.cssSelector("[data-qa-tile]")).collect {
            new Tile(it)
        }
    }

    public List<Tile> devices() {
        return getAllTiles().findAll {
            it.isDevice()
        }
    }
    public List<Tile> promotions() {
        return getAllTiles().findAll {
            !it.isDevice()
        }
    }

    public List<Tile> getAllTilesSelectedToCompare(){
        List<Tile> devices =  devices()
        return devices.findAll {
            it.isSelectedToCompare()
        }
    }
    protected List<Tile> getAllNonComingSoonDevices() {
        return devices().findAll {
            !it.hasAvailability("comingSoon")
        }
    }

    public List<Tile> getAllTilesDisabledForComparison(){
        List<Tile> devices =  devices()
        devices = devices.findAll {
            it.isDisabledToCompare()
        }
        return devices;
    }


    String getPageHeader() {
        return helper().getHeaderText()
    }

    public boolean isAccessoryPage(){
        return "Accessories".equals(helper().getHeaderText())
    }

    public boolean isAccessoryLandingPage(WebDriver driver){
        return driver.getCurrentUrl().equals(Configuration.hostUrl + ShopContext.BrowsingShop.path + ShopContext.Accessories.path + "/")
    }

    protected String getDeviceFoundMessage() {
        return helper().findElement(By.cssSelector("[data-qa-num-of-devices]")).getText();
    }

    protected boolean isDeviceFoundMessageDisplayed() {
        return helper().findElement(By.className("num-devices-left")).isDisplayed();
    }

    public DeviceDetailsPage selectDeviceByName(String name) {
        devices().find {
            it.title.equalsIgnoreCase(name)
        }.select()
        new DeviceDetailsPage(driver)
    }

    public DeviceDetailsPage selectDeviceByBrandAndModel(brand, model) {
        def device = devices().find {
            brand.replace("-", " ").equalsIgnoreCase(it.brand) && model.replace("-", " ").equalsIgnoreCase(it.modelFamily)
        }
        if(device == null) {
            throw new PageObjectFrameworkException(String.format("No device found on device listing page with brand: %s, model: %s", brand, model))
        } else {
            device.select()
            new DeviceDetailsPage(driver)
        }
    }

    public DeviceDetailsPage selectFirstDevice() {
        devices().get(0).select()
        new DeviceDetailsPage(driver)
    }

    public DeviceDetailsPage selectDeviceAt(int position) {
        devices().get(position).select()
        new DeviceDetailsPage(driver)
    }

    public String checkTileMarginTopValue() {
        WebElement tile = driver.findElement(By.cssSelector("[data-qa-tile]"))
        return tile.getCssValue("margin-top")
    }

    public String checkTileMarginBottomValue() {
        WebElement tile = driver.findElement(By.cssSelector("[data-qa-tile]"))
        return tile.getCssValue("margin-bottom")
    }

    public String checkTileMarginLeftValue() {
        WebElement tile = driver.findElement(By.cssSelector("[data-qa-tile-column]"))
        return tile.getCssValue("margin-left")
    }

    public String checkTileMarginRightValue() {
        WebElement tile = driver.findElement(By.cssSelector("[data-qa-tile-column]"))
        return tile.getCssValue("margin-right")
    }

    public Tile getTileFromList(int tileNumber) {
        List<Tile> tileList = getAllTiles()
        Tile tile = tileList.get(tileNumber)
        return tile
    }

    public int getSizeOfAllTilesFound() {
        return getTotalTilesFound()
    }

    protected PaginationSection pagination() {
        WebElement section = driver.findElement(By.cssSelector(".pagination .page-navigation"))
        return new PaginationSection(section, componentFactory)
    }

    protected PaginationSection showAllPagination() {
        WebElement section = driver.findElement(By.cssSelector(".pagination"))
        return new PaginationSection(section, componentFactory)
    }
// END Page elements

// START Matcher methods
    protected int getTotalTilesFound() {
        return getAllTiles().size()
    }

    protected int getTotalDevicesFound() {
        return devices().size()
    }

    protected int getTotalPromotionsFound(){
        return promotions().size()
    }

    protected int getTotalNoOfDevicesDisplayed() {
        return driver.findElement(By.cssSelector("[data-qa-total-devices]")).getAttribute("data-qa-total-devices").toInteger()
    }

    protected int getTotalNoOfPagesDisplayed() {
        return driver.findElement(By.cssSelector("[data-qa-total-pages]")).getAttribute("data-qa-total-pages").toInteger()
    }

    protected int getCurrentPageNumber() {
        return driver.findElement(By.cssSelector("[data-qa-current-page]")).getAttribute("data-qa-current-page").toInteger()
    }

    protected boolean isPageNoDisplayed() {
        return driver.findElement(By.cssSelector("[data-qa-total-pages]")).isDisplayed()
    }


    protected boolean hasFooterPromotion(String footerName) {
        return !driver.findElements(By.cssSelector(footerName)).isEmpty();
    }


    public WebElement upgradingMsisdnBar() {
        return driver.findElement(By.className("upgrading-msisdn"))
    }

    public String subHeading() {
        return driver.findElement(By.cssSelector(".info p")).getText()
    }

    public WebElement backToUpgradeOptionsLink() {
        return driver.findElement(By.id("back-to-upgrade-options"))
    }

    public WebElement thereIsNothingToBuyMessage() {
        driver.findElement(By.id("nothingToBuy"))
    }

    public boolean isThereIsNothingToBuyMessageDisplayed() {
        try {
            thereIsNothingToBuyMessage()
            return true;
        }catch(NoSuchElementException e ) {
            return false
        }
    }

    public UpgradeOptionsPage clickBackToUpgradeOptionsLink() {
        driver.findElement(By.id("back-to-upgrade-options")).click()
        new UpgradeOptionsPage()
    }

    public void clickMoreLinkUnderFilterSection(String FilterType){
        if(FilterType ==  "category"){
            driver.findElement(By.cssSelector(".section.category [data-qa-category-filter-more-link]")).click();
        }

    }

    public DeviceListingsPage selectToCompare(String brand, String modelFamily) {
        def device = devices().find {
            brand.replace("-", " ").equalsIgnoreCase(it.brand) && modelFamily.replace("-", " ").equalsIgnoreCase(it.modelFamily)
        }
        if(device == null) {
            throw new PageObjectFrameworkException(String.format("No device found on device listing page with brand: %s, mode: %s", brand, modelFamily))
        } else {
            device.selectToCompare()
        }
        return this
    }
    public boolean isAddToCompareButtonsAvailableForAllDeviceTiles() {
        def devices = devices()
        devices.every {
            it.hasAddToCompareButtonAvailable()
        }
    }


    public boolean isAddToCompareButtonsDisplayedForDevices() {
        def devices = devices()
        devices.every {
            it.hasAddToCompareButtonAvailableForDevices()
        }
    }

    public int getCountOfSelectedDevicesToCompare() {
        getAllTilesSelectedToCompare().size()
    }


    protected boolean  isComparisonFloatingBarDisplayed() {
        try {
            return driver.findElement(By.cssSelector("[data-qa-comparison-footer]")).isDisplayed()
        }
        catch(org.openqa.selenium.NoSuchElementException e) {
            return false
        }
    }

    public List<String> getDevicesFromFloatingBar() {

        return driver.findElements(By.cssSelector("[data-qa-comparison-footer-devices] p")).collect {
                it.getText()

        }
    }

    public void scrollToComparisonFooter() {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver
        javascriptExecutor.executeScript("window.scrollTo(0,document.body.scrollHeight);")
    }

    public String getFloatingBarImageSrc()
    {
        return driver.findElement(By.cssSelector("[data-qa-comparison-footer-devices] img")).getAttribute("src").replace(Configuration.hostUrl, "")
    }


    protected boolean  isCompareButtonDisabled() {
        try {
             return driver.findElement(By.cssSelector("[data-qa-compare-devices]")).getAttribute("disabled")
        }
        catch(org.openqa.selenium.NoSuchElementException e) {
            return false
        }
    }

    def getSortByFeature() {
        List<WebElement> elements = driver.findElements(By.xpath("//div[@class='section data-qa-feature']/ul/li"))
        prepareListOfSortByFeatures(elements)
    }

    private List<String> prepareListOfSortByFeatures(List<WebElement> webElements) {
        List<String> sortByFeatures = []
        webElements.each { element -> sortByFeatures.add(element.getText()) }
        sortByFeatures
    }

    public boolean isExisitingCustomerUpgradeLinkExists() {
        try {
            WebElement link = driver.findElement(By.linkText("Existing customers sign in for upgrades"))
            return link.isDisplayed() && link.getAttribute("href").contains("/upgrade/")
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isMoreLinkForFilterExists(String filterType) {
        try {
            if(filterType.equalsIgnoreCase("category")){
                WebElement link = driver.findElement(By.cssSelector(".section.category [data-qa-category-filter-more-link]"));
                return link.isDisplayed()
            }

        } catch (org.openqa.selenium.NoSuchElementException e) {
                return false;
        }
    }

    public boolean isLessLinkForFilterExists(String filterType) {
        try {
            if(filterType.equalsIgnoreCase("category")){
                if(driver.findElement(By.cssSelector(".section.category [data-qa-category-filter-more-link]")).text.equalsIgnoreCase("Less"))
                {return true};
            }

        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isMoreLinkForFilterNotExists(String filterType) {
        try {
            if(filterType.equalsIgnoreCase("colour")){
                WebElement link = driver.findElement(By.cssSelector(".section.colour [data-qa-colour-filter-more-link]"));
                return link.isDisplayed()
            }

        } catch (org.openqa.selenium.NoSuchElementException e) {
                return true;
        }
    }

    public boolean isMoreLinkStateRetained(String filterType) {
        try {
            if(filterType.equalsIgnoreCase("category")){
                return driver.findElement(By.cssSelector(".section.category [data-qa-category-filter-more-link]")).displayed
            }

        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean textSearchSectionDisplayed() {
        try {
            WebElement element = driver.findElement(By.cssSelector("[data-qa-text-search-section]"))
            return element.displayed
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean compatibleDeviceFilterSectionDisplayed() {
        try {
            WebElement element = driver.findElement(By.cssSelector("[data-qa-compatible-device-filter-section]"))
            return element.displayed
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean searchResultsDisplayed() {
        try {
            WebElement element = driver.findElement(By.cssSelector("search-results"))
            return element.displayed
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    DeviceListingsPage selectDeviceFromSearchResult(String device) {
        deviceSearchSection.deviceWithName(device).click()
        WebDriverWait wait = new WebDriverWait(driver, 5)
        wait.until(ExpectedConditions.invisibilityOfElementLocated (By.className("search-results")))
        this
    }

    public selectedCompatibleDevice() {
        compatibleDeviceSearchField().text()
    }

    public boolean isBasketIconOnSubNavigationBarExist() {
        try {
            driver.findElement(By.cssSelector("data-qa-sub-nav-basket-icon"));
            return true;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public boolean isSubNavigationPanelExist() {
        try {
            return driver.findElement(cssSelector("[data-qa-sub-nav-module-panel]")).displayed;
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }


    def boolean isCountdownTimerDisplayed() {
        return driver.findElements(By.className("page-count-down-promo"))
    }

    String getCountdownTimerPromoMessage() {
        return helper().findElement(By.cssSelector(".message.headTxt")).text
    }
}
