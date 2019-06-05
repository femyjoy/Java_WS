package from_ecom.browser
import PageObjects.ShopJourney
import PageObjects.WebdriverConcrete.BasketPage
import PageObjects.WebdriverConcrete.DeviceDetailsPage
import PageObjects.WebdriverConcrete.DeviceListingsPage
import PageObjects.WebdriverConcrete.TariffsAndExtrasPage
import PageObjects.WebdriverConcrete.Widgets.EShop.Basket.BasketDevice
import PageObjects.WebdriverConcrete.Widgets.EShop.Basket.BasketItem
import PageObjects.WebdriverConcrete.Widgets.EShop.Basket.BasketTariff
import PageObjects.WebdriverConcrete.Widgets.EShop.DeviceListings.DeviceFilter
import PageObjects.WebdriverConcrete.Widgets.EShop.Plan
import PageObjects.WebdriverConcrete.Widgets.EShop.TariffAndExtras.PackageSection
import framework.utils.RunInMobileBrowser
import framework.utils.SeleniumTestBase
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import junit.framework.Assert
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.springframework.core.io.ClassPathResource
import org.testng.annotations.*
import uk.co.o2.eShop.testing.FixturesInspector
import uk.co.o2.eshop.util.VersionInfo
import uk.co.o2.utils.PagePrice
import webclient.ProductCatalogueClientHeadersFactory
import webclient.ProductCatalogueServiceGateway
import webclient.ProductResourcesClient
import webclient.RestClientHelper

import static PageObjects.WebdriverConcrete.DeviceDetailsPageMatcher.aDeviceDetailsPage
import static PageObjects.WebdriverConcrete.ErrorPageMatcher.aErrorPage
import static PageObjects.WebdriverConcrete.NewUpgradeOptions.DeviceSearchMatcher.aNewDeviceSearch
import static PageObjects.WebdriverConcrete.NewUpgradeOptions.DeviceTileMatcher.aDeviceTile
import static PageObjects.WebdriverConcrete.Widgets.EShop.Basket.BasketErrors.ErrorMessage.ACCESSORY_LIMITATION
import static PageObjects.WebdriverConcrete.Widgets.EShop.Basket.BasketErrors.ErrorMessage.OUT_OF_STOCK
import static PageObjects.WebdriverConcrete.Widgets.EShop.DeviceDetails.Header.CostSectionMatcher.aDeviceCost
import static PageObjects.WebdriverConcrete.Widgets.EShop.DeviceDetails.Header.VariantSectionMatcher.aVariant
import static PageObjects.WebdriverConcrete.Widgets.EShop.DeviceDetails.RecommendedTariffSectionMatcher.aRecommendedTariffSection
import static PageObjects.WebdriverConcrete.Widgets.EShop.DeviceListings.DeviceFilter.allDevices
import static framework.utils.ShopContext.BrowsingShop
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue
import static org.testng.Assert.*
import static PageObjects.Enum.FreeSimTariffType.*

public class BrowsingJourneyTests extends SeleniumTestBase {

    private ProductCatalogueServiceGateway catalogueGateway;
    private ShopJourney browsingJourney

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        super.beforeTestClassAction()

        def pcClient = new ProductResourcesClient(ProductCatalogueClientHeadersFactory.customerAcquisition("ID-002000"))
        this.catalogueGateway = new ProductCatalogueServiceGateway(pcClient)
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
        catalogueGateway.resetProductCatalogueFixtures()
        this.afterTestClassAction()
    }

    //ECOM-6667
    @Test(groups = ["acquisitionDetailsShopTest", "testAcquisitionShopTest"])
    public void " Verify O2 recycle widget on basket page"() {
        def basketPage = browsingJourney.navigateToAcquisitionBasketWithRefreshTariff()
        String imageURL = "${framework.Configuration.hostUrl}/upgrade/static/0/newShopStatic/_assets/img/modules/o2-recycle/recycle-bg.jpg"

        def o2RecycleSection = basketPage.getO2RecycleSection()
        assert o2RecycleSection
        o2RecycleSection.selectMake("Apple");
        o2RecycleSection.selectModel("iPhone 3G S 8GB");
        assertThat(o2RecycleSection.recyclePriceTagMessage(), is("Get up to £4.00"))
    }

    //ECOM-9064
    @Test(groups = "acquisitionDetailsShopTest")
    public void " Verify sub-navigation module panel  on Listing,Details,Tariff,Basket pages"() {
        DeviceListingsPage handsetListPage = browsingJourney.startHandsetFirstJourney(true,true)
        assert handsetListPage.isSubNavigationPanelExist()

        def handsetDetailsPage = handsetListPage.selectDeviceByBrandAndModel("samsung", "s3100")

        TariffsAndExtrasPage tariffsAndExtrasPage = handsetDetailsPage.viewTariffs()
        assert tariffsAndExtrasPage.isSubNavigationPanelExist()

        def basketPage = browsingJourney.navigateToAcquisitionBasketWithRefreshTariff()
        assertTrue(basketPage.isSubNavigationPanelExist())
    }

    /**
     * This test validates the out of stock journey for a phone.  We should be able to progress to the basket
     * Requirements
     * - An out of stock phone can be added to the basket but the basket cannot checkout.
     * - Out of stock message displayed on device details page
     * - Out of stock error message displayed on basket page
     * - Out of stock notification on device tile on basket page
     * - Cannot checkout
     */
    @Test(groups = ["pageIntegrationTest", "precheckin"])
    public void phonePayGoOutOfStockJourney() {
        DeviceListingsPage handsetListPage = browsingJourney.startHandsetFirstJourney(true,true)

        def handsetDetailsPage = handsetListPage.selectDeviceByBrandAndModel("samsung", "s3100")

        assert handsetDetailsPage.stockStatus().outOfStock
       
        TariffsAndExtrasPage tariffsAndExtrasPage = handsetDetailsPage.viewTariffs()
        tariffsAndExtrasPage.selectBigTalkerTariffTile("25")

        BasketPage basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()

        assertTrue(!basketPage.isCheckoutAllowed())
        assertTrue(basketPage.getErrorMessages().contains(OUT_OF_STOCK))
        assertTrue(basketPage.getDevice().getErrors().contains("Out of stock."))
    }

    /**
     * This test is the completely normal journey.
     */
    @Test(groups = "pageIntegrationTest")
    public void vanilla() {
        TariffsAndExtrasPage tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("samsung", "standard")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .selectInsuranceTile(0)
                .selectAccessoryTile(3)

        String insuranceName = tariffsAndExtrasPage.getNameForInsuranceTile(0)
        String insuranceDescription = tariffsAndExtrasPage.getDescriptionForInsuranceTile(0)

        String accessoryName = tariffsAndExtrasPage.getNameForAccessoryTile(3)

        BasketPage basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()

        basketPage.insurances[0].with {
            assert it.title == insuranceName
            assert it.details == insuranceDescription
        }

        basketPage.accessories[0].with {
            assert it.title == accessoryName
        }
    }

    /**
     * This test validates the out of stock journey for a phone.  We should be able to progress to the basket
     *
     * Requirements
     * - An out of stock phone can be added to the basket but the basket cannot checkout.
     * - Out of stock message displayed on device details page
     * - Out of stock error message displayed on basket page
     * - Out of stock notification on device tile on basket page
     * - Cannot checkout
     */
    @Test(groups = ["pageIntegrationTest", "precheckin"])
    public void phonePayMonthlyOutOfStockJourney() {
        def handsetDetailsPage = browsingJourney.startHandsetFirstJourney()
                .selectDeviceByBrandAndModel("samsung", "Mini CCA Out Of Stock")

        assert handsetDetailsPage.stockStatus().isOutOfStock()

        TariffsAndExtrasPage tariffsAndExtrasPage = handsetDetailsPage.viewTariffs()
                .selectPayMonthlyTariffTile(0)

        BasketPage basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()

        assertTrue(!basketPage.isCheckoutAllowed())
        assertTrue(basketPage.getErrorMessages().contains(OUT_OF_STOCK))
        assertTrue(basketPage.getDevice().getErrors().contains("Out of stock."))
    }

    /**
     * This test validates that I can journey between handset details and tariffs and extras while maintaining my variant selection.
     *
     * Requirements
     *  - ECOM-2932 - variant selection should persist between pages.
     *  - deeplinking works for tariffs and extras
     *  - back to product details works on tariffs and extras and maintains variant selection.
     *
     */
    @Test(groups = "acquisitionShopTest")
    public void variantSelectionJourney() {
        DeviceDetailsPage deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true,true)
                .selectDeviceByBrandAndModel("apple", "iphone 5")

        assertThat(deviceDetailsPage, is(aDeviceDetailsPage()
                .with(aVariant()
                .with16GBAvailable()
                .with32GBAvailable()
                .with16GBSelected()
                .withColourSelected("Black")
                .withAvailableColours(["Black", "Gold", "Yellow", "Silver", "Space Gray", "White"]))
        ))

        //choose a non lead variant
        deviceDetailsPage.selectColourByName("Gold")
        deviceDetailsPage.selectCapacityByName("32GB")

        TariffsAndExtrasPage tariffsAndExtrasPage = deviceDetailsPage.viewTariffs()

        assert tariffsAndExtrasPage.selectedColourText == "Gold"
        assert tariffsAndExtrasPage.selectedCapacity == "32GB"

        tariffsAndExtrasPage.selectColour("White")
        tariffsAndExtrasPage.selectMemory("64GB")

        deviceDetailsPage = tariffsAndExtrasPage.clickOnBackToProductDetails()

        assertThat(deviceDetailsPage, is(aDeviceDetailsPage()
                .with(aVariant()
                .with16GBSelected()
                .withColourSelected("Black"))
        ))
    }

    @Test(groups = ["acquisitionShopTest", "precheckin", "mobileAcquisitionShopTest"])
    public void validatePayAsYouGoJourney() {

        def deviceFamily = FixturesInspector.aPhoneFamily()
                .withPAYGPlans()
                .withAccessories(2)
                .build().first()

        def device = deviceFamily.leadModel
        def prepayPlan = device.prepayPlans.first()
        def accessories = device.accessories

        def basketPage = browsingJourney.startHandsetFirstJourney(true,true)
                .selectDeviceByBrandAndModel(device.data.brand, device.data.modelFamily)
                .viewTariffs().viewPayAsYouGoTariffs()
                .selectPrepayPlan(prepayPlan)
                .selectAccessory(accessories[0])
                .selectAccessory(accessories[1])
                .clickOnContinueToBasketButton()

        def expectedUpfrontCost = new PagePrice((device.getCostsWithPlan(prepayPlan)[0].oneOff +
                accessories[0].price.oneOff + accessories[1].price.oneOff) / 100)

        //ECOM-6030
        // insurance cannot be added to payg tariff.. so no upsell insurances expected
        assertEquals(basketPage.insurances.size(), 0)
        assertEquals(basketPage.accessories.size(), 2)
        assertEquals(basketPage.checkoutAllowed, true, "Expected checkout to be allowed but was disabled.")
        assertEquals(basketPage.upfrontTotal, expectedUpfrontCost.unformattedStringValue)
        assertEquals(basketPage.monthlyTotal, "0.00")
    }

    @Test(groups = ["acquisitionShopTest", "precheckin"])
    public void validateSelectStoreJourneyWithClickAndCollectVariant() {
        def deviceFamily = FixturesInspector.aPhoneFamily()
                .withPostpayPlans()
                .withAccessories(2)
                .build().first()

        def device = deviceFamily.leadModel
        def plan = device.postPayPlans[0]
        def deviceCost = device.getCostsWithPlan(plan).find { it.monthly > 0 }

        def deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "G3")
                .selectColourByName("Black").selectCapacityByName("32GB")
                .openCheckStockInStoreOverlay()
                .searchStoresIn('SL1')

        def storeTileElements = deviceDetailsPage.getStoreTileElements()
        assert deviceDetailsPage.isStoreAvailableForClickAndCollectNow(storeTileElements.get(0));
        assert deviceDetailsPage.isStoreAvailableForClickAndCollectNow(storeTileElements.get(1));
        assert deviceDetailsPage.isStoreAvailableForClickAndCollect(storeTileElements.get(2));
        assert deviceDetailsPage.isStoreAvailableForClickAndCollect(storeTileElements.get(3));
        assert deviceDetailsPage.isStoreAvailableForClickAndCollectNow(storeTileElements.get(4));
        assert deviceDetailsPage.isStoreNotEligibleForCollection(storeTileElements.get(5));

       deviceDetailsPage = deviceDetailsPage.selectStoreByIndex(0);

       assert(deviceDetailsPage.isCollectionInfoContains("from O2 Slough - 0123 Mall"))
       assertEquals(deviceDetailsPage.checkStockInStoreLinkText, "Change Store?")

        def basketPage = deviceDetailsPage.viewTariffs().viewPayMonthlyTariffs()
                .selectRefreshPlan(plan, [oneOff: deviceCost.oneOff, monthly: deviceCost.monthly])
                .selectInsuranceTile(0)
                .selectAccessoryTile(0)
                .selectAccessoryTile(1)
                .clickOnContinueToBasketButton()

        def delivery = basketPage.getDelivery()
        assertEquals(delivery.upfrontCost, "Free")
        assertEquals(delivery.monthlyCost, "")
        assert (delivery.collectFromInfo.contains("from Slough - 0123 Mall. Change Store?"))
        assert basketPage.verifyClickAndCollectNowCollectionTile()
    }

    @Test(groups = ["acquisitionShopTest", "precheckin"])
    public void "validate click and collect journey for Delayed Delivery Device"() {

        def deviceFamily = FixturesInspector.aPhoneFamily()
                .withPostpayPlans()
                .withAccessories(2)
                .build().first()

        def device = deviceFamily.leadModel
        def plan = device.postPayPlans[0]
        def deviceCost = device.getCostsWithPlan(plan).find { it.monthly > 0 }

        def deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "G3")
                .selectColourByName("Silver").selectCapacityByName("128MB")
                .openCheckStockInStoreOverlay()
                .searchStoresIn('SL1')
        def storeTileElements = deviceDetailsPage.getStoreTileElements()
        assert (deviceDetailsPage.isStoreAvailableForClickAndCollectNow(storeTileElements.get(0)));
        assert (deviceDetailsPage.isStoreAvailableForClickAndCollectNow(storeTileElements.get(1)));
        assert (deviceDetailsPage.isStoreAvailableForDelayedDeliveryClickAndCollect(storeTileElements.get(2)));
        assert (deviceDetailsPage.isStoreAvailableForDelayedDeliveryClickAndCollect(storeTileElements.get(3)));
        assert (deviceDetailsPage.isStoreAvailableForClickAndCollectNow(storeTileElements.get(4)));
        assert (deviceDetailsPage.isStoreNotEligibleForCollection(storeTileElements.get(5)));

        deviceDetailsPage.selectStoreByIndex(2);

        //then cnc now message should be visible
        assertFalse(deviceDetailsPage.isCNCNowMessageVisible)


        assert (deviceDetailsPage.collectionInfo.equals("Collect in Up to 3 weeks from O2 Slough - 0789 Mall."))
        assertEquals(deviceDetailsPage.checkStockInStoreLinkText, "Change Store?")

        def tariffsAndExtrasPage = deviceDetailsPage.viewTariffs().viewPayMonthlyTariffs()
                .selectRefreshPlan(plan, [oneOff: deviceCost.oneOff, monthly: deviceCost.monthly])
                .selectInsuranceTile(0)
                .selectAccessoryTile(0)
                .selectAccessoryTile(1)

        assert (tariffsAndExtrasPage.storeInfo.contains("Click and collect in Up to 3 weeks from O2 Slough - 0789 Mall."))

        def basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()


        def delivery = basketPage.getDelivery()
        assertEquals(delivery.upfrontCost, "Free")
        assertEquals(delivery.monthlyCost, "")
        assert (delivery.collectFromInfo.equals("Collect from Slough - 0789 Mall, check delivery page in checkout for more details. Change Store?"))

    }

    @Test(groups = ["acquisitionShopTest", "precheckin"])
    public void validateSelectStoreShouldNotDisplayedWhenClickAndCollectDisabledVariantSelected() {

        def deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "G3")
                .selectColourByName("Yellow")

        assert (!deviceDetailsPage.isCnCLinkDisplayed())
    }

    @Test(groups = ["acquisitionShopTest", "precheckin"])
    public void validateSelectStoreJourneyWithClickAndCollectNowVariant() {

        def deviceFamily = FixturesInspector.aPhoneFamily()
                .withPostpayPlans().withAccessories(2)
                .build().first()

        def device = deviceFamily.leadModel
        def plan = device.postPayPlans[0]
        def deviceCost = device.getCostsWithPlan(plan).find { it.monthly > 0 }

        //given i select lg g3 and I open check store popup and i select sore 0789
        def deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "G3").selectColourByName("Black")
                .openCheckStockInStoreOverlay()
                .searchStoresIn('SL1')
                .selectStoreByIndex(2)

        //then cnc now message should be visible
        //assertFalse(deviceDetailsPage.isCNCNowMessageVisible)
         deviceDetailsPage.waitUntilStoreIsSelected()

        //link text should and store message should be updated
        assert (deviceDetailsPage.collectionInfo.contains("from O2 Slough - 0789 Mall"))
        assertEquals(deviceDetailsPage.checkStockInStoreLinkText, "Change Store?")

        //when I go to tariff page and click on back to device details link
        def tariffsAndExtrasPage = deviceDetailsPage.viewTariffs()
        deviceDetailsPage = tariffsAndExtrasPage.clickOnBackToProductDetails()

        //then the link and store message should reflect the selected store
        assert (deviceDetailsPage.collectionInfo.contains("from O2 Slough - 0789 Mall"))
        assertEquals(deviceDetailsPage.checkStockInStoreLinkText, "Change Store?")

        //when I open store overlay
        deviceDetailsPage.openCheckStockInStoreOverlay()

        //then I should see the selected store highlighted
        assertEquals(deviceDetailsPage.searchTerm, 'SL1')
        assertEquals(deviceDetailsPage.selectedStoreId, "0789")

        deviceDetailsPage.closeCheckStockInStoreOverlay()

        deviceDetailsPage = deviceDetailsPage.openCheckStockInStoreOverlay()
                .searchStoresIn('SL2')
                .selectStoreByIndex(7)
        assertEquals(deviceDetailsPage.selectedStoreId, "1470")



        assert (deviceDetailsPage.isCollectionInfoContains("TODAY from O2 Slough - 1470 Mall"))
        assertEquals(deviceDetailsPage.checkStockInStoreLinkText, "Change Store?")

        //when i go to tariff page
        tariffsAndExtrasPage = deviceDetailsPage.viewTariffs()

        assertEquals(tariffsAndExtrasPage.allAccessories.size(), 3)
        assertEquals(tariffsAndExtrasPage.getNameForAccessoryTile(0), "O2 In-Car Charger with Sony Ericsson Charger Cable")

        // i should see the same store info as that of device details page
        assert (tariffsAndExtrasPage.storeInfo.contains("TODAY from Slough - 1470 Mall"))

        def basketPage = tariffsAndExtrasPage.viewPayMonthlyTariffs()
                .selectRefreshPlan(plan, [oneOff: deviceCost.oneOff, monthly: deviceCost.monthly])
                .selectInsuranceTile(0)
                .selectAccessoryTile(0)
                .clickOnContinueToBasketButton()



        def delivery = basketPage.getDelivery()
        assert (delivery.hasInStockMessage())
        assertEquals(delivery.upfrontCost, "Free")
        assertEquals(delivery.monthlyCost, "")
        assert (delivery.collectFromInfo.contains(' from Slough - 1470 Mall. Change Store?'))

    }

    @Test(groups = ["acquisitionShopTest", "precheckin"])
    public void "it resets the selected store when the variant is changed on device details page"() {

        //given i select lg g3 and I open check store popup and i select sore 0789
        def deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "G3").selectColourByName("Black")
                .openCheckStockInStoreOverlay()
                .searchStoresIn('SL1')
                .selectStoreByIndex(2)

        //then cnc now message should be visible
        assertFalse(deviceDetailsPage.isCNCNowMessageVisible)


        //link text should and store message should be updated
        assert (deviceDetailsPage.collectionInfo.contains("from O2 Slough - 0789 Mall"))
        assertEquals(deviceDetailsPage.checkStockInStoreLinkText, "Change Store?")

        //when I change the variant
        deviceDetailsPage.selectColourByName("Silver");
        Thread.sleep(7000);

        //then the message and link text should be reset
        assert (deviceDetailsPage.hasNoCollectionInfo())
        assertEquals(deviceDetailsPage.checkStockInStoreLinkText, "Available to click and collect")

        //when I open store overlay
        deviceDetailsPage.openCheckStockInStoreOverlay()

        //then I should see no store is selected
        assertEquals(deviceDetailsPage.searchTerm, 'SL1')
        assertTrue(deviceDetailsPage.hasNoSelectedStore())


    }

    @Test(groups = ["acquisitionShopTest"])
    public void "it shows error message on unsuccessful store search on device details page"() {

        //500 Internal Server from API Gateway
        def deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "G3").selectColourByName("Black")
                .openCheckStockInStoreOverlay()
                .searchStoresIn("WC1A 2LD")

        //then i should see an error message & the update button is disabled
        assert deviceDetailsPage.getFindStoreErrorMessage() == "Click and collect isn't available at the moment. Try again at checkout."
//        assertFalse(deviceDetailsPage.isUpdateStoreButtonEnabled)

        //404 when there no stores found
        deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "G3").selectColourByName("Black")
                .openCheckStockInStoreOverlay()
                .searchStoresIn("WC1E")

        //then i should see an error message & the update button is disabled
        assert deviceDetailsPage.getFindStoreErrorMessage() == "There aren't any stores in your area. Try a different town or postcode."
//        assertFalse(deviceDetailsPage.isUpdateStoreButtonEnabled)

    }

    @Test(groups = ["acquisitionShopTest", "precheckin"])
    public void "it resets the selected store when the variant is changed on tariff page"() {

        //given i select lg g3 and I open check store popup and i select sore 0789
        def deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "G3").selectColourByName("Black")
                .openCheckStockInStoreOverlay()
                .searchStoresIn('SL2')
                .selectStoreByIndex(7)



        //link text should and store message should be updated
        assert (deviceDetailsPage.isCollectionInfoContains("TODAY from O2 Slough - 1470 Mall"))
        assertEquals(deviceDetailsPage.checkStockInStoreLinkText, "Change Store?")

        //when i go to tariff page
        def tariffsAndExtrasPage = deviceDetailsPage.viewTariffs()

        assertEquals(tariffsAndExtrasPage.allAccessories.size(), 3)
        assert tariffsAndExtrasPage.getAccessoryInStockMessageForCnCNow() == "Accessories available in Slough - 1470 Mall"
        assertEquals(tariffsAndExtrasPage.getNameForAccessoryTile(0), "O2 In-Car Charger with Sony Ericsson Charger Cable")

        // i should see the same store info as that of device details page
        assert (tariffsAndExtrasPage.storeInfo.contains("TODAY from Slough - 1470 Mall"))

        //when I change the variant on tariff page
        tariffsAndExtrasPage.selectColour("Silver")

        //then I should see all the accessories for the SILVER variant
        assert tariffsAndExtrasPage.allAccessories.size() == 3

        // and the store collection info should be reset on tariff page
        assertFalse(tariffsAndExtrasPage.isStoreInfoPresent())

        //when I reset the variant to black
        tariffsAndExtrasPage.selectColour("Black")

        //then I should see all the accessories for the BLACK variant
        assert tariffsAndExtrasPage.allAccessories.size() == 3

        //when I go back to device details page
        tariffsAndExtrasPage.clickOnBackToProductDetails()

        //then the message and link text should be reset
        assert (deviceDetailsPage.hasNoCollectionInfo())
        assertEquals(deviceDetailsPage.checkStockInStoreLinkText, "Available to click and collect")

        //when I open store overlay
        deviceDetailsPage.openCheckStockInStoreOverlay()

        //then I should see no store is selected
        assertEquals(deviceDetailsPage.searchTerm, 'SL2')
        assertTrue(deviceDetailsPage.hasNoSelectedStore())

        deviceDetailsPage.closeCheckStockInStoreOverlay()

        deviceDetailsPage.selectColourByName("White")
                .openCheckStockInStoreOverlay()
                .searchStoresIn('SL2')
                .selectStoreByIndex(7)



        //link text should and store message should be updated
        assert (deviceDetailsPage.isCollectionInfoContains("TODAY from O2 Slough - 1470 Mall"))
        assertEquals(deviceDetailsPage.checkStockInStoreLinkText, "Change Store?")

        //when i go to tariff page
        tariffsAndExtrasPage = deviceDetailsPage.viewTariffs()

        assertEquals(tariffsAndExtrasPage.allAccessories.size(), 3)

        tariffsAndExtrasPage.selectColour("Black")

        //then I should see all the accessories for the BLACK variant
        assert tariffsAndExtrasPage.allAccessories.size() == 3
    }

    @Test(groups = ["acquisitionShopTest", "precheckin"])
    public void "it should display stock messages for stock status PreOrder in CNC journey "() {

        def deviceFamily = FixturesInspector.aPhoneFamily()
                .withPostpayPlans().withAccessories(2)
                .build().first()

        def device = deviceFamily.leadModel
        def plan = device.postPayPlans[0]
        def deviceCost = device.getCostsWithPlan(plan).find { it.monthly > 0 }

        def deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "G3").selectColourByName("Silver")
                .openCheckStockInStoreOverlay()
                .searchStoresIn('SL1')
        def storeTileElements = deviceDetailsPage.getStoreTileElements()
        assert (deviceDetailsPage.isStoreAvailableForPreOrderClickAndCollect(storeTileElements.get(0)));
        assert (deviceDetailsPage.isStoreAvailableForPreOrderClickAndCollect(storeTileElements.get(1)));
        assert (deviceDetailsPage.isStoreAvailableForPreOrderClickAndCollect(storeTileElements.get(2)));
        assert (deviceDetailsPage.isStoreAvailableForPreOrderClickAndCollect(storeTileElements.get(3)));
        assert (deviceDetailsPage.isStoreNotEligibleForCollection(storeTileElements.get(4)));
        assert (deviceDetailsPage.isStoreNotEligibleForCollection(storeTileElements.get(5)));

        deviceDetailsPage.selectStoreByIndex(2)

        //then cnc now message should be visible
        assertFalse(deviceDetailsPage.isCNCNowMessageVisible)

        //link text should and store message should be updated
        assert (deviceDetailsPage.collectionInfo.equalsIgnoreCase("Delivery on 14th February from O2 Slough - 0789 Mall."))
        assertEquals(deviceDetailsPage.checkStockInStoreLinkText, "Change Store?")

        //when I go to tariff page and click on back to device details link
        def tariffsAndExtrasPage = deviceDetailsPage.viewTariffs()

        assertEquals(tariffsAndExtrasPage.allAccessories.size(), 3)
        assertEquals(tariffsAndExtrasPage.getNameForAccessoryTile(0), "O2 In-Car Charger with Sony Ericsson Charger Cable")

        // i should see the same store info as that of device details page
        assert (tariffsAndExtrasPage.storeInfo.equalsIgnoreCase("Delivery on 14th February from Slough - 0789 Mall."))

        def basketPage = tariffsAndExtrasPage.viewPayMonthlyTariffs()
                .selectRefreshPlan(plan, [oneOff: deviceCost.oneOff, monthly: deviceCost.monthly])
                .selectInsuranceTile(0)
                .selectAccessoryTile(0)
                .clickOnContinueToBasketButton()



        def delivery = basketPage.getDelivery()
        assertFalse(delivery.hasInStockMessage())
        assertEquals(delivery.upfrontCost, "Free")
        assertEquals(delivery.monthlyCost, "")
        assert (delivery.collectFromInfo.equalsIgnoreCase('Collect from Slough - 0789 Mall, check delivery page in checkout for more details. Change Store?'))
        assert basketPage.verifyClickAndCollectCollectionTile()

    }

    @Test(groups = ["acquisitionShopTest"])
    public void "it should display appropriate accessory promo and accessories based on the merchandising configuration"() {
        def tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true)
            .selectDeviceByBrandAndModel("LG", "G3").selectColourByName("White").viewTariffs()

        assertTrue(tariffsAndExtrasPage.accessoryPromoDisplayed())
        assertTrue(tariffsAndExtrasPage.accessoryPromoIsOfSingleSize())
        assertTrue(tariffsAndExtrasPage.getAccessoryPromoText().contains("The stunning iPhone 6S"))

        def accessories = [
                            [accessoryName:"Logitech V-10 Speakers",promotionalText:""],
                            [accessoryName:"Sennheiser MM100 Bluetooth Stereo Headset",promotionalText:""],
                            [accessoryName:"O2 In-Car Charger with Sony Ericsson Charger Cable",promotionalText:""]
                        ]
        validateAccessorySectionPromotionalContents(tariffsAndExtrasPage, accessories, 'The stunning iPhone 6S')

        tariffsAndExtrasPage.selectColour("Black")
        accessories = [
                [accessoryName:"O2 In-Car Charger with Sony Ericsson Charger Cable",promotionalText:"now £2.99"],
                [accessoryName:"Sennheiser MM100 Bluetooth Stereo Headset",promotionalText:"get it for free"],
                [accessoryName:"Logitech V-10 Speakers",promotionalText:""],
        ]
        validateAccessorySectionPromotionalContents(tariffsAndExtrasPage, accessories, 'The stunning iPhone 6S')

        tariffsAndExtrasPage.selectPayMonthlyTariffTile(3)

        accessories = [
                [accessoryName: "O2 In-Car Charger with Sony Ericsson Charger Cable", promotionalText: "now £2.99"],
                [accessoryName:"Sennheiser MM100 Bluetooth Stereo Headset",promotionalText:"get it for free"],
                [accessoryName:"Logitech V-10 Speakers",promotionalText:""]
        ]
        validateAccessorySectionPromotionalContents(tariffsAndExtrasPage, accessories, 'The stunning iPhone 6S')

        tariffsAndExtrasPage.selectConnectivity("Wifi")
        accessories = [
                [accessoryName:"O2 In-Car Charger with Sony Ericsson Charger Cable",promotionalText:"now £2.99"],
                [accessoryName:"Sennheiser MM100 Bluetooth Stereo Headset",promotionalText:"now £9.99"],
                [accessoryName:"Logitech V-10 Speakers",promotionalText:""]
        ]
        validateAccessorySectionPromotionalContents(tariffsAndExtrasPage, accessories, 'The stunning iPhone 6S', true)
    }

    private void validateAccessorySectionPromotionalContents(TariffsAndExtrasPage tariffsAndExtrasPage,def accessoriesToMatch, String promoContent, boolean doubleSizePromo = false) {
        assertTrue(tariffsAndExtrasPage.accessoryPromoDisplayed())
        if (doubleSizePromo) {
            assertTrue(tariffsAndExtrasPage.accessoryPromoIsOfDoubleSize())
        } else {
            assertTrue(tariffsAndExtrasPage.accessoryPromoIsOfSingleSize())
        }
        assertTrue(tariffsAndExtrasPage.getAccessoryPromoText().contains(promoContent))

        def accessories = tariffsAndExtrasPage.accessoriesSection().accessories()
        assert accessories.size() == accessoriesToMatch.size()
        accessoriesToMatch.eachWithIndex { accessoryMap, int index ->
            assert accessories[index].accessoryName == accessoryMap.accessoryName
            assert accessories[index].promotionalText == accessoryMap.promotionalText
        }
    }

    @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
    public void validateBasketCostsAreConsistentWithTariffAndExtrasCosts() {
        def deviceFamily = FixturesInspector.aPhoneFamily()
                .withPostpayPlans()
                .withAMonthlyDeviceCost()
                .withAccessories()
                .withInsurances()
                .build().first()

        def variant = deviceFamily.leadModel
        def plan = variant.postPayPlans[0]
        def deviceCost = variant.getCostsWithPlan(plan).find { it.monthly > 0 }


        def tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true,true)
                .selectDeviceByBrandAndModel(variant.data.brand, variant.data.modelFamily)
                .viewTariffs().viewPayMonthlyTariffs()
                .selectRefreshPlan(plan, [oneOff: deviceCost.oneOff, monthly: deviceCost.monthly])
                .selectInsuranceTile(0)
                .selectAccessoryTile(0)
                .selectAccessoryTile(1)

        PackageSection packageSection = tariffsAndExtrasPage.getPackageSection()
        String deviceUpfrontCost = packageSection.deviceUpfrontCost
        String deviceMonthlyCost = packageSection.deviceMonthlyCost
        String insuranceMonthlyCost = packageSection.insuranceMonthlyCost
        String accessoriesUpfrontCost = packageSection.accessoriesTotalUpfrontCost
        String totalUpfrontCost = packageSection.totalUpfrontCost
        String totalMonthlyCost = packageSection.totalMonthlyCost

        BasketPage basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()


        def device = basketPage.getDevice()
        assertEquals(device.getUpfrontCost(), deviceUpfrontCost)
        assertEquals(device.getMonthlyCost(), deviceMonthlyCost)
        assertEquals(basketPage.calculateUpfrontPriceFromBasketItems(basketPage.getAccessories()).toString(), accessoriesUpfrontCost)
        assertEquals(basketPage.getInsurances().get(0).getMonthlyCost(), insuranceMonthlyCost)
        assertEquals(basketPage.getUpfrontTotal(), totalUpfrontCost)
        assertEquals(basketPage.getMonthlyTotal(), totalMonthlyCost)
    }

    @Test(groups = "pageIntegrationTest")
    public void multipleAccessoriesOnTariffPageCarryOverToBasketPageAndCanBeRemoved() {

        def deviceFamily = FixturesInspector.aPhoneFamily()
                .withPAYGPlans()
                .withAccessories(4)
                .build().first()

        def device = ([] << deviceFamily.leadModel << deviceFamily.variants).find {
            it.accessories.size() >= 4
        }
        def plan = device.prepayPlans.first()

        def basketPage = browsingJourney.startHandsetFirstJourney(true,true)
                .viewPayAsYouGo()
                .selectDeviceByBrandAndModel(device.data.brand, device.data.modelFamily)
                .selectColourByName(device.data.familyVariant.colour)
                .selectCapacityByName(device.data.familyVariant.memory)
                .viewTariffs()
                .selectPrepayPlan(plan)
                .selectAccessoryTile(0)
                .selectAccessoryTile(1)
                .selectAccessoryTile(2)
                .selectAccessoryTile(3)
                .clickOnContinueToBasketButton()

        assertTrue(basketPage.accessories.size() == 4)
        basketPage.accessories.pop().remove()
        basketPage.accessories.pop().remove()
        assertTrue(basketPage.accessories.size() == 2)
    }

    @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
    public void addHandsetThenEmptyBasket() {
        def basketPage = browsingJourney.navigateToAcquisitionBasketWithRefreshTariff(true)
        assert basketPage.getDevice()
        assert basketPage.getTariff()

        basketPage = basketPage.emptyBasket()

        assertEquals(basketPage.basketItems.size(), 0)
        assertTrue(basketPage.isContinueShoppingAllowed())
        assertTrue(basketPage.verifyTheEmptyBasketMessage())
    }

    @Test(groups = "acquisitionShopTest")
    public void addHandsetThroughRecommendedTariff() {
        def basketPage = browsingJourney
                .startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("Apple", "iPhone 5S")
                .recommendedTariff()
                .clickOnAddDeviceTariffToBasketInRecommendedSection()
                .clickOnContinueToBasketButton()

        assertTrue(basketPage.getMonthlyTotal() == "30.00")
        assertTrue(basketPage.getUpfrontTotal() == "239.99")
    }

    @Test(groups = "acquisitionShopTest")
    public void addTabletThroughRecommendedTariff() {
        def basketPage = browsingJourney
                .startTabletFirstJourney(true)
                .selectDeviceByBrandAndModel("Google", "Nexus 7")
                .recommendedTariff()
                .clickOnAddDeviceTariffToBasketInRecommendedSection()
                .clickOnContinueToBasketButton()

        assertTrue(basketPage.getMonthlyTotal() == "25.00")
        assertTrue(basketPage.getUpfrontTotal() == "20.20")
    }

    @Test(groups = "acquisitionShopTest")
    public void shouldBeAbleToBuyFitnessBands() {
        def basketpage = browsingJourney
                .navigateToDeviceDetailsPage("sony", "smartband-talk")
                .viewTariffs()
                .selectPayMonthlyTariffTile(1)
                .clickOnContinueToBasketButton()
        assertTrue(basketpage.getMonthlyTotal() == "50.00")
        assertTrue(basketpage.getUpfrontTotal() == "89.99")
        assertTrue(basketpage.getDevice().getDeviceName().equals("Sony SmartBand Talk"))

    }

    @Test(groups = "acquisitionShopTest")
    public void "should be able to buy smartwatches"() {
        def basketpage = browsingJourney
                .navigateToDeviceDetailsPage("apple", "iwatch")
                .viewTariffs()
                .selectPayMonthlyTariffTile(1)
                .clickOnContinueToBasketButton()
        assertTrue(basketpage.getMonthlyTotal() == "42.00")
        assertTrue(basketpage.getUpfrontTotal() == "119.99")
        assertTrue(basketpage.getDevice().getDeviceName().equals("Apple iWatch"))

    }

    @Test(groups = "acquisitionShopTest")
    public void addHandsetThroughViewAllTariffsInRecommendedTariff() {
        def basketPage = browsingJourney
                .startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("Apple", "iPhone 5S")
                .recommendedTariff().clickOnViewAllTariffsInRecommendedSection().selectPayMonthlyTariffTile(0).clickOnContinueToBasketButton()

        assertTrue(basketPage.getMonthlyTotal() == "30.00")
        assertTrue(basketPage.getUpfrontTotal() == "239.99")
    }

    @Test(groups = "acquisitionShopTest")
    public void addTabletThroughViewAllTariffsInRecommendedTariff() {
        def basketPage = browsingJourney
                .startTabletFirstJourney(true)
                .selectDeviceByBrandAndModel("Google", "Nexus 7")
                .recommendedTariff().clickOnViewAllTariffsInRecommendedSection().selectPayMonthlyTariffTile(1).clickOnContinueToBasketButton()

        assertTrue(basketPage.getMonthlyTotal() == "35.00")
        assertTrue(basketPage.getUpfrontTotal() == "10.20")
    }

    //ECOM-9053 Display promotion ribbon in basket
    @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
    public void addInsuranceVerifyOverlayAndRemoveFromBasket() {
        def tariffsAndExtrasPage = browsingJourney
                .startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("Samsung", "Standard")
                .viewTariffs()
                .selectPayMonthlyTariffTile(1)
                .selectInsuranceTileByName('Generic Handset Insurance')

        int insuranceIndex = 0
        String insuranceName = tariffsAndExtrasPage.getNameForInsuranceTile(insuranceIndex)
        String insuranceDescription = tariffsAndExtrasPage.getDescriptionForInsuranceTile(insuranceIndex)
        PackageSection packageSection = tariffsAndExtrasPage.getPackageSection()
        assertEquals(packageSection.getPromotion(), "Tariff upsell promotional attachment content appears here")
        assertEquals(packageSection.getAdditionalPromotion(), "Upgrade after 12 months and we'll guarantee to buy out your device")
        def basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()
        assertEquals(basketPage.getPromotion(), "Tariff upsell promotional attachment content appears here")
        assertEquals(basketPage.getAdditionalPromotion(), "Upgrade after 12 months and we'll guarantee to buy out your device")

        def basketItems = basketPage.basketItems
        assertTrue(basketItems.any {
            it.getTitle() == insuranceName
        })

        assertTrue(basketItems.any {
            it.getDetails() == insuranceDescription
        })

        def insurances = basketPage.insurances
        assertEquals(insurances.size(), 1)

        assertEquals(insurances[0].insuranceOverlay.dataId, "o2GenericHandsetInsuranceAcquisition")

        insurances[0].remove()

        assertTrue(basketPage.basketItems.any {
            it.getTitle() == insuranceName
        })
    }

    //ECOM-4704
    @Test(groups = "acquisitionShopTest")
    public void validateViewRepresentativeExampleLinkAndMoreDetailsLinkAreAbsentForPayNGoMobile() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("lg", "gw300")
                .viewTariffs()
                .selectBigBundleTariffTile()
                .selectAccessoryTile("8feedaa2-2396-44bf-8084-38024e3c522e")
                .clickOnContinueToBasketButton()
        assertFalse(basketPage.isViewRepresentativeExampleLinkDisplayed())
        assertFalse(basketPage.isMoreDetailsLinkForTariffsDisplayed())
    }

    //ECOM-4704
    @Test(groups = "acquisitionShopTest")
    public void validateViewRepresentativeExampleLinkAndMoreDetailsLinkAreAbsentForPayNGoTablets() {
        def basketPage = browsingJourney.startTabletFirstJourney(true,false, "payasyougo")
                .selectDeviceByBrandAndModel("tesco", "dummy-air-2")
                .viewTariffs().selectPayAndGoTariffTile(0)
                .clickOnContinueToBasketButton()
        assertFalse(basketPage.isViewRepresentativeExampleLinkDisplayed())
        assertFalse(basketPage.isMoreDetailsLinkForTariffsDisplayed())
    }

    //ECOM-4704
    @Test(groups = "acquisitionShopTest")
    public void validateMoreDetailsLinkShouldOpenOverlayForVoiceTariff() {
        def basketPage = browsingJourney.navigateToTariffsAndExtrasPage("samsung", "standard").selectPayMonthlyTariffTile(0).clickOnContinueToBasketButton()
        assertTrue(basketPage.isViewRepresentativeExampleLinkDisplayed())

        def tariffDetailsOverlay = basketPage.tariff.overlay
        assertEquals(tariffDetailsOverlay.dataId, 'o2PayMonthlyChargesVoiceAcquisition')
        assertEquals(tariffDetailsOverlay.isOpen(), true, "expected the voice tariff detail overlay to be open but was closed")
        // For tariffs configured with dynamic promo content, overlay is opened with additional content along with dynamic representative example
        assertNull(tariffDetailsOverlay.getDynamicPromoContentDataId('voice'))
        def dynamicRepresentativeExample = tariffDetailsOverlay.getDynamicRepresentativeExample('voice')
        def expectedRepresentativeExampleValues = ["Duration of agreement" : "24 months",
                                                   "Device cash price"     : "£699.99",
                                                   "Upfront cost"          : "£329.99",
                                                   "Credit amount"         : "£360.00",
                                                   "Interest rate (fixed)" : "0%",
                                                   "Representative APR"    : "0%",
                                                   "Monthly device payment": "£15.00",
                                                   "Device amount payable" : "£689.99"]

        dynamicRepresentativeExample.validateAgainstHash(expectedRepresentativeExampleValues)
        tariffDetailsOverlay.close()
        assertEquals(tariffDetailsOverlay.isOpen(), false, "expected the voice tariff detail overlay to be closed but was open")

        basketPage = basketPage.clickOnChangeTariff().selectPayMonthlyTariffTile(3).clickOnContinueToBasketButton()
        assertTrue(basketPage.isViewRepresentativeExampleLinkDisplayed())
        tariffDetailsOverlay = basketPage.tariff.overlay
        assertEquals(tariffDetailsOverlay.dataId, 'o2PayMonthlyChargesVoiceAcquisition')
        assertEquals(tariffDetailsOverlay.isOpen(), true, "expected the voice tariff detail overlay to be open but was closed")

        assertNull(tariffDetailsOverlay.getDynamicPromoContentDataId('voice'))
        dynamicRepresentativeExample = tariffDetailsOverlay.getDynamicRepresentativeExample('voice')

        expectedRepresentativeExampleValues = ["Duration of agreement" : "24 months",
                                               "Device cash price"     : "£699.99",
                                               "Upfront cost"          : "£219.99",
                                               "Credit amount"         : "£480.00",
                                               "Interest rate (fixed)" : "0%",
                                               "Representative APR"    : "0%",
                                               "Monthly device payment": "£20.00",
                                               "Device amount payable" : "£699.99"]
        dynamicRepresentativeExample.validateAgainstHash(expectedRepresentativeExampleValues)

        tariffDetailsOverlay.close()
        assertEquals(tariffDetailsOverlay.isOpen(), false, "expected the voice tariff detail overlay to be open but was closed")
    }

    //ECOM-4704
    @Test(groups = "acquisitionShopTest")
    public void validateMoreDetailsLinkShouldOpenOverlayForDataTariff() {
        def basketPage = browsingJourney.navigateToTariffsAndExtrasPage("tesco", "dummy-air-2").selectPayMonthlyTariffTile(1).clickOnContinueToBasketButton()
        assertTrue(basketPage.isViewRepresentativeExampleLinkDisplayed())
        def tariffDetailsOverlay = basketPage.tariff.overlay
        assertEquals(tariffDetailsOverlay.isOpen(), true, "expected the tariff detail overlay to be open but was closed")
        assertEquals(tariffDetailsOverlay.dataId, 'o2PayMonthlyChargesDataAcquisition')
        assertNull(tariffDetailsOverlay.getDynamicPromoContentDataId('data'))

        // Verify that for tariffs configured with dynamic promo content, overlay is opened with additional content

        def expectedRepresentativeExampleValues = ["Duration of agreement" : "24 months",
                                                   "Device cash price"     : "£499.00",
                                                   "Upfront cost"          : "£100.00",
                                                   "Credit amount"         : "£240.00",
                                                   "Interest rate (fixed)" : "0%",
                                                   "Representative APR"    : "0%",
                                                   "Monthly device payment": "£10.00",
                                                   "Device amount payable" : "£340.00"]

        def dynamicRepresentativeExample = tariffDetailsOverlay.getDynamicRepresentativeExample('data')
        dynamicRepresentativeExample.validateAgainstHash(expectedRepresentativeExampleValues)
        tariffDetailsOverlay.close()
        assertEquals(tariffDetailsOverlay.isOpen(), false, "expected the tariff detail overlay to be closed but was open")

        basketPage = basketPage.clickOnChangeTariff().selectPayMonthlyTariffTile(0).clickOnContinueToBasketButton()
        assertTrue(basketPage.isViewRepresentativeExampleLinkDisplayed())
        tariffDetailsOverlay = basketPage.tariff.overlay
        assertEquals(tariffDetailsOverlay.isOpen(), true, "expected the insurance overlay to be open but was closed")
        assertEquals(tariffDetailsOverlay.dataId, 'o2PayMonthlyChargesDataAcquisition')
        assertTrue(tariffDetailsOverlay.getDynamicPromoContentDataId('data').contains('o2PayMonthlyDataTariffFirstPromotionAcquisition'))

        dynamicRepresentativeExample = tariffDetailsOverlay.getDynamicRepresentativeExample('data')
        expectedRepresentativeExampleValues = ["Duration of agreement" : "24 months",
                                               "Device cash price"     : "£499.00",
                                               "Upfront cost"          : "£50.20",
                                               "Credit amount"         : "£360.00",
                                               "Interest rate (fixed)" : "0%",
                                               "Representative APR"    : "0%",
                                               "Monthly device payment": "£15.00",
                                               "Device amount payable" : "£410.20"]
        dynamicRepresentativeExample.validateAgainstHash(expectedRepresentativeExampleValues)
        tariffDetailsOverlay.close()
        assertEquals(tariffDetailsOverlay.isOpen(), false, "expected the insurance overlay to be closed but was open")
    }

    // Ecom-4510
    @Test(groups = "acquisitionShopTest")
    public void verifyDescriptionOfInsuranceTypeDamageOnly() {
        def page = browsingJourney
                .startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("lg", "g3")
                .selectColourByName("Yellow")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .selectInsuranceTileByName('O2 Insurance – Damage Only')

        int insuranceIndex = 1
        String insuranceName = page.getNameForInsuranceTile(insuranceIndex)
        String insuranceDescription = page.getDescriptionForInsuranceTile(insuranceIndex)
        def basketPage = page.clickOnContinueToBasketButton()


        assertTrue(basketPage.basketItems.get(2).any {
            it.getTitle() == insuranceName
        })

        assertTrue(basketPage.basketItems.get(2).any {
            it.getDetails() == insuranceDescription
        })
    }


    @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
    public void addInsuranceAndVerifyOverlayForTabletInsurance() {
        def basketPage = browsingJourney.navigateToDeviceDetailsPage("samsung", "tab3")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .selectInsuranceTileByName('Premium Handset Insurance')
                .clickOnContinueToBasketButton()

        def insurances = basketPage.insurances
        assertEquals(insurances.size(), 1)
        assertEquals(insurances[0].insuranceOverlay.dataId, "o2TabletInsuranceAcquisition")
        //assertEquals(insurances[1].insuranceOverlay.dataId, "o2TabletInsuranceAcquisition")
    }

    @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
    public void addInsuranceAndVerifyOverlayForDamageOnlyAndGenericInsurance() {
        def basketPage = browsingJourney.navigateToDeviceDetailsPage("apple", "mini-cca5")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .selectInsuranceTileByName('O2 Insurance – Damage Only')
                .clickOnContinueToBasketButton()

        def insurances = basketPage.insurances
        assertEquals(insurances.size(), 1)
        //assertEquals(insurances[0].insuranceOverlay.dataId, "o2GenericHandsetInsuranceAcquisition")
        assertEquals(insurances[0].insuranceOverlay.dataId, "o2DamageOnlyInsuranceAcquisition")
    }


    @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
    public void changeTariffOnBasketPagePayM() {
        def postPayFamilyWithAccessoriesAndInsurance = FixturesInspector.aPhoneFamily()
                .withPostpayPlans()
                .withAccessories()
                .withInsurances()
                .build().first()

        def variant = postPayFamilyWithAccessoriesAndInsurance.leadModel
        def plan = variant.postPayPlans[0]
        def deviceCost = variant.getCostsWithPlan(plan).first()
        def variantMinimumCost = variant.minimumCost


        def tariffs = browsingJourney.startHandsetFirstJourney(true,true)
                .selectDeviceByBrandAndModel(variant.data.brand, variant.data.modelFamily)
                .viewTariffs().viewPayMonthlyTariffs()

        def plan1 = tariffs
                .selectFullUpfrontPlan(2)
        def tariffsAndExtrasPage = plan1
                .selectAccessoryTile(0)
                .selectInsuranceTile(0)


        def basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()
        tariffsAndExtrasPage = basketPage.clickOnChangeTariff()
        assertEquals(tariffsAndExtrasPage.getMakeAndModel(), "${variant.data.brand} ${variant.data.modelFamily}")

        assertEquals(tariffsAndExtrasPage.getPricingSummaryFromHeader(),
                "From ${new PagePrice(variantMinimumCost.oneOff / 100).formattedStringValue} upfront cost and " +
                        "${new PagePrice(variantMinimumCost.monthly / 100).formattedStringValue} a month")
        assertEquals(tariffsAndExtrasPage.getSelectedAccessories().size(), 1)
        assert !tariffsAndExtrasPage.isInsuranceSectionVisible()
    }

    //ECOM-2989
    @Test(groups = "acquisitionShopTest")
    public void changeTariffOnBasketPagePayG() {

        def tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "GW300")
                .viewTariffs()
                .selectBigBundleTariffTile()
                .selectAccessoryTile(0)

        def basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()
        tariffsAndExtrasPage = basketPage.clickOnChangeTariff()

        assertEquals(tariffsAndExtrasPage.getMakeAndModel(), "LG GW300")
        assertEquals(tariffsAndExtrasPage.getPricingSummaryFromHeader(), "£100.00 upfront cost")
        assertEquals(tariffsAndExtrasPage.getSelectedAccessories().size(), 1)
        assertEquals(tariffsAndExtrasPage.getSelectedAccessories()[0], "402a1373-c0da-4ec9-b901-7060e59ff666")

    }

    @Test(groups = "acquisitionShopTest")
    public void verifyTariffNameForPrePayTariff() {

        //Big Bundle
        def tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "GW300")
                .viewTariffs()
                .selectBigBundleTariffTile()
                .selectAccessoryTile(0)

        assertEquals(tariffsAndExtrasPage.packageSection.payGPlanName, "${BIG_BUNDLES.name} £25 top up")

        def basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()

        String tariffPlan = basketPage.getTariff().tariffPlan();
        assertEquals(tariffPlan, "${BIG_BUNDLES.name} £25 top up")

        //International Sim
        tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true,true)
                .selectDeviceByBrandAndModel("Blackberry", "Q5")
                .viewTariffs()
                .selectPrepayPlan([data : [id : "1f9afeb0-8393-455a-9724-4f602aeeb2r7"]])
                .selectAccessoryTile(0)
        assertEquals(tariffsAndExtrasPage.packageSection.payGPlanName, "${INTERNATIONAL.name} £12 top up")

        basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()

        tariffPlan = basketPage.getTariff().tariffPlan();
        assertEquals(tariffPlan, "${INTERNATIONAL.name} £12 top up")

        //Big Talker
        tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true,true)
                .selectDeviceByBrandAndModel("Samsung", "s3100")
                .viewTariffs()
                .selectPrepayPlan([data : [id : "71464670-0e36-11e2-892e-0800200c9a66"]])
        assertEquals(tariffsAndExtrasPage.packageSection.payGPlanName, "${BIG_TALKER.name} £35 top up")

        basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()

        tariffPlan = basketPage.getTariff().tariffPlan();
        assertEquals(tariffPlan, "${BIG_TALKER.name} £35 top up")
    }

    @Test(groups = "acquisitionShopTest")
    public void verifyTariffSubCategoryDisplayNamePrePayTariff() {

        //Big Talker
        def tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true,true, "payasyougo")
                .selectDeviceByBrandAndModel("Blackberry", "Q5")
                .viewTariffs()
                .verifyBigTalkerTariffCategoryTitle()
                .selectBigTalkerTariffTile("100")
                .selectAccessoryTile(0)

        def basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()

        String tariffPlan = basketPage.getTariff().tariffPlan();
        assertEquals(tariffPlan, "${BIG_TALKER.name} £0 top up")

        //International Sim
        tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true,true, "payasyougo")
                .selectDeviceByBrandAndModel("Blackberry", "Q5")
                .viewTariffs()
                .verifyInternationalTariffCategoryTitle()
                .selectPrepayPlan([data : [id : "1f9afeb0-8393-455a-9724-4f602aeeb2r7"]])
                .selectAccessoryTile(0)

        basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()

        tariffPlan = basketPage.getTariff().tariffPlan();
        assertEquals(tariffPlan, "${INTERNATIONAL.name} £12 top up")

    }

    @Test(groups = ["acquisitionShopTest", "pageIntegrationTest"])
    public void shouldNotBeAbleToAddOutOfStockHandsetToTheBasket() {
        def basketPage = browser().navigateToDeviceListingsPageFor("phones")
                .showAll()
                .selectDeviceByBrandAndModel("Apple", "iPhone 5")
                .selectColourByName("Gold")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .clickOnContinueToBasketButton()

        assertFalse(basketPage.isCheckoutAllowed())
        assertTrue(basketPage.errorMessages.contains(OUT_OF_STOCK))
    }

    @Test(groups = "pageIntegrationTest")
    public void shouldBeAbleToAddMultipleGiftsToTheBasket() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true,true)
                .selectDeviceByBrandAndModel("apple", "delaydelivery")
                .viewTariffs()
                .selectPayMonthlyTariffTile(4)
                .clickOnContinueToBasketButton()

        List<BasketItem> gifts = basketPage.gifts
        assertEquals(2, gifts.size())
        assertFalse(gifts.any { it.canBeRemoved() })
    }

    @Test(groups = ["mobileAcquisitionShopTest"])
    @RunInMobileBrowser
    public void '"canonical link should be available on the details page on mobile"'() {
        def deviceListingPage = browsingJourney.startHandsetFirstJourney(true)
        def deviceDetailsPage = deviceListingPage.showAll().selectDeviceByBrandAndModel("Apple", "iPhone 5S")

        assertThat(deviceDetailsPage, is(aDeviceDetailsPage().withCanonicalLinkSuffix("/phones/apple/iphone-5s/")))

        deviceListingPage = browser().navigateToDeviceListingsPageFor("tablets")
        deviceDetailsPage = deviceListingPage.showAll().selectDeviceByBrandAndModel("Google", "Nexus 7")

        assertThat(deviceDetailsPage, is(aDeviceDetailsPage().withCanonicalLinkSuffix("/tablets/google/nexus-7/")))
    }

    @Test(groups = "pageIntegrationTest")
    public void '"canonical link should be present on the details page"'() {
        def deviceListingPage = browsingJourney.startHandsetFirstJourney(true,true)
        def deviceDetailsPage = deviceListingPage.showAll().selectDeviceByBrandAndModel("Apple", "iPhone 5C")

        assertThat(deviceDetailsPage, is(aDeviceDetailsPage().withCanonicalLinkSuffix("/phones/apple/iphone-5c/")))


        deviceListingPage = browser().navigateToDeviceListingsPageFor("tablets")
        deviceDetailsPage = deviceListingPage.showAll().selectDeviceByBrandAndModel("Google", "Nexus 7")

        assertThat(deviceDetailsPage, is(aDeviceDetailsPage().withCanonicalLinkSuffix("/tablets/google/nexus-7/")))
    }

    @Test(groups = "acquisitionShopTest")
    public void verifyRefreshCcaDetailsInBasketForPayG() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("lg", "gw300")
                .viewTariffs().viewPayAsYouGoTariffs()
                .selectBigBundleTariffTile()
                .selectAccessoryTile(0)
                .clickOnContinueToBasketButton()

        assertFalse(basketPage.getDevice().isMyDevicePlanLinkIsDisplayed());
    }

    @Test(groups = "acquisitionShopTest")
    public void verifyCheckoutButtonIsEnabledWithStockLimitedAccessory() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("samsung", "standard")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .selectAccessoryTile(0)
                .selectAccessoryTile(10)
                .clickOnContinueToBasketButton()

        def accessories = basketPage.getAccessories()
        assertTrue(accessories.size() == 2)
        assertEquals(basketPage.getDevice().deviceName, "Samsung Standard")
        assertEquals(accessories.get(0).getTitle(), "Logitech V-10 Speakers")
        assertEquals(accessories.get(0).getUpfrontCost(), "£70.00")
        assertEquals(accessories.get(1).getTitle(), "O2 In-Car Charger that magically works")
        assertEquals(accessories.get(1).getUpfrontCost(), "£7.50\nWas £10.00")
        Assert.assertTrue("expected checkout to be allowed but it was disabled.", basketPage.isCheckoutAllowed())
    }

    @Test(groups = "pageIntegrationTest")
    public void basketValidation_MBB_PayAndGo_Dongle() {
        def deviceDetailsPage = browsingJourney.navigateToDeviceDetailsPage("huawei", "pay-and-go-e173-usb-modem", true)
        String deviceName = deviceDetailsPage.getDeviceName()
        assertFalse(currentPage().getHeaderText().isEmpty(), "phone brand and model not defined on phones details page")

        TariffsAndExtrasPage tariffPage = deviceDetailsPage.viewTariffs()
        tariffPage.viewPayAsYouGoTariffs()
        assertEquals(tariffPage.getPayAsYouGoGridPlans().size(), 2)
        Plan tariff = tariffPage.getPayAsYouGoGridPlans().get(0)
        String upfrontCost = tariff.getUpfrontAmount()
        tariff.select()

        BasketPage basketPage = tariffPage.clickOnContinueToBasketButton()

        BasketDevice device = basketPage.getDevice()
        assertEquals(device.getTitle(), deviceName)
        assertEquals(new PagePrice(device.getUpfrontCost()).formattedStringValue, upfrontCost)

        BasketTariff basketTariff = basketPage.getTariff()
        assertEquals(basketTariff.marketingMessage(), "This is a prepay mbb plan")
        assertEquals(basketTariff.dataAllowance(), "1GB")
    }

    @Test(groups = "pageIntegrationTest")
    public void basketValidation_MBB_PayAndGo_RefreshDongle() {
        def deviceDetailsPage = browsingJourney.navigateToDeviceDetailsPage("huawei", "e173-postpay-and-prepay", true)
        String deviceName = deviceDetailsPage.getDeviceName()
        assertFalse(currentPage().getHeaderText().isEmpty(), "dongle brand and model not defined on phones details page")

        TariffsAndExtrasPage tariffPage = deviceDetailsPage.viewTariffs()
        tariffPage.viewPayAsYouGoTariffs()
        assertEquals(tariffPage.getPayAsYouGoGridPlans().size(), 1)
        Plan tariff = tariffPage.getPayAsYouGoGridPlans().get(0)
        String upfrontCost = tariff.getUpfrontAmount()
        tariff.select()

        BasketPage basketPage = tariffPage.clickOnContinueToBasketButton()

        BasketDevice device = basketPage.getDevice()
        assertEquals(device.getTitle(), deviceName)
        assertEquals(new PagePrice(device.getUpfrontCost()).formattedStringValue, upfrontCost)

        BasketTariff basketTariff = basketPage.getTariff()
        assertEquals(basketTariff.marketingMessage(), "This is a prepay mbb plan")
        assertEquals(basketTariff.dataAllowance(), "1GB")
    }

    @Test(groups = "pageIntegrationTest")
    public void basketValidation_MBB_PayAndGo_RefreshHotspots() {
        def deviceDetailsPage = browsingJourney.navigateToDeviceDetailsPage("zte-mf59", "o2-pocket-hotspot", true)
        String deviceName = deviceDetailsPage.getDeviceName()
        assertFalse(currentPage().getHeaderText().isEmpty(), "hotspot brand and model not defined on phones details page")

        TariffsAndExtrasPage tariffPage = deviceDetailsPage.viewTariffs()
        tariffPage.viewPayAsYouGoTariffs()
        assertEquals(tariffPage.getPayAsYouGoGridPlans().size(), 1)
        Plan tariff = tariffPage.getPayAsYouGoGridPlans().get(0)
        String upfrontCost = tariff.getUpfrontAmount()
        tariff.select()
        BasketPage basketPage = tariffPage.clickOnContinueToBasketButton()

        BasketDevice device = basketPage.getDevice()
        assertEquals(device.getTitle(), deviceName)
        assertEquals(new PagePrice(device.getUpfrontCost()).formattedStringValue, upfrontCost)

        BasketTariff basketTariff = basketPage.getTariff()
        assertEquals(basketTariff.marketingMessage(), "This is a prepay mbb plan")
        assertEquals(basketTariff.dataAllowance(), "1GB")
    }

    //ECOM-2614: AC11 - Validating checkout information for a customer for a Data only PAYG tariff
    @Test(groups = "pageIntegrationTest")
    public void basketValidation_MBB_PayAndGo_RefreshTablet() {
        def deviceDetailsPage = browsingJourney.navigateToDeviceDetailsPage("samsung", "tab3", true)
        String deviceName = deviceDetailsPage.getDeviceName()
        assertFalse(currentPage().getHeaderText().isEmpty(), "tablet brand and model not defined on phones details page")

        TariffsAndExtrasPage tariffPage = deviceDetailsPage.viewTariffs()
        tariffPage.viewPayAsYouGoTariffs()
        assertEquals(tariffPage.getPayAsYouGoGridPlans().size(), 1)
        Plan tariff = tariffPage.getPayAsYouGoGridPlans().get(0)
        String upfrontCost = tariff.getUpfrontAmount()
        tariff.select()
        BasketPage basketPage = tariffPage.clickOnContinueToBasketButton()

        BasketDevice device = basketPage.getDevice()
        assertEquals(device.getTitle(), deviceName)
        assertEquals(new PagePrice(device.getUpfrontCost()).formattedStringValue, upfrontCost)

        BasketTariff basketTariff = basketPage.getTariff()
        assertEquals(basketTariff.marketingMessage(), "This is a prepay mbb plan")
        assertEquals(basketTariff.dataAllowance(), "1GB")

        def basketPageSummarySection = basketPage.getSummarySection()
        assertTrue(basketPageSummarySection.hasNameAndAddressInfo())
        assertTrue(basketPageSummarySection.hasCardDetailsInfo())
        //Assert "What you will get" section
        assertTrue(basketPageSummarySection.hasSecureCheckoutInfo())
        assertTrue(basketPageSummarySection.hasReturnPolicyInfo())
        assertTrue(basketPageSummarySection.hasProtectInfo())
    }

    @Test(groups = "pageIntegrationTest")
    public void shouldNotBeAbleToAddMoreThan6AccessoriesToTheBasket() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("samsung", "standard")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .selectAccessoryTile(0)
                .selectAccessoryTile(1)
                .selectAccessoryTile(2)
                .selectAccessoryTile(3)
                .selectAccessoryTile(4)
                .selectAccessoryTile(5)
                .selectAccessoryTile(6)
                .clickOnContinueToBasketButton()

        assertTrue(basketPage.accessories.size() == 7)
        assertFalse(basketPage.isCheckoutAllowed())
        assertTrue(basketPage.getErrorMessages().contains(ACCESSORY_LIMITATION))

        // Go back to tariff and extras page and remove one accessory
        basketPage = basketPage.clickOnChangeTariff()
                .selectPayMonthlyTariffTile(0)
                .selectAccessoryTile(0)
                .clickOnContinueToBasketButton()

        // Assert that there are 6 accessories and the user can now checkout with no error message
        def numberOfAccessoriesInBasket = basketPage.getAccessories().size()
        Assert.assertTrue("expected 6 accessories but found " + numberOfAccessoriesInBasket + ".", numberOfAccessoriesInBasket == 6)
        Assert.assertTrue("expected checkout to be allowed but it was disabled.", basketPage.isCheckoutAllowed())
        assertFalse(basketPage.getErrorMessages().contains(ACCESSORY_LIMITATION), "accessory limitation method displayed unexpectedly.")
    }

    @Test(groups = "pageIntegrationTest")
    public void shouldNotBeAbleToCheckOutIfDeviceIsOutOfStock() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("apple", "iPhone 5 64GB Purple cca")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .clickOnContinueToBasketButton()

        catalogueGateway.makeItemOutOfStock("device", "562eabcd-e846-425f-bd82-097a0368b281")
        try {
            assertTrue(basketPage.isCheckoutAllowed());
            basketPage.checkOutBasket({ new BasketPage() })
            assertTrue(basketPage.getErrorMessages().contains(OUT_OF_STOCK));
            assertTrue(basketPage.getDevice().errors.contains("Out of stock."))
            assertFalse(basketPage.isCheckoutAllowed());

        } finally {
            catalogueGateway.makeItemInStock("device", "562eabcd-e846-425f-bd82-097a0368b281")
        }
    }


    @Test(groups = "pageIntegrationTest")
    public void shouldNotBeAbleToCheckOutIfAccessoryIsOutOfStock() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("samsung", "standard")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .selectAccessoryTile("8feedaa2-2396-44bf-8084-38024e3c522e")
                .clickOnContinueToBasketButton()

        assertTrue(basketPage.isCheckoutAllowed());

        catalogueGateway.makeItemOutOfStock("accessories", "8feedaa2-2396-44bf-8084-38024e3c522e")
        try {
            basketPage.checkOutBasket({ new BasketPage() })
            assertTrue(basketPage.getErrorMessages().contains(OUT_OF_STOCK));
            def accessories = basketPage.accessories
            assert accessories.size() == 1
            assert accessories[0].errors.contains("Out of stock.")
            assert accessories[0].changeAccessoryLinkDisplayed()
            assert "Choose a different item" == accessories[0].getChangeAccessoryMessage()
            assert !basketPage.isCheckoutAllowed()
        } finally {
            catalogueGateway.makeItemInStock("accessories", "8feedaa2-2396-44bf-8084-38024e3c522e")
        }

    }

    private void verifyDeviceDetailsInBasket(String expectedItemName, String expectedItemOnetimeCost, String expectedItemMonthlyCost, BasketDevice basketDevice) {
        assertEquals(basketDevice.getTitle(), expectedItemName)
        assertEquals(new PagePrice(basketDevice.getUpfrontCost()).unformattedStringValue, expectedItemOnetimeCost)
        assertEquals(new PagePrice(basketDevice.getMonthlyCost()).unformattedStringValue, expectedItemMonthlyCost)
    }

    @Test(groups = "pageIntegrationTest")
    public void validatePreOrderStockMessage() {
        def page = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("apple", "iPhone 5S Indigo")

        assertThat(page, is(aDeviceDetailsPage()
                .withStockMessage("Pre-order - Available from 24 December")
        ))

        page = page.viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .clickOnContinueToBasketButton()

        assertEquals(page.device.getStockMessage(), "Available from 24 December")
        page.emptyBasket()
    }

    @Test(groups = "acquisitionShopTest")
    public void canNavigateToTariffAndExtrasPageAfterSelectingNonLeadProductVariantAndSelectTariffs() {
        def tariffandExtrasPage = browsingJourney.navigateToDeviceDetailsPage("samsung", "standard")
                .selectWifi()
                .selectColourByName("Gold")
                .viewTariffs()

        tariffandExtrasPage.selectPayMonthlyTariffTile(0)
        def selectedColour = tariffandExtrasPage.getSelectedColour()
        def selectedCapacity = tariffandExtrasPage.getSelectedCapacity()

        def pageTitle = browser().getTitle()
        assertEquals(pageTitle, "O2 | Samsung standard phone cca Gold 32GB | Tariffs And Extras")

        assertEquals(selectedColour, "#FFD700")
        assertEquals(selectedCapacity, "32GB")

        def basketPage = tariffandExtrasPage.clickOnContinueToBasketButton()
    }

    @DataProvider(name = "PayMonthlyOrPayGo")
    public Object[][] payMonthlyOrPayGo() { [[false], [true]] }


    @Test(groups = "acquisitionShopTest")
    public void shopURLsInHeaderAndFooterShouldEndWithTrailingSlash() {
        def handsetListPage = browsingJourney.startHandsetFirstJourney(true)
        def shopUrlsInHeader = getShopUrlsFromHeader()
        for (headerShopUrl in shopUrlsInHeader) {
            assertTrue(isURLValid(headerShopUrl))
        }
        def shopUrlsInFooter = getShopUrlsFromFooter()
        for (shopUrlInFooter in shopUrlsInFooter) {
            assertTrue(isURLValid(shopUrlInFooter))
        }
    }

    @Test(groups = ["acquisitionShopTest", "precheckin"])
    public void validateDiscountedPriceForAccessoriesIsDisplayedInBasketRefreshTariffs() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("apple", "iPhone 5 64GB Purple cca")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .selectAccessoryTile("ac561fcd-1381-43ec-ba70-5d8f98a4b84b")
                .clickOnContinueToBasketButton()

        def accessories = basketPage.accessories
        assertEquals(accessories.size(), 1)
        assertEquals(new PagePrice(accessories[0].getUpfrontCost()).formattedStringValue, "£30.00")
    }

    @Test(groups = "acquisitionShopTest")
    public void validateDiscountedPriceForAccessoriesIsDisplayedInBasketPayNGoTariffs() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("lg", "gw300")
                .viewTariffs()
                .selectBigBundleTariffTile()
                .selectAccessoryTile("8feedaa2-2396-44bf-8084-38024e3c522e")
                .clickOnContinueToBasketButton()

        assertTrue(basketPage.isCheckoutAllowed());

        def accessories = basketPage.accessories
        assertEquals(accessories.size(), 1)
        assertEquals(new PagePrice(accessories[0].getUpfrontCost()).formattedStringValue, "£10.00")
    }

    // We derive the url by actually going to the endpoint as it could result in a redirect
    private String o2TariffsPortalPageUrl() {
        browser().navigateTo("https://www.o2.co.uk/shop/tariffs")
        return browser().getCurrentUrl()
    }

    @Test(groups = ["browsingJourney", "precheckin", "mobileAcquisitionShopTest"])
    public void validateTotalsDisplayedInBasket() {

        def listPage = browsingJourney.startHandsetFirstJourney(true,true)
        def freeDevice = listPage.devices().find { 0 == it.upfrontCost }
        assert freeDevice
        def basketPage = listPage.selectDeviceByBrandAndModel(freeDevice.brand, freeDevice.modelFamily)
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .clickOnContinueToBasketButton()

        double monthlyPaymentCalculatedPrice = basketPage.calculateMonthlyPaymentPriceFromBasketItems(basketPage.basketItems)
        double actualMonthlyPayment = new PagePrice(basketPage.getMonthlyTotal()).numericalValue
        assertEquals(actualMonthlyPayment, monthlyPaymentCalculatedPrice, String.format("%.2f is not same as %.2f", actualMonthlyPayment, monthlyPaymentCalculatedPrice))
    }

        @Test(groups = ["browsingJourney", "precheckin", "mobileAcquisitionShopTest"])
        public void validateUpfrontCostsDisplayedInBasket() {
            def listPage = browsingJourney.startHandsetFirstJourney(true,true)
            def freeDevice = listPage.devices().find { 0 == it.upfrontCost }
            assert freeDevice
            def basketPage = listPage.selectDeviceByBrandAndModel(freeDevice.brand, freeDevice.modelFamily)
                    .viewTariffs()
                    .selectPayMonthlyTariffTile(0)
                    .clickOnContinueToBasketButton()

            def paymentTodayCalculatedPrice = basketPage.calculateUpfrontPriceFromBasketItems(basketPage.basketItems)
            double actualTotalToPayToday = new PagePrice(basketPage.getUpfrontTotal()).numericalValue
            assertEquals(actualTotalToPayToday.doubleValue(), paymentTodayCalculatedPrice.doubleValue(), 0.0)
        }

        // #ECOM-5017
        @Test(groups = "acquisitionShopTest")
        public void listingPageShouldOnlyHaveLeadDevicesUnderPayMonthlyTab() {
            def handsetListPage = browsingJourney.startHandsetFirstJourney(true,true)

            handsetListPage.filterBy(DeviceFilter.allDevices().byBrand("apple"))
            assert handsetListPage.devices().find {
                "apple".equalsIgnoreCase(it.brand) && "iphone".equalsIgnoreCase(it.modelFamily)
            }

            assert !handsetListPage.devices().find {
                "apple".equalsIgnoreCase(it.brand) && "Apple 5 100S Indigo (child of iPhone 5 16GB Black cca".equalsIgnoreCase(it.modelFamily)
            }
            assert !handsetListPage.devices().find {
                "apple".equalsIgnoreCase(it.brand) && "Apple iphone 5 100gb black and slate (child of iPhone 5 16GB Black cca)".equalsIgnoreCase(it.modelFamily)
            }
        }

        @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
        public void modelNameShouldBeDisplayedForLeadModelsWhichDoNotHaveModelFamilyOnHandsetListPage() {
            def handsetListPage = browsingJourney.startHandsetFirstJourney(true,true)
            handsetListPage.filterBy(DeviceFilter.allDevices().byBrand("apple"))
            assert handsetListPage.devices().find {
                "apple".equalsIgnoreCase(it.brand) && "iphone 5s".equalsIgnoreCase(it.modelFamily)
            }
        }

        @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
        public void verifyFamilyVariantBlockPresentForALeadModelWithFamilyVariantOnPayGTab() {
            def handsetListPage = browsingJourney.startHandsetFirstJourney(true,true, "payasyougo")
            def detailsPage = handsetListPage.selectDeviceByBrandAndModel("blackberry", "q5")

            assertThat(detailsPage, is(aDeviceDetailsPage()
                    .with(aVariant()
                    .withNumberOfColours(3)
                    .withNumberOfCapacities(1)
                    .withNoConnectivityOption()
            )));
        }

        @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
        public void verifyFamilyVariantBlockPresentForALeadModelWithFamilyVariantPayMTab() {
            def handsetListPage = browsingJourney.startHandsetFirstJourney(true)
            def detailsPage = handsetListPage.selectDeviceByBrandAndModel("apple", "iphone 5s")
            assertThat(detailsPage, is(aDeviceDetailsPage()
                    .with(aVariant()
                    .withNumberOfColours(1)
                    .withNumberOfCapacities(1)
                    .withNoConnectivityOption()
            )));
        }

        //FIXME: what does this even test? It seems to be checking the non-existance of something by checking the existance of something else unrelated...?
        @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
        public void verifyFamilyVariantBlockNotPresentForALeadModelWithNoFamilyVariantOnPayMTab() {
            def handsetListPage = browsingJourney.startHandsetFirstJourney(true,true)
            def detailsPage = handsetListPage.selectDeviceByBrandAndModel("blackberry", "z3")


            assertThat(detailsPage, is(aDeviceDetailsPage()
                    .with(aVariant()
                    .withNumberOfColours(1)
                    .withAvailableColour("Black")
                    .withNumberOfCapacities(1)
            )));
        }

        @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
        public void verifyFamilyVariantBlockNotPresentForALeadModelWithNoFamilyVariantOnPayGTab() {
            def handsetListPage = browsingJourney.startJourneyToPAYGHandsetListPage(true)
            def detailsPage = handsetListPage.selectDeviceByBrandAndModel("apple", "iphone")

            assertThat(detailsPage, is(aDeviceDetailsPage()
                    .with(aVariant()
                    .withNumberOfColours(1)
                    .withAvailableColour("Black")
                    .withNumberOfCapacities(1)
            )));

        }

        @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
        public void verifyVariantBlockDisplayedByDefaultWhenUserNavigatedToDetailsPage() {
            def handsetListPage = browsingJourney.startHandsetFirstJourney(true,true, "payasyougo")
            def detailsPage = handsetListPage.selectDeviceByBrandAndModel("lg", "gw 306")

            assertThat(detailsPage, is(aDeviceDetailsPage()
                    .with(aVariant()
                    .withNumberOfColours(2)
                    .withAvailableColour("Black")
                    .withAvailableColour("Gold")
                    .withNumberOfCapacities(1)
                    .withColourSelected("Black")
                    .with16GBSelected()
            )));
        }

        @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
        public void verifyCapacitiesDisplayedWhenUserSelectsAVariantColour() {
            def handsetListPage = browsingJourney.startHandsetFirstJourney(true,true, "payasyougo")
            def detailsPage = handsetListPage.selectDeviceByBrandAndModel("lg", "gw 306")
            detailsPage.selectColourByName("Gold")

            assertThat(detailsPage, is(aDeviceDetailsPage()
                    .with(aVariant()
                    .withNumberOfCapacities(1)
                    .with32GBAvailable())
            ));

            detailsPage.selectColourByName("Black")
            assertThat(detailsPage, is(aDeviceDetailsPage()
                    .with(aVariant()
                    .withNumberOfCapacities(1)
                    .with16GBAvailable()
            )));

        }

        @Test(groups = "acquisitionShopTest")
        public void verifyDeepLinkingOnDetailsPage() {
            def deviceDetailsPage = browsingJourney.deepLinkToDeviceDetailsPage("phones", "apple", "iphone-5", "colour=gold&memory=32gb")

            assertThat(deviceDetailsPage, is(aDeviceDetailsPage()
                    .with(aVariant().withColourSelected("Gold").with32GBSelected())
                    .with(aDeviceCost().withOneOffCost(68.99))
                    .whichIsOutOfStock()));

        }

        //ECOM 5661
        @Test(groups = "acquisitionShopTest")
        public void "verify deep link to accessories details page"() {
            def deviceDetailsPage = browsingJourney.deepLinkToDeviceDetailsPage("accessories", "sandisk", "memory-card")
            assertThat(deviceDetailsPage, is(aDeviceDetailsPage()
                    .forDevice("SanDisk Memory Card")
                    .whichIsInStock()
                    .with(aDeviceCost().withUnconnectedOneOffCost(25.00))
            ));
        }
        //ECOM 5735
        @Test(groups = "acquisitionShopTest")
        public void "verify browsing journey test for accessories"() {
            def deviceDetailsPage = browsingJourney.startAccessoriesFirstJourney(true)
                    .selectDeviceByBrandAndModel("sandisk", "memory-card")
            assertThat(deviceDetailsPage, is(aDeviceDetailsPage()
                    .forDevice("SanDisk Memory Card")
                    .whichIsInStock()
                    .with(aDeviceCost().withUnconnectedOneOffCost(25.0))
            ));
            deviceDetailsPage.addAccessoryToBasket()
            def basketPage = deviceDetailsPage.goToBasket()
            assertTrue(basketPage.checkoutAllowed);

            def accessories = basketPage.getAccessories()
            assertEquals(accessories.get(0).getTitle(), "SanDisk Memory Card")
            assertEquals(accessories.get(0).getUpfrontCost(), "£25.00")

        }

        @Test(groups = "acquisitionShopTest")
        public void verifyDeepLinkingOnDetailsPageForRecommedationSection() {
            def deviceDetailsPage = browsingJourney.deepLinkToDeviceDetailsPage("phones", "apple", "iphone-5", "scrollTo=recommendedTariff")
            deviceDetailsPage.waitUntilPageIsInDockMode(deviceDetailsPage)
            assertThat(deviceDetailsPage, is(aDeviceDetailsPage()
                    .withDeviceFloatingBarDisplayed()
                    .with(aRecommendedTariffSection()
                    .withExpectedRecommendedTariff("100MB", "3G", "Unlimited", "600", "£239.99", "£30.00")
                    .withExpectedMonthlyCostCombination("*£15.00 device + £15.00 airtime")
            )
            ));

        }

        @Test(groups = "acquisitionShopTest")
        public void verifyDeeplinkingOnTariffAndExtrasPage() {
            //Navigating to the PAYM tariffAndExtras Page
            def tariffAndExtrasPagePayM = browsingJourney.navigateToTariffsAndExtrasPage("apple", "iphone-5s-indigo", "69e9f870-8818-11f2-9e96-0800100c9abb", "", "paymonthly")

            assertEquals(tariffAndExtrasPagePayM.getMakeAndModel(), "Apple iPhone 5S Indigo")
            assertEquals(tariffAndExtrasPagePayM.getPricingSummaryFromHeader(), "From £68.99 upfront cost and £32.00 a month")

            //Navigating to the PAYG tariffAndExtras Page
            def tariffAndExtrasPagePayG = browsingJourney.navigateToTariffsAndExtrasPage("apple", "iphone-5s-indigo", "69e9f870-8818-11f2-9e96-0800100c9abb", "", "payasyougo")

            assertEquals(tariffAndExtrasPagePayG.getMakeAndModel(), "Apple iPhone 5S Indigo")
            assertEquals(tariffAndExtrasPagePayG.getPricingSummaryFromHeader(), "£100.00 upfront cost")

            //Navigating to the PAYM tariffAndExtras Page which has no PAYM tariffs
            def tariffAndExtrasPageOnlyPayG = browsingJourney.navigateToTariffsAndExtrasPage("nokia", "6700-slide", "21a0a50a-ff49-421c-ad89-957a6cb98a67", "", "paymonthly")
            assertEquals(tariffAndExtrasPageOnlyPayG.getMakeAndModel(), "Nokia 6700 Slide")
            assertEquals(tariffAndExtrasPageOnlyPayG.getPricingSummaryFromHeader(), "£100.00 upfront cost")

            //Navigating to the PAYG tariffAndExtras Page which has no PAYG tariffs
            def tariffAndExtrasPageOnlyPayM = browsingJourney.navigateToTariffsAndExtrasPage("samsung", "standard", "68ee7410-810a-11e2-9e96-0800200c9a66", "", "payasyougo")
            assertEquals(tariffAndExtrasPageOnlyPayM.getMakeAndModel(), "Samsung Standard")
            assertEquals(tariffAndExtrasPageOnlyPayM.getPricingSummaryFromHeader(), "From £89.99 upfront cost and £50.00 a month")

            //Navigating to the device tariffAndExtras Page
            def tariffAndExtrasPage = browsingJourney.navigateToTariffsAndExtrasPage("samsung", "standard")
            assertEquals(tariffAndExtrasPage.getMakeAndModel(), "Samsung Standard")
            assertEquals(tariffAndExtrasPageOnlyPayM.getPricingSummaryFromHeader(), "From £89.99 upfront cost and £50.00 a month")
            assertEquals(tariffAndExtrasPage.getSelectedColour(), "#C0C0C0")
            assertEquals(tariffAndExtrasPage.getSelectedCapacity(), "64GB")

            //Navigating to the device tariffAndExtras Page which has only PayG tariffs
            def tariffAndExtrasPageDevicePayG = browsingJourney.navigateToTariffsAndExtrasPage("blackberry", "q5")
            assertEquals(tariffAndExtrasPage.getMakeAndModel(), "Blackberry Q5")
            //TODO - this model of phone has no tariffs associated
            //assertEquals(tariffAndExtrasPageOnlyPayM.getPricingSummaryFromHeader(),"£100.00 upfront cost")
            assertEquals(tariffAndExtrasPage.getSelectedColour(), "#000000")
            assertEquals(tariffAndExtrasPage.getSelectedCapacity(), "8GB")


            //Navigating to the PAYM tariffAndExtras Page ignoring non recommended accessories (LIVE BUG ECOMM-8011)
            def selectedRecommendedAccessoryId = "ac561fcd-1381-43ec-ba70-5d8f98a4b84b"
            def selectedNonRecommendedAccessoryId = "e11b7606-135c-4d32-8c80-55ab9b6a2fff"
            def tariffAndExtrasPagePayMAccessories = browsingJourney.navigateToTariffsAndExtrasPage("lg", "g3", "142eabcd-e126-325g-ba82-297a7368x221", "", "paymonthly", "$selectedRecommendedAccessoryId,$selectedNonRecommendedAccessoryId")

            assert "LG G3" == tariffAndExtrasPagePayMAccessories.getMakeAndModel()
            assert [selectedRecommendedAccessoryId] == tariffAndExtrasPagePayMAccessories.getSelectedAccessories()

        }

        @Test(groups = "acquisitionShopTest")
        public void verifyDeepLinkingOnPAYMTariffPlan() {
            //Navigating to PAYM Tariffs and Extras Page for lead model with no Accessories and no Insurance
            def planId = "ac262ae0-8595-11e2-9e96-0800200c9a66:89.99:25.00:sp-4d6f7-6579f-28b61-0de011-da3"
            def tariffAndExtrasPageWithoutAccessoriesAndInsuranceForLeadModel = browsingJourney.navigateToTariffsAndExtrasPage("samsung", "standard",
                    "68ee7410-810a-11e2-9e96-0800200c9a66",
                    planId,
                    "paymonthly");

            assertEquals(tariffAndExtrasPageWithoutAccessoriesAndInsuranceForLeadModel.getMakeAndModel(), "Samsung Standard")
            assertEquals(tariffAndExtrasPageWithoutAccessoriesAndInsuranceForLeadModel.getSelectedTariff(), planId)
            assertEquals(tariffAndExtrasPageWithoutAccessoriesAndInsuranceForLeadModel.getSelectedColour(), "#C0C0C0")
            assertEquals(tariffAndExtrasPageWithoutAccessoriesAndInsuranceForLeadModel.getSelectedCapacity(), "64GB")

            //Navigating to PAYM Tariffs and Extras Page for lead model with Accessories and Insurance
            def tariffAndExtrasPageWithAccessoriesAndInsuranceForLeadModel = browsingJourney.navigateToTariffsAndExtrasPage("samsung", "standard",
                    "68ee7410-810a-11e2-9e96-0800200c9a66",
                    planId,
                    "paymonthly", "402a1373-c0da-4ec9-b901-7060e59ff666", "2b218e2a-9977-44ef-bb0b-7660ef1e4535");

            assertEquals(tariffAndExtrasPageWithAccessoriesAndInsuranceForLeadModel.getSelectedAccessories().get(0), "402a1373-c0da-4ec9-b901-7060e59ff666")
            assertEquals(tariffAndExtrasPageWithAccessoriesAndInsuranceForLeadModel.getSelectedInsurance(), "2b218e2a-9977-44ef-bb0b-7660ef1e4535")

            //Navigating to PAYM Tariffs and Extras Page for non lead model with Accessories and Insurance
            def tariffAndExtrasPageWithAccessoriesAndInsuranceForNonLeadModel = browsingJourney.navigateToTariffsAndExtrasPage("samsung", "standard",
                    "68ee7410-810a-11e2-9e96-0800200c9a98",
                    "cca1234-bcb5-4db9-a341-03babdb9efa2:209.99:20.00:4d6f7-6579f-28b61-0de011-da4",
                    "paymonthly");

            assertEquals(tariffAndExtrasPageWithAccessoriesAndInsuranceForNonLeadModel.getSelectedTariff(), "cca1234-bcb5-4db9-a341-03babdb9efa2:209.99:20.00:4d6f7-6579f-28b61-0de011-da4")
            assertEquals(tariffAndExtrasPageWithAccessoriesAndInsuranceForNonLeadModel.getSelectedColour(), "#FFD700")
            assertEquals(tariffAndExtrasPageWithAccessoriesAndInsuranceForNonLeadModel.getSelectedCapacity(), "32GB")
        }

        @Test(groups = "acquisitionShopTest")
        public void verifyDeepLinkingOnPAYGTariffPlan() {
            def planId = "66f43114-ee3c-4828-9d6c-5c035ab9ff8f"
            def tariffAndExtrasPage = browsingJourney.navigateToTariffsAndExtrasPage("nokia", "6700-slide",
                    "21a0a50a-ff49-421c-ad89-957a6cb98a67",
                    planId,
                    "payasyougo");

            assertEquals(tariffAndExtrasPage.getMakeAndModel(), "Nokia 6700 Slide")
            assertEquals(tariffAndExtrasPage.getSelectedTariff(), planId)
        }

        @Test(groups = "acquisitionShopTest")
        public void itShouldDisplayPageNotFoundForOldBookmarkedUrlWhichIsChildVariantOfProductFamily() {
            def errorPage = browsingJourney.navigateToExpectingErrorPage("phones/apple/iphone-5-32gb-gold-cca/?contractType=paymonthly");

            assertTrue(errorPage.isOnShopErrorPage())
        }

        @Test(groups = "acquisitionShopTest")
        public void itShouldDisplayPageNotFoundForInvalidModel() {
            def errorPage = browsingJourney.navigateToExpectingErrorPage("phones/apple/invalid-model/?contractType=paymonthly");

            assertTrue(errorPage.isOnShopErrorPage())
        }


        private Boolean isURLValid(String shopUrl) {
            def pattern1 = ".*/"
            def pattern2 = ".*/#.*"
            def pattern3 = ".*/?.*"

            shopUrl.matches(pattern1) || shopUrl.matches(pattern2) || shopUrl.matches(pattern3)
        }

        //ECOM-2008: AC1 - Acquisition: Validating checkout information for a acquisition customer for a voice+data refresh tariff
        // with a device that has any upfront cost
        @Test(groups = ["acquisitionShopTest", "pageIntegrationTest"])
        public void validateBasketCheckoutInfoAreasForAcquisitionVoiceAndDataRefreshTariffsWithAnyUpfrontCost() {
            def basketPageSummarySection = browsingJourney.startHandsetFirstJourney(true)
                    .selectDeviceByBrandAndModel("samsung", "standard")
                    .viewTariffs()
                    .selectPayMonthlyTariffTile(1)
                    .clickOnContinueToBasketButton()
                    .getSummarySection()

            //Assert "What you'll need" section
            assertTrue(basketPageSummarySection.hasWhatYouWillNeedInfo())
            assertTrue(basketPageSummarySection.hasNameAndAddressInfo())
            assertTrue(basketPageSummarySection.hasAccountDetailsInfo())
            assertTrue(basketPageSummarySection.hasCardDetailsInfo())
            //Assert "What you will get" section
            assertTrue(basketPageSummarySection.hasSecureCheckoutInfo())
            assertTrue(basketPageSummarySection.hasReturnPolicyInfo())
            assertTrue(basketPageSummarySection.hasProtectInfo())
            assertTrue(basketPageSummarySection.hasExistingNumberInfo())
            assertTrue(basketPageSummarySection.hasPayAirtimeInfo())
        }

        //ECOM-2620: AC10 - Validating checkout information for a customer for a Data only PAYG refresh tariff
        @Test(groups = "acquisitionShopTest")
        public void validateBasketCheckoutInfoAreasVoiceAndDataPayAndGo() {
            def basketPageSummarySection = browsingJourney.startHandsetFirstJourney(true)
                    .selectDeviceByBrandAndModel("lg", "gw300")
                    .viewTariffs()
                    .selectBigBundleTariffTile()
                    .clickOnContinueToBasketButton()
                    .getSummarySection()

            //Assert "What you'll need" section
            assertTrue(basketPageSummarySection.hasWhatYouWillNeedInfo())
            assertTrue(basketPageSummarySection.hasNameAndAddressInfo())
            assertTrue(basketPageSummarySection.hasCardDetailsInfo())
            //Assert "What you will get" section
            assertTrue(basketPageSummarySection.hasSecureCheckoutInfo())
            assertTrue(basketPageSummarySection.hasReturnPolicyInfo())
            assertTrue(basketPageSummarySection.hasProtectInfo())
            assertTrue(basketPageSummarySection.hasExistingNumberInfo())
        }

        @Test(groups = "browsingJourney")
        //ECOM-2630: AC2 - Validating checkout information for an acquisition customer for a Data only Refresh tariff
        // with a device that has any upfront cost
        public void validateBasketCheckoutInfoAreasForAcquisitionCustomerDataOnlyRefreshTariff() {
            def deviceDetailsPage = browsingJourney.navigateToDeviceDetailsPage("huawei", "e173-postpay-and-prepay", true)
            def tariffsAndExtrasPage = deviceDetailsPage.viewTariffs()
            tariffsAndExtrasPage.getPayMonthlyGridPlans().first().select()
            def basketPageSummarySection = tariffsAndExtrasPage.clickOnContinueToBasketButton().getSummarySection()

            //Assert "What you'll need" section
            assertTrue(basketPageSummarySection.hasWhatYouWillNeedInfo())
            assertTrue(basketPageSummarySection.hasNameAndAddressInfo())
            assertTrue(basketPageSummarySection.hasAccountDetailsInfo())
            assertTrue(basketPageSummarySection.hasCardDetailsInfo())
            //Assert "What you will get" section
            assertTrue(basketPageSummarySection.hasSecureCheckoutInfo())
            assertTrue(basketPageSummarySection.hasReturnPolicyInfo())
            assertTrue(basketPageSummarySection.hasProtectInfo())
            assertTrue(basketPageSummarySection.hasPayAirtimeInfo())
        }

        @Test(groups = "browsingJourney")
        //ECOM-2723
        public void validateUserSelectionIsPreservedWithBrowserBackButton() {
            def handsetDetailsPage = browsingJourney.navigateToDeviceDetailsPage("apple", "iphone-5", true)
            def tariffsAndExtrasPage = handsetDetailsPage.viewTariffs()
            tariffsAndExtrasPage.getPayMonthlyGridPlans().first().select()
            tariffsAndExtrasPage.selectAccessoryTile("402a1373-c0da-4ec9-b901-7060e59ff666")
            tariffsAndExtrasPage.selectAccessoryTile("ac561fcd-1381-43ec-ba70-5d8f98a4b84b")
            tariffsAndExtrasPage.selectInsuranceTile(0)
            tariffsAndExtrasPage.clickOnContinueToBasketButton()

            tariffsAndExtrasPage = browser().clickOnBackButton(TariffsAndExtrasPage.class)

            assertTrue(tariffsAndExtrasPage.getSelectedAccessories().contains("402a1373-c0da-4ec9-b901-7060e59ff666"))
            assertTrue(tariffsAndExtrasPage.getSelectedAccessories().contains("ac561fcd-1381-43ec-ba70-5d8f98a4b84b"))
            assertEquals(tariffsAndExtrasPage.getSelectedTariff(), "cca1234-bcb5-4db9-a341-03babdb9efad:239.99:15.00:ip-4d6f7-6579f-28b61-0de011-da9")
            assertEquals(tariffsAndExtrasPage.getSelectedInsurance(), "2b218e2a-9977-44ef-bb0b-7660ef1e4535")

        }

        @Test(groups = "browsingJourney")
        //ECOM-2658: Checkout information areas for wifi only device
        public void validateBasketCheckoutInfoAreasForAcquisitionCustomerWifiDevices() {

            def tariffAndExtrasPageForWifiDevices = browsingJourney.navigateToTariffsAndExtrasPage("apple", "ipad-mini-wifi")
            assertEquals(tariffAndExtrasPageForWifiDevices.getMakeAndModel(), "Apple iPad Mini")
            assertEquals(tariffAndExtrasPageForWifiDevices.getPricingSummaryFromHeader(), "£559.00 upfront cost")

            def basketPageSummarySection = tariffAndExtrasPageForWifiDevices.clickOnContinueToBasketButton().getSummarySection()

            //Assert "What you'll need" section
            assertTrue(basketPageSummarySection.hasWhatYouWillNeedInfo())
            assertTrue(basketPageSummarySection.hasNameAndAddressInfo())
            assertTrue(basketPageSummarySection.hasCardDetailsInfo())
            //Assert "What you will get" section
            assertTrue(basketPageSummarySection.hasSecureCheckoutInfo())
            assertTrue(basketPageSummarySection.hasReturnPolicyInfo())
            assertTrue(basketPageSummarySection.hasProtectInfo())
        }

        @Test(groups = "browsingJourney")
        //ECOM-1764
        public void validateContextIsMaintainedForPayMDeviceFromDetailsToTariffsAndExtrasPage() {
            def handsetDetailsPage = browsingJourney.navigateToDeviceDetailsPage("apple", "iphone-5", true)
            def tariffsAndExtrasPage = handsetDetailsPage.viewTariffs()

            assertEquals(tariffsAndExtrasPage.getContractType(), "paymonthly")
        }

        @Test(groups = "browsingJourney")
        //ECOM-1764
        public void validateContextIsMaintainedForPayGDeviceFromDetailsToTariffsAndExtrasPage() {
            def handsetDetailsPage = browsingJourney.navigateToDeviceDetailsPage("apple", "iphone", true)
            def tariffsAndExtrasPage = handsetDetailsPage.viewTariffs()

            assertEquals(tariffsAndExtrasPage.getContractType(), "payasyougo")
        }

        @Test(groups = "browsingJourney")
        //ECOM-3944
        public void validateAutoReDirectToBasketPageWhenNoTariffsAndExtrasAvailable() {
            def handsetDetailsPage = browsingJourney.navigateToDeviceDetailsPage("apple", "ipad-mini-wifi-with-no-tariffs-and-extras", true)
            handsetDetailsPage.addToBasket()
            handsetDetailsPage.waitForAddToBasketButtonTobeEnabled()
            def basketPage = handsetDetailsPage.goToBasket()

            assert basketPage.accessories[0].title == "Apple iPad mini wifi no tariffs and extras"
        }

        private List<String> getShopUrlsFromHeader() {
            def elements = currentPage().findElements(By.xpath("//div[@class='nav-primary']//a[contains(@href,'/shop')]"))
            prepareListOfShopUrls(elements)
        }

        private List<String> getShopUrlsFromFooter() {
            def elements = currentPage().findElements(By.xpath("//div[@id='o2-footer']//a[contains(@href,'/shop')]"))
            prepareListOfShopUrls(elements)
        }

        private List<String> prepareListOfShopUrls(List<WebElement> webElements) {
            List<String> shopUrls = []
            webElements.each { element -> shopUrls.add(element.getAttribute('href')) }
            shopUrls
        }

        // ECOM-4207
        @Test(groups = "acquisitionShopTest")
        public void "device listing page links to payg device details page"() {
            def deviceListPage = browser().navigateToDeviceListingsPageFor("Phones")
                    .viewPayAsYouGo().showAll()
            def deviceDetailsPage = deviceListPage.selectDeviceByBrandAndModel("Apple", "iPhone 5 64GB Purple cca")

            assertThat(deviceDetailsPage, is(aDeviceDetailsPage().wthPaygUrl()))
        }

        //ECOM-4158
        @Test(groups = "acquisitionShopTest")
        public void VerifyingTheRedirectionForComingSoonAndEndOfLifeDevicesFromTariffPage() {

            //End of life device
            browser().navigateToDetailsPageForFallBackUrl("https://localhost.o2.co.uk:9443/upgrade/store/tariff/apple/iphone-1/?productId=e72345155-9215-4181-b1d9-cfc2e20c58xaa&planId=&contractType=payMonthly")
            assertEquals(browser().getTitle(), "Apple iPhone 1 Specs, Contract Deals & Pay As You Go")

            //Coming soon device
            browser().navigateToDetailsPageForFallBackUrl("https://localhost.o2.co.uk:9443/upgrade/store/tariff/apple/iphone-1/?productId=e72345155-9215-4181-b1d9-cfc2e20c58xbb&planId=&contractType=payMonthly")
            assertEquals(browser().getTitle(), "Apple iPhone 1 Specs, Contract Deals & Pay As You Go")
        }

        //ECOM-5963
        @Test(groups = "acquisitionShopTest")
        public void "verify the basket icon link in sub-navigation bar for accessories acquisition journey"() {
            def deviceListPage = browser().navigateToDeviceListingsPageFor("accessories")
            //assertThat(deviceListPage , is(aDeviceListingsPage().withBasketIconOnSubNavigationBar()))
            def deviceDetailsPage = deviceListPage.selectDeviceByBrandAndModel("sandisk", "memory-card")

            assertThat(deviceDetailsPage, is(aDeviceDetailsPage()
                    .withBasketIconOnSubNavigationBar()
            ))
            deviceDetailsPage.addAccessoryToBasket()
            BasketPage basketPage = deviceDetailsPage.clickOnBasketIconOnSubNavigationBar()
            assertTrue(basketPage.checkoutAllowed)
            assertEquals(basketPage.getAccessories().get(0).getTitle(), "SanDisk Memory Card")
            assertTrue(basketPage.isBasketIconOnSubNavigationBarNotExist())
        }

        //ECOM-5963
        @Test(groups = "acquisitionShopTest")
        public void "verify the basket icon link in sub-navigation bar for phone acquisition journey"() {
            def deviceListPage = browser().navigateToDeviceListingsPageFor("Phones")
                    .viewPayAsYouGo().showAll()
            def deviceDetailsPage = deviceListPage.selectDeviceByBrandAndModel("Apple", "iPhone 5 64GB Purple cca")

            assertThat(deviceDetailsPage, is(aDeviceDetailsPage()
                    .withBasketIconOnSubNavigationBar()
            ))

            TariffsAndExtrasPage tariffsAndExtrasPage = deviceDetailsPage.viewTariffs()
            assertTrue(tariffsAndExtrasPage.isBasketIconOnSubNavigationBarExist())

            tariffsAndExtrasPage.selectBigBundleTariffTile()

            def basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()

            assertTrue(basketPage.checkoutAllowed)
            assertEquals(basketPage.getBasketItems().get(0).getTitle(), "Apple iPhone 5 64GB Purple cca")
            assertTrue(basketPage.isBasketIconOnSubNavigationBarNotExist())

            tariffsAndExtrasPage = browser().clickOnBackButton(tariffsAndExtrasPage)

            basketPage = tariffsAndExtrasPage.clickOnBasketIconOnSubNavigationBar()
            assertEquals(basketPage.getBasketItems().get(0).getTitle(), "Apple iPhone 5 64GB Purple cca")
            assertTrue(basketPage.isBasketIconOnSubNavigationBarNotExist())

        }

        //ECOM-6171
        @Test(groups = ["acquisitionShopTest", "precheckin"])
        public void "On the device details page on Pay monthly - there is Pay and Go text this needs to be a link through to the Pay and Go tab on the tariff and extras page"() {

            //given i select blackberry q5 and i choose the color as White
            def deviceDetailsPage = browsingJourney.startHandsetFirstJourney(true,true)
                    .selectDeviceByBrandAndModel("blackberry", "q5").selectColourByName("White")

            //when i go to tariff page
            def tariffsAndExtrasPage = deviceDetailsPage.viewTariffsByAvailability()

            //then i should see the selected colour as White
            assertEquals(tariffsAndExtrasPage.selectedColourText, "White")

            //then I should see activeContractType as Pay & GO
            assert tariffsAndExtrasPage.activeContractType.equalsIgnoreCase("payasyougo")

            //when I change the variant on tariff page
            tariffsAndExtrasPage.selectColour("Black");

            //then I should see activeContractType as payasyougo
            assert tariffsAndExtrasPage.activeContractType.equalsIgnoreCase("payasyougo")

            //when I go back to device details page
            tariffsAndExtrasPage.clickOnBackToProductDetails()

            //choose a non lead variant
            deviceDetailsPage.selectColourByName("White")

            //when i go to tariff page
            tariffsAndExtrasPage = deviceDetailsPage.viewTariffsByAvailability()

            //then i should see the selected colour as White
            assertEquals(tariffsAndExtrasPage.selectedColourText, "White")

            //then I should see activeContractType as payasyougo
            assert tariffsAndExtrasPage.activeContractType.equalsIgnoreCase("payasyougo")

        }

        //ECOM-6179
        @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest", "browsingJourney"])
        public void "Basket  - Insurance upsell"() {
            def page = browsingJourney
                    .startTabletFirstJourney(true)
                    .selectDeviceByBrandAndModel("Sony", "tab99")
                    .selectColourByName("White")
                    .viewTariffs()
                    .selectPayMonthlyTariffTile(0)
                    .selectInsuranceTile(1)

            int insuranceIndex = 1
            String insuranceName = page.getNameForInsuranceTile(insuranceIndex)
            String lowestPriceInsuranceName = page.getNameForInsuranceTile(0)
            String insuranceDescription = page.getDescriptionForInsuranceTile(insuranceIndex)
            def basketPage = page.clickOnContinueToBasketButton()


            def basketItems = basketPage.basketItems
            assertTrue(basketItems.any {
                it.getTitle() == insuranceName
            })

            assertTrue(basketItems.any {
                it.getDetails() == insuranceDescription
            })

            def insurances = basketPage.insurances
            assertEquals(insurances.size(), 1)

            assertEquals(insurances[0].insuranceOverlay.dataId, "o2TabletInsuranceAcquisition")

            insurances[0].remove()

            assertTrue(basketPage.basketItems.any {
                it.getTitle() == lowestPriceInsuranceName
            })
        }

        //ECOM-6387
        @Test(groups = "acquisitionShopTest")
        public void "Check the presence of version code for css and js files on Tariff & Extras page and Basket Page"() {
            DeviceDetailsPage detailsPage = browsingJourney.navigateToDeviceDetailsPage("samsung", "standard", true)
            TariffsAndExtrasPage page = detailsPage.viewTariffs()
            Properties properties = new Properties();
            ClassPathResource resource = new ClassPathResource("upgradeShopVersion.properties");
            try {
                properties.load(resource.getInputStream());
            } catch (IOException e) {
            }

            // tariff & Extras page
            String actualJsUrl = page.returnBaseURL() + "/upgrade/static/" + new VersionInfo(properties).getRevision() + "/newShopStatic/_assets/js/tariff-and-extras.min.js"
            assertTrue(page.getJSLink(actualJsUrl))
            String actualCssUrl = page.returnBaseURL() + "/upgrade/static/" + new VersionInfo(properties).getRevision() + "/newShopStatic/_assets/css/tariffAndExtras.css"
            assertTrue(page.getCSSLink(actualCssUrl))

            //Basket page
            BasketPage basketPage = page.selectPayMonthlyTariffTile(0).clickOnContinueToBasketButton()
            String actualJsLink = basketPage.returnBaseURL() + "/upgrade/static/" + new VersionInfo(properties).getRevision() + "/newShopStatic/_assets/js/basket.min.js"
            assertTrue(basketPage.getJSLink(actualJsLink))
            String actualCssLink = basketPage.returnBaseURL() + "/upgrade/static/" + new VersionInfo(properties).getRevision() + "/newShopStatic/_assets/css/basket.css"
            assertTrue(basketPage.getCSSLink(actualCssLink))

        }

        @Test(groups = ["acquisitionShopTest"])
        public void 'cacheable urls should be set with the cache-headers in response'() {
            RestClientHelper restClientHelper = new RestClientHelper()
            def cacheableUrls = ['/upgrade/store/',
                                 '/upgrade/store/phones', '/upgrade/store/tablets', '/upgrade/store/mobile-broadband',
                                 '/upgrade/store/smartwatches', '/upgrade/store/fitness-trackers', '/upgrade/store/accessories',
                                 '/upgrade/store/phones/', '/upgrade/store/tablets/', '/upgrade/store/mobile-broadband/',
                                 '/upgrade/store/smartwatches/', '/upgrade/store/fitness-trackers/', '/upgrade/store/accessories/',
                                 '/upgrade/store/phones/apple/iphone-5s/',
                                 '/upgrade/store/tablets/tesco/dummy-air-2',
                                 '/upgrade/store/smartwatches/bingo/t-10-smartwatch/',
                                 '/upgrade/store/fitness-trackers/sony/smartband-talk',
                                 '/upgrade/store/mobile-broadband/huawei/pay-monthly-e173-usb-modem',
                                 '/upgrade/store/accessories/scandisk/memory-card/',
                                 '/upgrade/store/tariff/apple/iphone-5-64gb-purple-cca/?productId=562eabcd-e846-425f-bd82-097a0368b281&planId=&contractType=paymonthly',
                                 '/upgrade/store/sim-cards/pay-as-you-go',
                                 '/upgrade/store/sim-cards/pay-as-you-go/delivery?planId=somePlanId'
            ]

            restClientHelper.verifyCacheHeadersForRestUrls("${framework.Configuration.hostUrl}", cacheableUrls, Method.HEAD, [:], { resp ->
                assert resp.allHeaders.any { it =~ "Cache-Control: max-age=1, public" }
            })

            def nonCacheableUrls = ['/upgrade/store/sim-cards/sim-only-deals/', '/upgrade/store/tariff/submitToBasket/', '/upgrade/store/sim-cards/pay-as-you-go/confirmation']

            def noCacheVerificationClosure = { resp ->
                assert resp.allHeaders.any { it =~ "Cache-Control: no-cache" }
                assert resp.allHeaders.any { it =~ "Cache-Control: no-store" }
            }
            restClientHelper.verifyCacheHeadersForRestUrls("${framework.Configuration.hostUrl}", nonCacheableUrls, Method.HEAD, [:], noCacheVerificationClosure)

            def nonCacheableAjaxUrls = ['/upgrade/store/ajax/customerInfo/', '/upgrade/store/ajax/cnc/selectedStore']
            restClientHelper.verifyCacheHeadersForAjaxRestUrls("${framework.Configuration.hostUrl}", nonCacheableAjaxUrls, Method.GET, ContentType.JSON, [:], noCacheVerificationClosure)

            def nonCacheableAjaxPostUrls = ['/upgrade/store/ajax/recentlyViewedDevices/microsoft/band/']
            restClientHelper.verifyCacheHeadersForAjaxRestUrls("${framework.Configuration.hostUrl}", nonCacheableAjaxPostUrls, Method.POST, ContentType.JSON, [:], noCacheVerificationClosure)
        }

        @Test(groups = ["acquisitionShopTest"])
        public void 'cheapest accessory 3 for 2'() {
            def deviceListingPage = browser().navigateToDeviceListingsPageFor("phones")
            def basketPage = deviceListingPage.viewPayAsYouGo().selectDeviceByName("Apple iPhone 5").viewTariffs().selectPayAndGoTariffTile(0)
                    .selectAccessoryTile(0)
                    .selectAccessoryTile(2)
                    .selectAccessoryTile(5)
                    .clickOnContinueToBasketButton()

            def freeAccessory = basketPage.basketItems.find { it.title == "SkullCandy Headphones from trusted brand" }
            assert freeAccessory.upfrontCost == "Free\nWas £10.00"
            assert freeAccessory.promotionText == "Three for two - skullcandy, Logitech speakers, Nokia BH-108 Bluetooth Headset"
        }

        @Test(groups = ["acquisitionShopTest"])
        public void 'Verify "Was" price for free accessory'() {
            def basketPage = browsingJourney.startHandsetFirstJourney(true,true)
                    .selectDeviceByBrandAndModel("apple", "delaydelivery")
                    .viewTariffs()
                    .selectPayMonthlyTariffTile(6)
                    .clickOnContinueToBasketButton()

            def freeAccessory = basketPage.basketItems.find { it.title == "Logitech V-10 Speakers" }
            assert freeAccessory.upfrontCost == "Free\nWas £70.00"
        }

        @Test(groups = ["acquisitionShopTest"])
        public void applyVoucherCodeThenEmptyBasket() {
            def listPage = browsingJourney.startHandsetFirstJourney(true)
            def basketPage = listPage.selectDeviceByBrandAndModel("LG", "GW300")
                    .viewTariffs()
                    .selectPayAndGoTariffTile(0)
                    .selectAccessoryTile(0)
                    .clickOnContinueToBasketButton()

            assert basketPage.getDevice()
            assert basketPage.getTariff()

            basketPage = basketPage.expandAndApplyVoucherCode("ACCOFF50")

            assert basketPage.getVoucherMessage() == "Promo code: ACCOFF50"
            assert basketPage.isVoucherRemoveLinkVisible()

            basketPage = basketPage.emptyBasket()

            assertEquals(basketPage.basketItems.size(), 0)
            assertTrue(basketPage.isContinueShoppingAllowed())
            assertTrue(basketPage.verifyTheEmptyBasketMessage())
        }

        @Test(groups = ["acquisitionShopTest"])
        public void applyVoucherOnDeviceAndVerifyPriceAndPromotionText() {
            def listPage = browsingJourney.startHandsetFirstJourney(true)
            def basketPage = listPage.selectDeviceByBrandAndModel("LG", "GW300")
                    .viewTariffs()
                    .selectPayAndGoTariffTile(0)
                    .clickOnContinueToBasketButton()

            assert basketPage.getDevice()
            assert basketPage.getTariff()

            basketPage = basketPage.expandAndApplyVoucherCode("FT_DEVICE_10")

            assert basketPage.getVoucherMessage() == "Promo code: FT_DEVICE_10"

            def device = basketPage.getDevice()
            assert device.getPromotionText() == "20 percentage off on devices as voucherable promotion"
            assert device.getUpfrontCost() == "£80.00\nWas £100.00";
        }

        @Test(groups = ["acquisitionShopTest"])
        public void verifyPromotionTextWhenPercentageDiscountIsAppliedOnAccessory() {
            def listPage = browsingJourney.startHandsetFirstJourney(true)
            def basketPage = listPage.selectDeviceByBrandAndModel("Samsung", "Standard")
                    .viewTariffs()
                    .selectPayMonthlyTariffTile(0)
                    .selectAccessoryTile(17)
                    .selectAccessoryTile(18)
                    .clickOnContinueToBasketButton()


            def accessories = basketPage.getAccessories()
            assert accessories.get(1).getPromotionText() == "Get a fantastic 50% off on charging plates when bought with samsung standard and iphone case"
            assert accessories.get(1).upfrontCost == "£7.50\nWas £15.00";
        }

        @Test(groups = ["acquisitionShopTest"])
        public void verifyPromotionTextWhenPercentageDiscountIsAppliedOnDevice() {
            def listPage = browsingJourney.startHandsetFirstJourney(true)
            def basketPage = listPage.selectDeviceByBrandAndModel("Samsung", "Standard")
                    .viewTariffs()
                    .selectPayMonthlyTariffTile(0)
                    .selectAccessoryTile(18)
                    .clickOnContinueToBasketButton()


            def device = basketPage.getDevice()
            assert device.getPromotionText() == "Get a fantastic 50% off on samsung standard when bought with charging plates"
            assert device.upfrontCost == "£165.00\nWas £329.99";
        }

        @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest", "precheckin"])
        public void "Verify 'apply unique limited voucher code' on basket page"() {
            def listPage = browsingJourney.startHandsetFirstJourney(true)
            def basketPage = listPage.selectDeviceByBrandAndModel("LG", "GW300")
                    .viewTariffs()
                    .selectPayAndGoTariffTile(0)
                    .selectAccessoryTile(0)
                    .selectAccessoryTile(1)
                    .selectAccessoryTile(2)
                    .clickOnContinueToBasketButton()
            basketPage = basketPage.expandAndApplyVoucherCode("UNIQUE_ACCOFF50")
            assert basketPage.getVoucherMessage() == "Promo name: 50 off UNIQUE"
            assert basketPage.isVoucherRemoveLinkVisible()
        }

        @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
        public void "verify basket page for errorCode when voucherable promotion does not promote any items from basket"() {

            def listPage = browsingJourney.startHandsetFirstJourney(true)
            def basketPage = listPage.selectDeviceByBrandAndModel("LG", "GW300")
                    .viewTariffs()
                    .selectPayAndGoTariffTile(0)
                    .clickOnContinueToBasketButton()

            basketPage = basketPage.expandAndApplyVoucherCode("ACCOFF50")
            assert basketPage.getVoucherMessage() == "Promo code: ACCOFF50"
            assert basketPage.isVoucherRemoveLinkVisible()

            assert basketPage.getApplyVoucherError() == "This promo code can't be used with this order. Remove it to continue."
            assert basketPage.voucherDiscount == "-£0.00"
        }

        @Test(groups = ["acquisitionShopTest", "mobileAcquisitionShopTest"])
        public void "verify that countdown timer on listing and details pages along with promotional and promotion expired messages"() {

            def listPage = browsingJourney.startHandsetFirstJourney(false)

            assert listPage.countdownTimerDisplayed
            assert listPage.countdownTimerPromoMessage == "50 % off on HTC One (M8). Offer ends soon"

            listPage = listPage.viewPayAsYouGo()

            assert listPage.countdownTimerDisplayed
            assert listPage.countdownTimerPromoMessage == "You just missed the offer 50 % off on HTC One (M8)"

            listPage = browsingJourney.startTabletFirstJourney()

            assert !listPage.countdownTimerDisplayed

            listPage = browsingJourney.startHandsetFirstJourney(false)
            def detailsPage = listPage.selectDeviceByBrandAndModel("lg", "g3")

            assert detailsPage.countdownTimerDisplayed
            assert detailsPage.countdownTimerPromoMessage == "50 % off on lg g3. Offer ends soon"

            listPage = browsingJourney.navigateToAccessoriesListPage()
            detailsPage = listPage.selectDeviceByName("Logitech V-10 Speakers")

            assert detailsPage.countdownTimerDisplayed
            assert detailsPage.countdownTimerPromoMessage == "50 % off on Logitech Speakers . Offer ends soon"
        }

        @Test(groups = ["acquisitionShopTest"])
        public void "verify sorting of prepay tariffs for phones"() {
            def listPage = browsingJourney.startHandsetFirstJourney(true,true)
            List<Plan> plans = listPage.selectDeviceByBrandAndModel("Blackberry", "Q5")
                            .viewTariffs().getPayAsYouGoGridPlans()

            //Verifying Big Talker plans
            assert plans[0].getName() == "Keep your credit and get"
            assert plans[0].getMinutes() == 600
            assert plans[1].getMinutes() == 900
            assert plans[2].getMinutes() == 300
            assert plans[3].getMinutes() == 600

            //Verifying International Sim plans
            assert plans[4].getName() == "Keep your credit and get"
            assert plans[4].getDataAllowanceText() == "200MB data"
            assert plans[5].getDataAllowanceText() == "700MB data"
            assert plans[6].getDataAllowanceText() == "100MB data"
            assert plans[7].getDataAllowanceText() == "300MB data"
            assert plans[5].getAdditionalPlanDetails() == "International Sim plan at 1p/min"

            listPage = browsingJourney.startHandsetFirstJourney(true)
            plans = listPage.selectDeviceByBrandAndModel("lg", "gw300")
                    .viewTariffs().getPayAsYouGoGridPlans()

            //Verifying Big Bundle plans
            assert plans[0].getName() == "200MB data"
            assert plans[1].getName() == "500MB data"
            assert plans[2].getName() == "700MB data"

            listPage = browsingJourney.startHandsetFirstJourney(true,true)
            plans = listPage.selectDeviceByBrandAndModel("Samsung", "s3100")
                    .viewTariffs().getPayAsYouGoGridPlans()

            assert plans[0].getAdditionalPlanDetails() == "Big Talker plan at 1p/min"
        }

    @Test(groups = ["acquisitionShopTest"])
    public void "verify accessory promo for prepay journey"() {
        def listPage = browsingJourney.startHandsetFirstJourney(true,true)
        listPage.payAsYouGoTab().click()
        TariffsAndExtrasPage tariffsAndExtrasPage = listPage.selectDeviceByBrandAndModel("lg", "cookie")
            .viewTariffs()
        tariffsAndExtrasPage.viewPayAsYouGoTariffs()

        assertTrue(tariffsAndExtrasPage.accessoryPromoDisplayed())
        assertTrue(tariffsAndExtrasPage.accessoryPromoIsOfSingleSize())
        assertTrue(tariffsAndExtrasPage.getAccessoryPromoText().contains("O2 Insurance"))

        tariffsAndExtrasPage.selectPayAndGoTariffTile(0)
        assertTrue(tariffsAndExtrasPage.accessoryPromoDisplayed())
        assertTrue(tariffsAndExtrasPage.accessoryPromoIsOfSingleSize())
        assertTrue(tariffsAndExtrasPage.getAccessoryPromoText().contains("The stunning iPhone 6S"))
    }

        @Test(groups = ["acquisitionShopTest"])
        public void "verify tariffs and extra page when payg tariffs are selected"() {
            def listPage = browsingJourney.startHandsetFirstJourney(true,true, "payasyougo")
            def tariffsAndExtraPage = listPage.selectDeviceByBrandAndModel("lg", "l30")
                            .viewTariffs()

            //Verifying Big Bundle plans in package section
            assert tariffsAndExtraPage.bigBundlesInfoText == "Get the biggest amount of data, as well as minutes and texts."
            assert tariffsAndExtraPage.payAsYouGoGridPlans[0].recommendedSash
            assert tariffsAndExtraPage.payAsYouGoGridPlans[0].promotionRibbonText == "1 000 000 free o2 to o2 minutes"

            tariffsAndExtraPage = tariffsAndExtraPage.selectPayAndGoTariffTile(0)

            assert tariffsAndExtraPage.packageSection.payGPlanName == "${BIG_BUNDLES.name} £230 top up"

            assert tariffsAndExtraPage.packageSection.payGPlanDetails == "£230 top up\n" +
                    "200 texts\n" +
                    "565 minutes"

            //Verifying Big Talker plans
            assert tariffsAndExtraPage.bigTalkersInfoText == "Top up, use your credit, and get a free allowance of minutes, texts and data."
            assert tariffsAndExtraPage.payAsYouGoGridPlans[2].promotionRibbonText == "1 000 000 free o2 to o2 minutes 1"

            tariffsAndExtraPage = tariffsAndExtraPage.selectPayAndGoTariffTile(1)

            assert tariffsAndExtraPage.packageSection.payGPlanName == "Simply Pay As You Go £220 top up"

            assert tariffsAndExtraPage.packageSection.payGPlanDetails == "£220 top up\n" +
                    "3GB data\n" +
                    "Unlimited texts\n" +
                    "4500 minutes"

            //Verifying International Sim plans
            assert tariffsAndExtraPage.internationalSimsInfoText == "Stay in touch with friends and family abroad. Get international calls from 1p a minute."
            tariffsAndExtraPage = tariffsAndExtraPage.selectPayAndGoTariffTile(4)
            assert tariffsAndExtraPage.packageSection.payGPlanName == "International Sim £225 top up"

            assert tariffsAndExtraPage.packageSection.payGPlanDetails == "£225 top up\n" +
                    "9GB data\n" +
                    "100 texts\n" +
                    "6670 minutes"

            // dont truncate promotion ribbon text for payG tariffs and allows them to span 3 lines
            assert tariffsAndExtraPage.payAsYouGoGridPlans[5].promotionRibbonText == "Get £10 credit, on us. When you order your sim here and top up within 30 days."
        }

    @Test(groups = ["acquisitionShopTest"])
    public void "test reassurance message hidden for browsing "(){

        def accessoryDetailsPage = browsingJourney.navigateToAccessoriesListPage().selectDeviceByName("SanDisk Memory Card")
        accessoryDetailsPage.addAccessoryToBasket()
        def accessoryBasketPage = accessoryDetailsPage.goToBasket()
        assertFalse(accessoryBasketPage.getSummarySection().isReassuranceMessageDisplayed())

        def tabletBasketPage = browsingJourney
                .startTabletFirstJourney(true)
                .selectDeviceByBrandAndModel("Google", "Nexus 7")
                .recommendedTariff()
                .clickOnAddDeviceTariffToBasketInRecommendedSection()
                .clickOnContinueToBasketButton()

        assertFalse(tabletBasketPage.getSummarySection().isReassuranceMessageDisplayed())

        def phoneBasketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("samsung", "standard")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .selectInsuranceTile(0)
                .selectAccessoryTile(3)
                .clickOnContinueToBasketButton()

        assertFalse(phoneBasketPage.getSummarySection().isReassuranceMessageDisplayed())
    }

    @Test(groups = "acquisitionShopTest")
    public void "validate display of device and tariff specific insurances"() {


        TariffsAndExtrasPage tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true)
                                                    .selectDeviceByBrandAndModel("apple", "iPhone 5 64GB Purple cca")
                                                    .viewTariffs()

        assert !tariffsAndExtrasPage.isInsuranceSectionVisible()
        tariffsAndExtrasPage = tariffsAndExtrasPage.selectPayMonthlyTariffTile(0)
        assert tariffsAndExtrasPage.isInsuranceSectionVisible()
        tariffsAndExtrasPage = tariffsAndExtrasPage.selectInsuranceTileByName("Generic Handset Insurance")

        tariffsAndExtrasPage = tariffsAndExtrasPage.showAllPlans().selectPayMonthlyTariffTile(15)
        tariffsAndExtrasPage = tariffsAndExtrasPage.selectInsuranceTileByName("Generic Handset Insurance")
        tariffsAndExtrasPage = tariffsAndExtrasPage.selectInsuranceTileByName("Generic Handset Insurance")

        tariffsAndExtrasPage = tariffsAndExtrasPage.selectPayMonthlyTariffTile(0)
        tariffsAndExtrasPage = tariffsAndExtrasPage.selectInsuranceTileByName("Generic Handset Insurance")

        tariffsAndExtrasPage = tariffsAndExtrasPage.showAllPlans().selectPayMonthlyTariffTile(15)
        tariffsAndExtrasPage = tariffsAndExtrasPage.selectInsuranceTileByName("Generic Handset Insurance")

        def basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()

        assert basketPage.insurances[0].title == "Generic Handset Insurance"
        assert basketPage.device.deviceName == "Apple iPhone 5 64GB Purple cca"

        def tariff = basketPage.getTariff()
        assert tariff.minutes() == "600 mins"
        assert tariff.texts() == "Unlimited texts"
        assert tariff.tariffLength() == "24 month contract"
    }

    @Test(groups = "acquisitionShopTest")
    // commenting the ft as part of change in ECOM-8603
    // this needs to be enabled once ECOM-8635 is played
    public void "validate change accessory functionality on basket page"() {

        // pay monthly
        def tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("lg", "g3")
                .viewTariffs().viewPayMonthlyTariffs()

        tariffsAndExtrasPage.selectPayMonthlyTariffTile(0)
        tariffsAndExtrasPage.selectAccessoryTile(0)
        tariffsAndExtrasPage.clickOnContinueToBasketButton()

        def accessoryDetailsPage = browsingJourney.navigateToAccessoriesListPage().showAll().selectDeviceByBrandAndModel("Iphone","Screenguard")
        accessoryDetailsPage.addAccessoryToBasket()
        def basketPage = accessoryDetailsPage.goToBasket()


        def accessories = basketPage.getAccessories()
        assert !accessories.get(0).changeAccessoryLinkDisplayed()
        assert accessories.get(1).changeAccessoryLinkDisplayed()

        tariffsAndExtrasPage = accessories.get(1).changeAccessoryAndRedirectToTariffsPage() as TariffsAndExtrasPage
        assert tariffsAndExtrasPage.isOnPage()

        // pay and go
        tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true)
                                                .selectDeviceByBrandAndModel("apple", "iphone-5s")
                                                .viewTariffs()
                                                .viewPayAsYouGoTariffs()

        tariffsAndExtrasPage.selectPayAndGoTariffTile(0)
        tariffsAndExtrasPage.clickOnContinueToBasketButton()

        accessoryDetailsPage = browsingJourney.navigateToAccessoriesListPage().showAll().selectDeviceByBrandAndModel("Iphone","Screenguard")
        accessoryDetailsPage.addAccessoryToBasket()
        basketPage = accessoryDetailsPage.goToBasket()

        accessories = basketPage.getAccessories()
        assert accessories.get(0).changeAccessoryLinkDisplayed()

        def deviceListingPage = accessories.get(0).changeAccessoryAndRedirectToAccessoryLandingPage()
        assert deviceListingPage.isAccessoryPage()
        assert deviceListingPage.isAccessoryLandingPage(browser().driver())
    }


    @Test(groups = "acquisitionShopTest")
    public void "validate choose different item functionality on basket page"() {

        // device + wearable
        def tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("lg", "g3")
                .viewTariffs().viewPayMonthlyTariffs()

        tariffsAndExtrasPage.selectPayMonthlyTariffTile(0)
        tariffsAndExtrasPage.selectAccessoryTile(0)
        tariffsAndExtrasPage.clickOnContinueToBasketButton()

        def deviceListingPage =  browsingJourney.navigateToWearablesListPage("smartwatches")
        deviceListingPage.deviceOnlyTab().click()

        def deviceDetailsPage = deviceListingPage.selectDeviceAt(3)
        deviceDetailsPage.addToBasket()
        def basketPage = deviceDetailsPage.goToBasket()

        deviceListingPage = basketPage.accessories[0].changeAccessoryAndRedirectToDeviceListingPage(browser().driver())
        assert deviceListingPage.devices()[3].title == "Samsung Galaxy Gear 2"

        // only wearable
         //fitness trackers
        deviceListingPage =  browsingJourney.startFitnessTrackersFirstJourney()
        deviceListingPage.deviceOnlyTab().click()

        deviceDetailsPage = deviceListingPage.selectDeviceAt(1)
        deviceDetailsPage.addToBasket()
        basketPage = deviceDetailsPage.goToBasket()

        deviceListingPage = basketPage.accessories[0].changeAccessoryAndRedirectToDeviceListingPage(browser().driver())
        assert deviceListingPage.devices()[1].title == "Samsung Fitness Band non sim2"


         //tablets
        browsingJourney.startTabletFirstJourney()
        deviceListingPage.deviceOnlyTab().click()

        deviceDetailsPage = deviceListingPage.selectDeviceAt(3)
        deviceDetailsPage.addToBasket()
        basketPage = deviceDetailsPage.goToBasket()

        deviceListingPage = basketPage.accessories[0].changeAccessoryAndRedirectToDeviceListingPage(browser().driver())
        assert deviceListingPage.devices()[3].title == "Google Nexus 7 Wifi2"
    }

    @Test(groups = "acquisitionShopTest")
    public void "should redirect to accessory listing page when basket has only accesscories and accessory is out of stock"(){
        def accessoryDetailsPage = browsingJourney.navigateToAccessoriesListPage().showAll().selectDeviceByBrandAndModel("Iphone","Screenguard")
        accessoryDetailsPage.addAccessoryToBasket()
        def basketPage = accessoryDetailsPage.goToBasket()


        def accessories = basketPage.getAccessories()
        assert accessories.get(0).changeAccessoryLinkDisplayed()

        def deviceListingPage = accessories.get(0).changeAccessoryAndRedirectToAccessoryLandingPage()
        assert deviceListingPage.isAccessoryPage()
        assert deviceListingPage.isAccessoryLandingPage(browser().driver())
    }

    @Test(groups = "acquisitionShopTest")
    public void "verify delivery message for accessory on accessory detail page"(){
        // when delivery message is configured
        def accessoryDetailsPage = browsingJourney.navigateToAccessoriesListPage().showAll().selectDeviceByBrandAndModel("Sony","QX10 Smart Lens White")
        assert "You will get accessory within a week" == accessoryDetailsPage.getDeliveryMessage()

        // default message
        accessoryDetailsPage = browsingJourney.navigateToAccessoriesListPage().selectDeviceAt(0)
        assert "Free delivery via Royal Mail within three working days" == accessoryDetailsPage.getDeliveryMessage()
    }

    @Test(groups = "upgradeShopTest")
    public void "404 error page should display top devices"() {

        def shopErrorPage = browsingJourney.navigateToErrorPage("non-existent")
        assert shopErrorPage.noOfTopDevicesShown() == 4

        //nonconnected device in search
        shopErrorPage.typeDeviceToSearch("wifi")
        assertThat(shopErrorPage.deviceSearchSection, is(aNewDeviceSearch().
                withSearchResults("wifi",4)))

        //accessory device in search
        shopErrorPage.typeDeviceToSearch("sandisk")
        assertThat(shopErrorPage.deviceSearchSection, is(aNewDeviceSearch().
                withSearchResults("sandisk",1)))
        //payg device in search
        shopErrorPage.typeDeviceToSearch("L30")
        assertThat(shopErrorPage.deviceSearchSection, is(aNewDeviceSearch().
                withSearchResults("L30",1)))

        shopErrorPage.typeDeviceToSearch("Apple Mini CCA5")
        assertThat(shopErrorPage.deviceSearchSection, is(aNewDeviceSearch().
                withSearchResults("Apple Mini CCA5",1)
                .withSearchResultsCorrectlyLinkedToDetailsPage("upgrade/store/phones/apple/mini-cca5/#contractType=paymonthly")))

        shopErrorPage.typeDeviceToSearch("Apple ipad Air 2")
        assertThat(shopErrorPage.deviceSearchSection, is(aNewDeviceSearch().
                withSearchResults("Apple ipad Air 2",1)
                .withSearchResultsCorrectlyLinkedToDetailsPage("upgrade/store/tablets/apple/ipad-air-2/#contractType=paymonthly")))

        assertThat(shopErrorPage, is(aErrorPage()
                .withTopDeviceAt(1, aDeviceTile()
                .withName("LG Cookie"))
                .withTopDeviceAt(2, aDeviceTile()
                .withName("LG G3"))
                .withTopDeviceAt(3, aDeviceTile()
                .withName("Sony Ericsson Jalou"))
                .withTopDeviceAt(4, aDeviceTile()
                .withName("Nokia 2730"))))
    }

    @Test(groups = "acquisitionShopTest")
    public void "Should add multiple nonconnected wearables with device in basket"() {

        def deviceListPage = browsingJourney.startHandsetFirstJourney()
        def deviceDetailsPage = deviceListPage.selectDeviceByBrandAndModel("Apple", "iPhone 5s")
        deviceDetailsPage.viewTariffs().selectPayMonthlyTariffTile(0).clickOnContinueToBasketButton()

        deviceDetailsPage = browsingJourney.navigateToDeviceDetailsPageWithSubtype("Samsung", "Galaxy Gear", "smartwatches")
        deviceDetailsPage.addToBasket()
        deviceDetailsPage.waitForAddToBasketButtonTobeEnabled()
        deviceDetailsPage.goToBasket()

        deviceDetailsPage = browsingJourney.navigateToDeviceDetailsPageWithSubtype("Samsung", "Proxima", "smartwatches")
        deviceDetailsPage.addToBasket()
        deviceDetailsPage.waitForAddToBasketButtonTobeEnabled()
        def basket = deviceDetailsPage.goToBasket()

        assert basket.basketItems.size() == 6

        assert basket.device.title == "Apple iPhone 5s"
        def accessories = basket.accessories
        assert accessories[0].title == "Samsung Galaxy Gear Non-Sim"
        assert accessories[1].title == "Samsung Proxima Non-Sim"
    }

    @Test(groups = "acquisitionShopTest")
    public void "Should add multiple nonconnected wearables in basket"() {

        def deviceListPage = browsingJourney.startSmartwatchesFirstJourney(false, false, "nonconnected")
        def deviceDetailsPage = deviceListPage.selectDeviceByBrandAndModel("Samsung", "Galaxy Gear")
        deviceDetailsPage.addToBasket()
        deviceDetailsPage.waitForAddToBasketButtonTobeEnabled()
        deviceDetailsPage.goToBasket()

        deviceDetailsPage = browsingJourney.navigateToDeviceDetailsPageWithSubtype("Samsung", "Proxima", "smartwatches")

        deviceDetailsPage.addToBasket()
        deviceDetailsPage.waitForAddToBasketButtonTobeEnabled()
        def basket = deviceDetailsPage.goToBasket()

        assert basket.basketItems.size() == 3


        def accessories = basket.accessories
        assert accessories[0].title == "Samsung Galaxy Gear Non-Sim"
        assert accessories[0].upfrontCost == "£97.99"
        assert accessories[1].title == "Samsung Proxima Non-Sim"
        assert accessories[1].upfrontCost == "£96.99"

        assert basket.upfrontTotal == "194.98"
        assert basket.monthlyTotal == "0.00"
    }

    @Test(groups = "acquisitionShopTest")
    public void "should not show collection tile when basket is not eligible for CnC and CnCN"() {
        def accessoriesListPage = browsingJourney.startAccessoriesFirstJourney(false,true)
        def detailsPage = accessoriesListPage.selectDeviceByBrandAndModel("sony","qx10-smart-lens-white")
        detailsPage.addAccessoryToBasket()
        def basketPage = detailsPage.goToBasket()

        assert basketPage.clickAndCollectCollectionTileHidden()
    }

    @Test(groups = "acquisitionShopTest")
    public void "verify delivery message on basket page having accessories only"() {
        def basketPage = browser().navigateToDeviceDetailsPageWithSubType("sandisk","memory-card","accessories")
                .addAccessoryToBasket().goToBasket()

        basketPage = browser().navigateToDeviceDetailsPageWithSubType("samsung", "hm3200-wave-bluetooth-headset","accessories")
                .addAccessoryToBasket().goToBasket()

        assertEquals(basketPage.getDelivery().getInStockMessage(), "Free delivery via Royal Mail within three working days")
        assertEquals(basketPage.getOutOfStockMessageRightContainer(), "Free delivery")
    }

    @Test(groups = "acquisitionShopTest")
    public void "verify delivery message on basket page having device and accessories"() {
        def tariffsAndExtrasPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "GW300")
                .viewTariffs()
                .selectBigBundleTariffTile()
                .selectAccessoryTile(0)

        def basketPage = tariffsAndExtrasPage.clickOnContinueToBasketButton()

        basketPage = browser().navigateToDeviceDetailsPageWithSubType("sandisk","memory-card","accessories")
                .addAccessoryToBasket().goToBasket()

        basketPage = browser().navigateToDeviceDetailsPageWithSubType("samsung", "hm3200-wave-bluetooth-headset","accessories")
                .addAccessoryToBasket().goToBasket()

        assertEquals(basketPage.getDelivery().getInStockMessage(), "Order by midnight for free next working day delivery2")
        assertEquals(basketPage.getInStockMessageRightContainer(), "Free next day delivery")
    }

    @Test(groups = "acquisitionShopTest")
    public void verifyOverriddenDeliveryMessageOnBasketPageForAccessoriesWithDevice() {
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("samsung", "standard")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .selectAccessoryTile(17)
                .selectAccessoryTile(18)
                .clickOnContinueToBasketButton()

        assertEquals(basketPage.getDevice().deviceName, "Samsung Standard")
        assertEquals(basketPage.getDelivery().getInStockMessage(), "Order by midnight for free next working day delivery2")
        assertEquals(basketPage.getInStockMessageRightContainer(), "Free next day delivery")
    }

    //ECOM-8501
    @Test(groups = "acquisitionShopTest")
    public void verifyDeliveryMessageOnBasketPageForNonConnectedDevices()
{
    // delayed-delivery
     def deviceListPage = browsingJourney.startSmartwatchesFirstJourney(false,false, "nonconnected")
     def deviceDetailsPage = deviceListPage.selectDeviceByBrandAndModel("Samsung", "Proxima")
     deviceDetailsPage.addToBasket()
     deviceDetailsPage.waitForAddToBasketButtonTobeEnabled()
     def basketPage = deviceDetailsPage.goToBasket()

     assertFalse(basketPage.isDeliveryInformationContentDisplayedForDevice())
     basketPage.emptyBasket()

    //OOS
    deviceListPage = browsingJourney.startFitnessTrackersFirstJourney(false,false, "nonconnected")
    deviceDetailsPage = deviceListPage.selectDeviceByBrandAndModel("Samsung", "fitness band non sim2")
    deviceDetailsPage.addToBasket()
    deviceDetailsPage.waitForAddToBasketButtonTobeEnabled()
    basketPage = deviceDetailsPage.goToBasket()

    assertFalse(basketPage.isDeliveryInformationContentDisplayedForDevice())
    basketPage.emptyBasket()

    //Pre-Order
    deviceListPage = browsingJourney.startTabletFirstJourney(false,false, "nonconnected")
    deviceDetailsPage = deviceListPage.selectDeviceByBrandAndModel("Google", "Nexus 7 Wifi Preorder")
    deviceDetailsPage.addToBasket()
    deviceDetailsPage.waitForAddToBasketButtonTobeEnabled()
    basketPage = deviceDetailsPage.goToBasket()

    assertFalse(basketPage.isDeliveryInformationContentDisplayedForDevice())

}
    //ECOM-8501
    @Test(groups = "acquisitionShopTest")
    public void verifyDeliveryMessageOnBasketPageForMultipleItems()
    {
        //Scenario-1  Device - delayed delivery
        def basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "G3")
                .selectColourByName("Silver")
                .selectCapacityByName("128MB")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .clickOnContinueToBasketButton()

        //Add an In-Stock accessory
        basketPage = browser().navigateToDeviceDetailsPageWithSubType("sandisk","memory-card","accessories")
                    .addAccessoryToBasket().goToBasket()

        assertFalse(basketPage.isDeliveryInformationContentDisplayedForDevice())
        basketPage.emptyBasket()

        //Scenario-2  Device - Pre-order

        basketPage = browsingJourney.startHandsetFirstJourney(true)
                .selectDeviceByBrandAndModel("LG", "G3")
                .selectColourByName("Silver")
                .selectCapacityByName("32GB")
                .viewTariffs()
                .selectPayMonthlyTariffTile(0)
                .clickOnContinueToBasketButton()

        //Add an In-Stock accessory
        basketPage = browser().navigateToDeviceDetailsPageWithSubType("sandisk","memory-card","accessories")
                .addAccessoryToBasket().goToBasket()

        assertFalse(basketPage.isDeliveryInformationContentDisplayedForDevice())

    }

    @Test(groups = "acquisitionShopTest")
    public void "should complete journey for refurb paymonthly phone"(){

        def basketPage = browsingJourney.startHandsetFirstJourney(true, true).filterBy(allDevices()
                .withCondition("Like New"))
                .selectDeviceByBrandAndModel("Apple","iPhone 5s Like New")
                .viewTariffs()
                .selectPayMonthlyTariffTile(1)
                .clickOnContinueToBasketButton()

        basketPage.device.deviceName == "Apple iPhone 5s Like New"
        basketPage.tariff
    }

}


