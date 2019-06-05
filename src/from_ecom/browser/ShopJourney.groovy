package from_ecom.browser

import PageObjects.Enum.FreeSimTariffType
import PageObjects.WebdriverConcrete.*
import PageObjects.WebdriverConcrete.NewUpgradeOptions.UpgradeOptionsPage
import PageObjects.helper.PageHelper
import framework.Configuration
import framework.browser.BrowserFactory
import framework.browser.WebBrowser
import framework.utils.ShopContext
import framework.utils.Utilities
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver

import static framework.utils.ShopContext.BrowsingShop

//Methods in this class, ideally should be invoked only once per test, as it will clear cookies for each request.
public class ShopJourney {

    private final String shopContext
    private final String hostUrl
    private final String shopUrl

    private String prependUrl = "http://servedby.o2.co.uk/click/6/28251;641128;369310;252;0/?ft_keyword={keyword}&ft_section={matchtype}&url="
    private String appendUrl = "&cm_mmc=googleuk-_-PPC_PAYG_PLA-_-SamsungGalaxySIII4G-_-SamsungGalaxySIII4G+SamsungGalaxySIII4G+Exact"

    public ShopJourney(ShopContext context) {
        this.shopContext = Configuration.server.contains("ref") && context == BrowsingShop ? "/shop/" : context.path
        this.hostUrl = "https://" + Configuration.server + ":" + Configuration.serverPort;
        this.shopUrl = hostUrl + shopContext
    }

    String getShopContext() {
        return shopContext
    }

    String getHostUrl() {
        return hostUrl
    }

    public String getShopUrl() {
        return this.shopUrl
    }

    public UpgradeSignInPage showSignInPage() {
        String url = hostUrl + shopContext + "signin/";
        return Start(url, UpgradeSignInPage.class, false);
    }

    public static void resetPITUserOtacStatus(String msisdn) {
        Utilities.simplePutRequest("http://10.200.170.15:" + Configuration.serverPort + "/otac/reset?msisdn=" + msisdn);
    }

    public BasketPage startFusePumpAccessoryJourney(String accessoryId, boolean forceBrowserRefresh = false, prependAppend = false) {
        String url = hostUrl + shopContext + "fusepump/add/accessory/?id=${accessoryId}"
        url = prependAppend ? prependUrl + url + appendUrl : url
        return Start(url, BasketPage.class, forceBrowserRefresh);
    }

    public BasketPage startFusePumpStandardJourney(String deviceId = null, String planId = null, String dataAllowanceId = null, boolean forceBrowserRefresh = false, prependAppend = false) {
        String url = hostUrl + shopContext + buildFusePumpUrl(deviceId, planId, dataAllowanceId)
        url = prependAppend ? prependUrl + url + appendUrl : url
        return Start(url, BasketPage.class, forceBrowserRefresh);
    }

    public BasketPage startFusePumpMbbDevicesJourney(String deviceId = null, String planId = null, String dataAllowanceId = null, boolean forceBrowserRefresh = false, prependAppend = false) {

        String url = shopUrl + buildFusePumpUrl(deviceId, planId, dataAllowanceId)
        url = prependAppend ? prependUrl + url + appendUrl : url
        return Start(url, BasketPage.class, forceBrowserRefresh);
    }

    public BasketPage startFusePumpRefreshJourney(String deviceId, String planId, String dataAllowanceId, String oneOff, String monthly, boolean forceBrowserRefresh = false, prependAppend = false) {
        String url = shopUrl + "fusepump/add/product/?device=${deviceId}&plan=${planId}&dataallowance=${dataAllowanceId}&oneOff=${oneOff}&monthly=${monthly}"
        url = prependAppend ? prependUrl + url + appendUrl : url
        return Start(url, BasketPage.class, forceBrowserRefresh);
    }

    public BasketPage startFusePumpmbbRefreshJourney(String deviceId, String planId, String oneOff, String monthly, boolean forceBrowserRefresh = false, prependAppend = false) {
        String url = shopUrl + "fusepump/add/product/?device=${deviceId}&plan=${planId}&oneOff=${oneOff}&monthly=${monthly}"
        url = prependAppend ? prependUrl + url + appendUrl : url
        return Start(url, BasketPage.class, forceBrowserRefresh);
    }

    public void abandonCheckout() {
        String url = shopUrl + "abandonCheckout/"
        browser().navigateTo(url);
    }

    public DeviceListingsPage startHandsetFirstJourney(boolean forceBrowserRefresh = false,boolean showAllDevices = false, String contractType="paymonthly") {
        String url = shopUrl + "phones/?contractType=${contractType}"
        navigateToUrl(url, forceBrowserRefresh)
        def listingPage = new DeviceListingsPage(browser().driver())
        showAllDevices ? listingPage.showAll() :listingPage
    }

    public DeviceListingsPage startTabletFirstJourney(boolean forceBrowserRefresh = false,boolean showAll = false, String contractType="paymonthly") {
        String url = shopUrl + "tablets/?contractType=${contractType}"
        navigateToUrl(url, forceBrowserRefresh)
        def listingPage = new DeviceListingsPage(browser().driver())
        showAll ? listingPage.showAll() : listingPage
    }

    public DeviceListingsPage startSmartwatchesFirstJourney(boolean forceBrowserRefresh = false, boolean showAll = false, String contractType="paymonthly") {
        String url = shopUrl + "smartwatches/?contractType=${contractType}"
        navigateToUrl(url, forceBrowserRefresh)
        def listingPage = new DeviceListingsPage(browser().driver())
        showAll ? listingPage.showAll() : listingPage
    }

    public DeviceListingsPage startFitnessTrackersFirstJourney(boolean forceBrowserRefresh = false,boolean showAll = false, String contractType="paymonthly") {
        String url = shopUrl + "fitness-trackers/?contractType=${contractType}"
        navigateToUrl(url, forceBrowserRefresh)
        def listingPage = new DeviceListingsPage(browser().driver())
        showAll ? listingPage.showAll() : listingPage
    }

    public DeviceListingsPage startAccessoriesFirstJourney(boolean forceBrowserRefresh = false,boolean showAll = false) {
        String url = shopUrl + "accessories/all?contractType=nonconnected"
        navigateToUrl(url, forceBrowserRefresh)
        def listingPage = new DeviceListingsPage(browser().driver())
        showAll ? listingPage.showAll() : listingPage
    }

    public BasketPage navigateToAcquisitionBasketWithRefreshTariff(boolean forceBrowserRefresh = false) {
        def listingPage = startHandsetFirstJourney(forceBrowserRefresh)
        def tariffsPage = listingPage.selectDeviceByBrandAndModel("Apple", "Mini CCA5").viewTariffs()
        tariffsPage.getPayMonthlyGridPlans().first().select()
        tariffsPage.clickOnContinueToBasketButton()
    }

    public BasketPage navigateToAcquisitionBasketWithRefreshTariffAndAccessory() {
        def tariffsPage = navigateToDeviceDetailsPage("LG", "G3").viewTariffs()
        tariffsPage.getPayMonthlyGridPlans().first().select()
        tariffsPage.selectAccessoryTile(0)
        tariffsPage.clickOnContinueToBasketButton()
    }

    public OTACPage navigateToOtacPage(String userName, String password) {
        def signInPage = showSignInPage()
        signInPage.signIn(userName, password)
        def upgradeOptionsPage = new UpgradeOptionsPage()
        def deviceDetailsPage = upgradeOptionsPage.viewAllPhones().showAll().selectDeviceByBrandAndModel("samsung", "standard")
        def page = deviceDetailsPage.viewTariffs()
                .selectPayMonthlyTariffTile(0)
        page.clickOnContinueToBasketButton()

        helper().findElement(By.cssSelector("input[name='securecheckout']")).click()
        new OTACPage().clickOnSendOTAC()
    }

    public HandsetListPage startHandsetFirstJourneyWithOutExpandingPages() {
        String url = shopUrl + "phones/#page=1";
        return Start(url, HandsetListPage.class, true);
    }

    public DeviceListingsPage startJourneyToPAYGHandsetListPage(boolean forceBrowserRefresh = false) {
        String url = shopUrl + "phones/?contractType=payasyougo";
        navigateToUrl(url, forceBrowserRefresh)
        def handsetListPage = new DeviceListingsPage(browser().driver())
        handsetListPage.showAll()
        handsetListPage
    }

    public ShopErrorPage startJourneyToTariffsListPage(boolean forceBrowserRefresh = false) {
        String url = shopUrl + "tariffs/?is4G=false";
        return Start(url, ShopErrorPage.class, forceBrowserRefresh);
    }

    public ShopErrorPage navigateToExpectingErrorPage(url) {
        browser().navigateTo(shopUrl+url)
        return new ShopErrorPage()
    }

    public ShopErrorPage navigateToErrorPage(String url) {
        browser().navigateTo(shopUrl + url)
        return new ShopErrorPage()
    }

    public ShopPage navigateToShopPage() {
        browser().navigateTo(shopUrl)
        return new ShopPage(browser().driver())
    }

    public PayMonthlySimOnlyPage startJourneyToSimplicityPage(boolean forceBrowserRefresh = false) {
        String url = shopUrl + "sim-cards/sim-only-deals/";
        Start(url, PayMonthlySimOnlyPage.class, forceBrowserRefresh);
    }

    public BuildYourOwnTariffPage startJourneyToBuildYourOwnTariffPage(boolean forceBrowserRefresh = false) {
        String url = shopUrl + "sim-cards/sim-only-deals/30-day/";
        Start(url, BuildYourOwnTariffPage.class, forceBrowserRefresh);
    }

    public MyOffersPage startJourneyToMyOffersPageForCategory(String category, boolean forceBrowserRefresh = false) {
        String url = shopUrl + "my-offers/" + category;
        Start(url, MyOffersPage.class, forceBrowserRefresh);
    }


    public DeviceDetailsPage navigateToDeviceDetailsPage(String brand, String model, boolean forceBrowserRefresh = false, boolean payG = false) {
        return browser().navigateToDeviceDetailsPage(brand, model, payG)
    }

    public DeviceDetailsPage navigateToDeviceDetailsPageWithSubtype(String brand, String model, String subType) {
        return browser().navigateToDeviceDetailsPageWithSubType(brand, model, subType)
    }

    public DeviceDetailsPage deepLinkToDeviceDetailsPage(String deviceType, String brand, String model, String parm="") {
        return browser().deepLinkToDeviceDetailsPage(deviceType,brand, model, parm)
    }

    public DeviceListingsPage startJourneyWithPhoneCanonicalBrand(Boolean payAndGo = false, String brand) {
        String url = shopUrl + "phones/" + brand + "/";
        url = payAndGo ? url + "?payGo=true" : url;
        navigateToUrl(url, true)
        new DeviceListingsPage(browser().driver())
    }

    public BasketPage navigateToExtrasPageForAffiliateUser() {
        String url = shopUrl + "extras/";
        browser().navigateTo(url)
        browser().refresh()
        return new BasketPage();
    }

    public TariffsAndExtrasPage navigateToTariffsAndExtrasPage(String brand, String model) {
        String url = shopUrl + "tariff/" + brand + "/" + model + "/"
        browser().navigateTo(url)
        return PageObjectFactory.CreateWebDriverPageObject(TariffsAndExtrasPage.class)
    }

    public DeviceListingsPage navigateToHandsetListPage() {
        String url = shopUrl + "phones/#page=1";
        browser().navigateTo(url)
        browser().refresh()
        new DeviceListingsPage(browser().driver())
    }

    public DeviceListingsPage navigateToAccessoriesListPage() {
        String url = shopUrl + "accessories/all";
        browser().navigateTo(url)
        new DeviceListingsPage(browser().driver())
    }

    public DeviceListingsPage navigateToWearablesListPage(String wearablePath) {
        String url = shopUrl + wearablePath;
        browser().navigateTo(url)
        new DeviceListingsPage(browser().driver())
    }

    public <T extends CustomerBasePage> T navigateTo(String path, Class<T> nextPage = null, boolean withDriver = false) {
        String url = "${shopUrl}${path}";
        browser().navigateTo(url)
        if (nextPage) {
            withDriver ? nextPage.getConstructor(WebDriver).newInstance(browser().driver()) : nextPage.newInstance()
        }
    }

    private static String buildFusePumpUrl(String deviceId, String planId, String dataAllowanceId) {
        String url = "fusepump/add/product/?"
        url = deviceId ? "${url}device=${deviceId}" : url
        url = planId ? "${url}&plan=${planId}" : url
        url = dataAllowanceId ? "${url}&dataallowance=${dataAllowanceId}" : url
        return url
    }

    private navigateToUrl(String url,  boolean forceBrowserRefresh) {
        browser().cookies().deleteAll();
        browser().navigateTo(url);

        if (forceBrowserRefresh) {
            browser().refresh()
        }
    }

    private <T> T Start(String url, Class<T> expectedPage, boolean forceBrowserRefresh) {
        navigateToUrl(url, forceBrowserRefresh)
        return PageObjectFactory.CreateWebDriverPageObject(expectedPage);
    }

    public TariffsAndExtrasPage navigateToTariffsAndExtrasPage(String brand, String model, String productId,
                                                               String planId, String contractType,
                                                               String accessoriesId = "", String insuranceId = "",
                                                               boolean forceBrowserRefresh = false) {
        String url = shopUrl + "tariff/" + brand + "/" + model + "/?productId=" + productId + "&planId=" + planId +
                "&contractType=" + contractType + "&accessories=" + accessoriesId + "&insuranceId=" + insuranceId;
        return Start(url, TariffsAndExtrasPage.class, forceBrowserRefresh);
    }

    private WebBrowser browser() {
        return new BrowserFactory().browser();
    }

    public PageHelper helper() {
        return browser().currentPageHelper();
    }

    def FreeSimConfirmationPage createFreeSimOrderFor(FreeSimTariffType tariffType) {
        def freeSimPage = FreeSimPage.navigateToFreeSimPage(browser().driver())
        def deliveryPage
        switch (tariffType) {
            case FreeSimTariffType.BIG_BUNDLES:
            case FreeSimTariffType.BIG_TALKER:
            case FreeSimTariffType.INTERNATIONAL:
                deliveryPage = freeSimPage.selectSubTab(tariffType.name).selectTariff(tariffType.name, 2)
                break
            case FreeSimTariffType.IPAD:
                deliveryPage = freeSimPage.selectTab("iPads and tablets").getiPadTile().select()
                break
            case FreeSimTariffType.TABLET:
            default:
                deliveryPage = freeSimPage.selectTab("iPads and tablets").getTabletTile().select()
                break
        }
        deliveryPage.populatePersonalDetailsWithAddressAndSubmitOrder([houseNumber: '260', postCode: 'SL1 4DX'], [email: 'test@test.com', title: 'Mr', fName: 'testFname', lName: 'testLname', mobile: '07999999999'])
    }
}

