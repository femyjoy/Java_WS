package from_ecom.browser

import PageObjects.ShopJourney
import PageObjects.WebdriverConcrete.*
import framework.utils.SeleniumTestBase
import org.testng.annotations.*

import static framework.utils.ShopContext.BrowsingShop
import static org.testng.Assert.assertEquals

/*
 * Tests specifically to validate page titles on product details and tariff lists pages in the Acquisition Shop.
 */

public class BrowsingPageTitleTests extends SeleniumTestBase {

    private ShopJourney browsingJourney

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        super.beforeTestClassAction()
        this.browsingJourney = new ShopJourney(BrowsingShop)
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeTest() {
        super.beforeTestAction()
    }

    @AfterMethod(alwaysRun = true)
    public void afterTest() {
        super.afterTestAction()
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        this.afterTestClassAction()
    }

    @Test(groups = ["acquisitionShopTest"])
    public void validatePageTitleOnPhonesDetailsAndTariffListPageHandsetJourney() {
        def page = browsingJourney.startHandsetFirstJourney(true)
        assertEquals(browser().getTitle(), "Pay Monthly Mobile Phones, Contracts, Offers & Deals | O2")

        def tariffAndExtrasPage = page.selectDeviceByName("Apple iPhone 5 64GB Purple cca").viewTariffs()
        assertEquals(browser().getTitle(), "O2 | Apple iPhone 5 64GB Purple cca | Tariffs And Extras")

        tariffAndExtrasPage.selectPayMonthlyTariffTile(0).clickOnContinueToBasketButton()

        assertEquals(browser().getTitle(), "O2 | Basket")
    }

    @Test(groups = ["acquisitionShopTest", "precheckin"])
    public void validatePageTitleOnProductFamilyDetailsAndTariffListPageHandsetJourney() {
        DeviceListingsPage deviceListingsPage= browsingJourney.startHandsetFirstJourney(true,true)
        assertEquals(browser().getTitle(), "Pay Monthly Mobile Phones, Contracts, Offers & Deals | O2")

        DeviceDetailsPage deviceDetailsPage = deviceListingsPage.selectDeviceByBrandAndModel("Apple", "iPhone 5")
        assertEquals(browser().getTitle(), "Apple iPhone 5 Specs, Contract Deals & Pay As You Go")

        deviceDetailsPage.selectColourByName("White");
        deviceDetailsPage.selectCapacityByName("64GB");

        TariffsAndExtrasPage tariffAndExtrasPage = deviceDetailsPage.viewTariffs()
        assertEquals(browser().getTitle(), "O2 | Apple iPhone 5 64GB White cca | Tariffs And Extras")

        def basketPage = tariffAndExtrasPage.selectBigBundleTariffTile().clickOnContinueToBasketButton()

        assertEquals(browser().getTitle(), "O2 | Basket")
    }

    //TODO REWRITE TEST - rework this test - without breadcrumbs - how do we navigate back?
/*
    @Test(groups=["acquisitionShopTest"])
    public void validatePageTitleOnProductFamilyDetailsAndTariffListPageHandsetJourneyWithAPhoneInBasket(){
        HandsetListPage handsetListPage = browsingJourney.startHandsetFirstJourney(true)
        assertEquals(handsetListPage,"O2 | Phones"))

        HandsetDetailsPage handsetDetailsPage = handsetListPage.getHandsetWithName("Apple iPhone 5").clickOnChoosePhoneButton()
        assertEquals(handsetDetailsPage,"O2 | Apple iPhone 5 | Buy now on Refresh"))


        RefreshTariffListPage refreshTariffListPage  = handsetDetailsPage.viewRefreshTariffs()

        assertEquals(refreshTariffListPage,"O2 | Apple iPhone 5 16GB Black cca | Refresh tariffs"))

        refreshTariffListPage.selectRefreshPlanAt(0)
        ExtrasPage extrasPage = refreshTariffListPage.clickOnBuyNow()
        assertEquals(extrasPage,"O2 | Extras"))

        handsetListPage = extrasPage.getBreadcrumb().clickOnBreadcrumb("Phones", HandsetListPage.class)



        assertEquals(handsetListPage,"O2 | Phones"))
        handsetDetailsPage = handsetListPage.getHandsetWithName("Samsung Standard").clickOnChoosePhoneButton()
        assertEquals(handsetDetailsPage,"O2 | Samsung Standard | Buy now on Refresh"))

    }
      */

    @Test(groups = ["acquisitionShopTest"])
    public void validatePageTitleOnPagesOnDongleDetailsAndTariffListPages() {
        def dongleDetailsPage = browsingJourney.navigateToDeviceDetailsPage("huawei", "e173-postpay-and-prepay", true,true)
        assertEquals(browser().getTitle(), "Huawei E173 Postpay And Prepay Specs, Contract Deals & Pay As You Go")
        TariffsAndExtrasPage tariffsAndExtrasPage = dongleDetailsPage.viewTariffs()
        assertEquals(browser().getTitle(), "O2 | Huawei E173 Postpay And Prepay | Tariffs And Extras")
        dongleDetailsPage = tariffsAndExtrasPage.clickOnBackToProductDetails()
        tariffsAndExtrasPage = dongleDetailsPage.viewTariffs().viewPayAsYouGoTariffs()
        assertEquals(browser().getTitle(), "O2 | Huawei E173 Postpay And Prepay | Tariffs And Extras")
        tariffsAndExtrasPage.selectMBBPAYGTariffTile(0).clickOnContinueToBasketButton()
        assertEquals(browser().getTitle(), "O2 | Basket")
    }

    @Test(groups = ["acquisitionShopTest"])
    public void validatePageTitleOnPagesOnTabletDetailsAndTariffListPages() {
        def tabletDetailsPage = browsingJourney.navigateToDeviceDetailsPage("firestorm", "enro", true)
        assertEquals(browser().getTitle(), "Firestorm enro Specs, Contract Deals & Pay As You Go")
        def tariffsAndExtrasPage = tabletDetailsPage.viewTariffs()
        assertEquals(browser().getTitle(), "O2 | Firestorm enro | Tariffs And Extras")
        tariffsAndExtrasPage.selectMBBPAYGTariffTile(0).clickOnContinueToBasketButton()
        assertEquals(browser().getTitle(), "O2 | Basket")
    }

    @Test(groups = ["acquisitionShopTest"])
    public void validatePageTitleOnPagesOnHotspotDetailsAndTariffListPages() {
        def hotspotDetailsPage = browsingJourney.navigateToDeviceDetailsPage( "zte-mf60", "o2-pocket-hotspot", true)
        assertEquals(browser().getTitle(), "ZTE MF60 O2 Pocket Hotspot Specs, Contract Deals & Pay As You Go")
        hotspotDetailsPage.viewTariffs()
        assertEquals(browser().getTitle(), "O2 | ZTE MF60 O2 Pocket Hotspot | Tariffs And Extras")
    }

    @Test(groups = ["acquisitionShopTest"])
    public void validateTheBackToProductDetailsInTheHeaderForPAYM() {
        def tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByName("Samsung Standard")
                .viewTariffs()

        assertEquals(browser().getTitle(), "O2 | Samsung standard phone cca Silver 64GB | Tariffs And Extras")
        tariffsAndExtrasPage.clickOnBackToProductDetails()
        assertEquals(browser().getTitle(), "Samsung Standard Specs, Contract Deals & Pay As You Go")
    }

    @Test(groups = ["acquisitionShopTest"])
    public void validateTheBackToProductDetailsInTheHeaderForPAYG() {
        def payAsYouGoTariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true,true,"payasyougo")
                .selectDeviceByName("Apple iPhone 5S Indigo")
                .viewTariffs()
        assertEquals(browser().getTitle(), "O2 | Apple iPhone 5S Indigo | Tariffs And Extras")
        payAsYouGoTariffsAndExtrasPage.clickOnBackToProductDetails()
        assertEquals(browser().getTitle(), "Apple iPhone 5S Indigo Specs, Contract Deals & Pay As You Go")

    }

    @Test(groups = ["acquisitionShopTest"])
    public void validateTheBackToProductDetailsInTheHeaderOnDirectLandingToTariffAndExtrasPage() {
        TariffsAndExtrasPage directLandingInTariffsAndExtrasPage = browsingJourney.navigateToTariffsAndExtrasPage("samsung", "standard")
        assertEquals(browser().getTitle(), "O2 | Samsung standard phone cca Silver 64GB | Tariffs And Extras")
        directLandingInTariffsAndExtrasPage.clickOnBackToProductDetails()
        assertEquals(browser().getTitle(), "Samsung Standard Specs, Contract Deals & Pay As You Go")
    }

    //ECOM-5939
    @Test(groups = ["acquisitionShopTest", "precheckin"])
    public void "validate the page title for accessory list and details page"() {
        DeviceListingsPage deviceListingsPage= browsingJourney.startAccessoriesFirstJourney(true)
        assertEquals(browser().getTitle(), "Mobile Smartphone and Tablet accessories & cases")

        DeviceDetailsPage deviceDetailsPage = deviceListingsPage.selectDeviceByBrandAndModel("sandisk", "memory-card")
        assertEquals(browser().getTitle(), "SanDisk Memory Card - accessories from O2")

        deviceDetailsPage.addAccessoryToBasket()
        deviceDetailsPage.goToBasket()
        assertEquals(browser().getTitle(), "O2 | Basket")
    }
}
