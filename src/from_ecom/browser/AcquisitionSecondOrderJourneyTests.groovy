package from_ecom.browser

import PageObjects.ShopJourney
import PageObjects.WebdriverConcrete.ContinueOrAbandonCheckoutPage
import PageObjects.WebdriverConcrete.DeviceDetailsPage
import PageObjects.WebdriverConcrete.DeviceListingsPage
import framework.utils.SeleniumTestBase
import org.testng.annotations.*
import uk.co.o2.ecomm.common.codec.DefaultCodec
import uk.co.o2.ecomm.common.codec.StringCodec
import webclient.ProductCatalogueClientHeadersFactory
import webclient.ProductCatalogueServiceGateway
import webclient.ProductResourcesClient

import static PageObjects.WebdriverConcrete.Widgets.EShop.Basket.BasketErrors.ErrorMessage.OUT_OF_STOCK
import static framework.utils.ShopContext.BrowsingShop
import static org.testng.Assert.*

public class AcquisitionSecondOrderJourneyTests extends SeleniumTestBase {

    private ProductCatalogueServiceGateway catalogueGateway;
    private ShopJourney browsingJourney
    private StringCodec codec

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        super.beforeTestClassAction()

        def pcClient = new ProductResourcesClient(ProductCatalogueClientHeadersFactory.customerAcquisition("ID-002000"))
        this.catalogueGateway = new ProductCatalogueServiceGateway(pcClient)
        this.browsingJourney = new ShopJourney(BrowsingShop)
        this.codec = new DefaultCodec()
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
        catalogueGateway.resetProductCatalogueFixtures()
        this.afterTestClassAction()
    }


    @Test(groups = ["acquisitionShopTest", "journeyTest"])
    public void signedInAcquisitionCreationOfRefreshBasketAfterCreatingRefreshOrderAndAbandonOldCheckout() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
            .selectDeviceByBrandAndModel("lg", "gw300")
            .viewTariffs()
            .selectBigBundleTariffTile()
            .selectAccessoryTile("8feedaa2-2396-44bf-8084-38024e3c522e")
            .clickOnContinueToBasketButton()
        assertTrue(basketPage.getDevice().getDeviceName().equals("LG GW300"))

        assertTrue(basketPage.isCheckoutAllowed());
        basketPage.clickOnCheckOut(1)

        def checkoutInProgressPage = browser().navigateToDeviceListingsPageFor("phones")
            .selectDeviceByBrandAndModel("samsung", "standard").viewTariffs().viewPayMonthlyTariffs()
            .selectPayMonthlyTariffTile(0)
            .selectAccessoryTile(0)
            .clickOnContinueToBasketButtonExpectingCheckoutInProgressPage()

        basketPage = checkoutInProgressPage.abandonCheckoutExpectingBasketPage();
        assertTrue(basketPage.getDevice().getDeviceName().equals("Samsung Standard"))
    }

    @Test(groups = ["acquisitionShopTest", "journeyTest"])
    public void signedInAcquisitionCreationOfSIMOBasketAfterCreatingRefreshOrderAndAbandonOldCheckout() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
            .selectDeviceByBrandAndModel("lg", "gw300")
            .viewTariffs()
            .selectBigBundleTariffTile()
            .selectAccessoryTile("8feedaa2-2396-44bf-8084-38024e3c522e")
            .clickOnContinueToBasketButton()
        assertTrue(basketPage.getDevice().getDeviceName().equals("LG GW300"))

        assertTrue(basketPage.isCheckoutAllowed());
        basketPage.clickOnCheckOut(1)

        def checkoutInProgressPage = browser().navigateToSimplicityPage().tariffSection().findTariffAt(0)
            .select().selectDeviceFromDropDown("iPhone").addAndGotoContinueOrAbandonCheckoutPage()

        basketPage = checkoutInProgressPage.abandonCheckoutExpectingBasketPage();
        assert basketPage.tariff.getMonthlyCost() == "£26.50"
    }

    @Test(groups = ["acquisitionShopTest", "journeyTest"])
    public void signedInAcquisitionCreationOfAccessoryOnlyBasketAfterCreatingRefreshOrderAndAbandonOldCheckout() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
            .selectDeviceByBrandAndModel("lg", "gw300")
            .viewTariffs()
            .selectBigBundleTariffTile()
            .selectAccessoryTile("8feedaa2-2396-44bf-8084-38024e3c522e")
            .clickOnContinueToBasketButton()
        assertTrue(basketPage.getDevice().getDeviceName().equals("LG GW300"))

        assertTrue(basketPage.isCheckoutAllowed());
        basketPage.clickOnCheckOut(1)

        DeviceListingsPage deviceListPage = browser().navigateToDeviceListingsPageFor("accessories")
        def accessoryDetailPage = deviceListPage.selectDeviceByName("SanDisk Memory Card")

        accessoryDetailPage.addAccessoryToBasket()
        def checkoutInProgressPage = accessoryDetailPage.goToBasketExpectingCheckoutInProgressPage()

        basketPage = checkoutInProgressPage.abandonCheckoutExpectingBasketPage();

        def accessories = basketPage.getAccessories()
        assertEquals(accessories.get(0).getTitle(), "SanDisk Memory Card")
        assertEquals(accessories.get(0).getUpfrontCost(), "£25.00")
    }

    @Test(groups = ["acquisitionShopTest", "journeyTest"])
    public void signedInAcquisitionCreationOfNonConnectedBasketAfterCreatingRefreshOrderAndAbandonOldCheckout() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
            .selectDeviceByBrandAndModel("lg", "gw300")
            .viewTariffs()
            .selectBigBundleTariffTile()
            .selectAccessoryTile("8feedaa2-2396-44bf-8084-38024e3c522e")
            .clickOnContinueToBasketButton()
        assertTrue(basketPage.getDevice().getDeviceName().equals("LG GW300"))

        assertTrue(basketPage.isCheckoutAllowed());
        basketPage.clickOnCheckOut(1)

        DeviceDetailsPage deviceDetailsPage = browser().navigateToDeviceListingsPageFor("fitness-trackers").viewNonConnected().selectFirstDevice()
        deviceDetailsPage.addToBasket()
        def checkoutInProgressPage = deviceDetailsPage.goToBasketExpectingCheckoutInProgressPage()

        basketPage = checkoutInProgressPage.abandonCheckoutExpectingBasketPage();
        assert basketPage.accessories[0].title == "Samsung Fitness Band non sim"
    }


    @Test(groups = ["acquisitionShopTest", "journeyTest"])
    public void signedInAcquisitionCreationOfRefreshBasketAfterCreatingRefreshOrderAndFinishPreviousOrder() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
            .selectDeviceByBrandAndModel("lg", "gw300")
            .viewTariffs()
            .selectBigBundleTariffTile()
            .selectAccessoryTile("8feedaa2-2396-44bf-8084-38024e3c522e")
            .clickOnContinueToBasketButton()
        assertTrue(basketPage.getDevice().getDeviceName().equals("LG GW300"))
        assertTrue(basketPage.isCheckoutAllowed());
        basketPage.clickOnCheckOut(1)

        def checkoutInProgressPage = browser().navigateToDeviceListingsPageFor("phones")
            .selectDeviceByBrandAndModel("samsung", "standard").viewTariffs().viewPayMonthlyTariffs()
            .selectPayMonthlyTariffTile(0)
            .selectAccessoryTile(0)
            .clickOnContinueToBasketButtonExpectingCheckoutInProgressPage()
        String mainBasketId = checkoutInProgressPage.getMainBasketId()

        String checkoutURL = checkoutInProgressPage.finishOrderAndGetCheckoutURL();
        String encodedAndUTF8OrderId = URLEncoder.encode(codec.encode(mainBasketId), "UTF-8")
        assertTrue(checkoutURL.contains(encodedAndUTF8OrderId))
        //clean up for next tests
        browser().browserBack()
        new ContinueOrAbandonCheckoutPage().abandonCheckoutExpectingBasketPage()

    }
    @Test(groups = ["acquisitionShopTest", "journeyTest"])
    public void signedInAcquisitionCreationOfRefreshBasketAfterCreatingRefreshOrderAndFinishPreviousOrderWhileDeviceInFirstOrderIsOutOfStock() {


        def basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("lg", "gw300")
                .viewTariffs()
                .selectBigBundleTariffTile()
                .selectAccessoryTile("8feedaa2-2396-44bf-8084-38024e3c522e")
                .clickOnContinueToBasketButton()
        assertTrue(basketPage.getDevice().getDeviceName().equals("LG GW300"))
        assertTrue(basketPage.isCheckoutAllowed());
        basketPage.clickOnCheckOut(1)

        catalogueGateway.makeItemOutOfStock("device", "e7eef155-9215-4181-b1d9-cfc2e20c79e9")

        try {
            def checkoutInProgressPage = browser().navigateToDeviceListingsPageFor("phones")
                    .selectDeviceByBrandAndModel("samsung", "standard").viewTariffs().viewPayMonthlyTariffs()
                    .selectPayMonthlyTariffTile(0)
                    .selectAccessoryTile(0)
                    .clickOnContinueToBasketButtonExpectingCheckoutInProgressPage()

            checkoutInProgressPage.finishOrderAndGetCheckoutURL();

            assertTrue(basketPage.getErrorMessages().contains(OUT_OF_STOCK));
            assertTrue(basketPage.getDevice().errors.contains("Out of stock."))
            assertFalse(basketPage.isCheckoutAllowed());
        } finally {
            catalogueGateway.makeItemInStock("device", "e7eef155-9215-4181-b1d9-cfc2e20c79e9")
        }
        //clean up for next tests
        browser().browserBack()
        new ContinueOrAbandonCheckoutPage().abandonCheckoutExpectingBasketPage()

    }

    @Test(groups = ["acquisitionShopTest", "journeyTest"])
    public void signedInAcquisitionCreationOfRefreshBasketAfterCreatingRefreshOrderAndFinishPreviousOrderWhileDeviceInFirstOrderIsOutOfStockButClickAndCollect() {

        def deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "GW300")
                .openCheckStockInStoreOverlay()
                .searchStoresIn('SL1')

        deviceDetailsPage.selectStoreByIndex(1);

        def basketPage = deviceDetailsPage
                .viewTariffs()
                .selectBigBundleTariffTile()
                .clickOnContinueToBasketButton()
        assertTrue(basketPage.getDevice().getDeviceName().equals("LG GW300"))
        assertTrue(basketPage.isCheckoutAllowed());
        basketPage.clickOnCheckOut(1)

        catalogueGateway.makeItemOutOfStock("device", "e7eef155-9215-4181-b1d9-cfc2e20c79e9")
        try {
            def checkoutInProgressPage = browser().navigateToDeviceListingsPageFor("phones")
                    .selectDeviceByBrandAndModel("samsung", "standard").viewTariffs().viewPayMonthlyTariffs()
                    .selectPayMonthlyTariffTile(0)
                    .selectAccessoryTile(0)
                    .clickOnContinueToBasketButtonExpectingCheckoutInProgressPage()

            String checkoutURL = checkoutInProgressPage.finishOrderAndGetCheckoutURL();
            String mainBasketId = checkoutInProgressPage.getMainBasketId();

            assertFalse(basketPage.isCheckoutAllowed());
            String encodedAndUTF8OrderId = URLEncoder.encode(codec.encode(mainBasketId), "UTF-8")
            assertTrue(checkoutURL.contains(encodedAndUTF8OrderId))
        } finally {
            catalogueGateway.makeItemInStock("device", "e7eef155-9215-4181-b1d9-cfc2e20c79e9")
        }
        //clean up for next tests
        browser().browserBack()
        new ContinueOrAbandonCheckoutPage().abandonCheckoutExpectingBasketPage()

    }
}