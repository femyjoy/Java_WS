package from_ecom.browser.webclient

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import webclient.Domain.*

import static webclient.ProductCatalogueServiceGateway.StockStatus.InStock
import static webclient.ProductCatalogueServiceGateway.StockStatus.OutOfStock
import static webclient.ProductResourcesClient.ServiceType.BasketService
import static webclient.ProductResourcesClient.ServiceType.CustomerService
import static webclient.ProductResourcesClient.ServiceType.ExternalFakes
import static webclient.ProductResourcesClient.ServiceType.OrderService
import static webclient.ProductResourcesClient.ServiceType.ProductService
import static webclient.ProductResourcesClient.ServiceType.VoucherSimulator

class ProductCatalogueServiceGateway {

    ProductResourcesClient client;

    public static ProductCatalogueServiceGateway createBasicInstance() {
        return new ProductCatalogueServiceGateway(new ProductResourcesClient(ProductCatalogueClientHeadersFactory.customerAcquisition("ID-000100")))
    }

    public ProductCatalogueServiceGateway(ProductResourcesClient client) {
        this.client = client;
    }

    def String newBasketIdForUpgrade(String userId, String msisdn) {
        CustomerLookup lookup = this.client.get("customers/" + userId, CustomerLookup.class, CustomerService);
        String sharedBasket = lookup.msisdns.find { it -> it.msisdn == msisdn }.links.find { it -> it.rel == "urn:o2:getSharedBasket" }.href
        return client.get(sharedBasket, Basket.class, BasketService).id;
    }

    def String newPrivateBasket(String userId) {
        CustomerLookup lookup = this.client.get("customers/" + userId, CustomerLookup.class, CustomerService);
        String basket = lookup.portals.head().links.find { it.rel == "urn:o2:createPrivateBasket" }.href
        return this.client.post(basket, Basket.class, BasketService).id
    }

    def String newAnonymousBasket() {
        return this.client.post("baskets/createAnonymousBasket", Basket.class, BasketService).id
    }

    def getSavedBasket(String email) {
        Thread.sleep(2000) //to wait for emailscheduler to finish
        BasketEmail emailResponse = client.get("emails/savedbasket/$email", BasketEmail, ExternalFakes)
        assert emailResponse
        emailResponse
    }

    def getProduct(String productRef){
        client.getAsMap("admin/$productRef", ProductService)
    }

    def saveProduct(String productRef, def data){
        client.put("admin/$productRef", data, ProductService)
    }

    def getVoucherDetails(String voucherCode) {
        client.getAsMap("voucher/voucherCode/$voucherCode", VoucherSimulator)
    }

    def saveVoucher(def voucher) {
        client.put("voucher/", voucher, VoucherSimulator)
    }

    def deleteVoucher(String voucherId) {
        client.delete("voucher/$voucherId", VoucherSimulator)
    }

    enum StockStatus {
        InStock("InStock"), OutOfStock("OutOfStock")

        private StockStatus(String asString) {
            this.asString = asString
        }

        public final String asString;

        public StockStatus other() {
            this == InStock ? OutOfStock : InStock
        }
    }

    public void makeItemOutOfStock(String itemType, String itemId) throws Exception {
        setItemStockStatus(itemType, itemId, StockStatus.OutOfStock)
    }

    public void makeItemInStock(String itemType, String itemId) throws Exception {
        setItemStockStatus(itemType, itemId, StockStatus.InStock)
    }

    private void setItemStockStatus(String itemType, String itemId, StockStatus stock) {
        String productUrl = "admin/" + itemType + "/" + itemId

        String response = client.getAsString(productUrl, ProductService)
        def responseMap = new JsonSlurper().parseText(response)
        responseMap.classification?.colour?.remove('canonical')
        responseMap.classification?.memory?.remove('canonical')
        if (responseMap.stockInfo) {
            responseMap.stockInfo.stock = stock.asString
        } else {
            responseMap.stock = stock.asString
        }
        JsonBuilder jsonBuilder = new JsonBuilder(responseMap)

        client.putAsString(productUrl, jsonBuilder.toPrettyString(), ProductService)
    }

    // what does def void even mean?
    def void resetProductCatalogueFixtures() {
        // no-op for now!
        //client.getWithNoResult("http://localhost:8080/ecommFixtures/reset")
    }

    def Basket getBasket(String basketId) { return this.client.get("baskets/" + basketId, Basket.class, BasketService) }

    def emptyBasket(String basketId) { this.client.post("baskets/" + basketId + "/empty", BasketService) }

    def deleteOrder(String orderId) { this.client.delete("orders/" + orderId, OrderService) }

    def addHandsetToBasket(String basketId, String handsetId) {
        this.client.post("baskets/" + basketId + "/add/device/" + handsetId, BasketService)
    }

    def addBolton(String basketId, String boltonId) {
        this.client.post("baskets/" + basketId + "/add/bolton/" + boltonId, BasketService)
    }

    def getBolton(String boltonId) { this.client.get("products/bolton/" + boltonId, Bolton.class, ProductService) }

    def addTariff(String basketId, String tariffId) {
        this.client.post("baskets/" + basketId + "/add/plan/" + tariffId, BasketService)
    }

    def addDataAllowance(String basketId, String dataAllowanceId) {
        this.client.post("baskets/" + basketId + "/add/dataallowance/" + dataAllowanceId, BasketService)
    }

    def addPrepaySim(String basketId, String simId) {
        this.client.post("baskets/" + basketId + "/add/prepaySim/" + simId, BasketService)
    }

    def addAccessory(String basketId, String accessoryId) {
        this.client.post("baskets/" + basketId + "/add/accessory/" + accessoryId, BasketService)
    }

    def addLoyaltyDiscount(String basketId, String loyaltyDiscountId) {
        this.client.post("baskets/" + basketId + "/add/loyaltyDiscount/" + loyaltyDiscountId, BasketService)
    }

    def setSecciAsViewed(String orderId) {
        this.client.put("orders/" + orderId + "/consumerCredit/secciViewed", null, OrderService)
    }

    def generateCCAAgreement(String orderId) {
        this.client.put("orders/" + orderId + "/consumerCredit/generateCca", null, OrderService)
    }

    def setPortalId(String orderId, String portalId) {
        this.client.put("orders/" + orderId + "/portalId", ['portalId': portalId], OrderService)
    }

    def addInsurance(String basketId, String insuranceId) {
        this.client.post("baskets/" + basketId + "/add/insurance/" + insuranceId, BasketService)
    }

    def getInsurance(String insuranceId) {
        this.client.get("products/insurance/" + insuranceId, Insurance.class, ProductService)
    }

    def Order makeBasketAnOrder(String basketId) {
        def orderId = this.client.postData("orders", [basketId: basketId], CreatedOrder.class, OrderService).id
        return this.client.get("orders/" + orderId, Order.class, OrderService)
    }

    def CustomerIdentity getCustomerIdentityFromMsisdn(String msisdn) {
        return this.client.get("customers;type=msisdn/" + msisdn, CustomerIdentity.class, CustomerService)
    }

    def setDirectDebitAccountDetails(String orderId, PCAccountDetails details) {
        this.client.put("orders/" + orderId + "/directDebitAccount", details, OrderService)
    }

    def setCreditCheckDetails(String orderId, CreditCheckData data) {
        this.client.put("orders/" + orderId + "/creditCheckDetails", data, OrderService)
    }

    def modifyUnresolvedPrice(String basketId, int oneOffPrice, int monthlyPrice) {
        def itemId = getBasket(basketId).errors.find { it -> it.key == "UnresolvedPrice" }.value.items.first()
        this.client.put("baskets/" + basketId + "/modify/" + itemId + "/price", [oneOff: oneOffPrice, monthly: monthlyPrice], BasketService)
    }

    def setOrderAsEarlyUpgrade(String orderId) {
        this.client.put("orders/" + orderId + "/upgradeEntitlementDetails/", [upgradeEntitlementOverrideApplied: false, earlyUpgradeOffered: true, earlyUpgradeTaken: true], OrderService)
    }

    def Address getAddress(String orderId) {
        Order myOrder = this.client.get("orders/" + orderId, Order.class, OrderService)
        return myOrder.getRequirements().getDeliveryAddress().getAddress()
    }

    def setAddress(String orderId, Address address) {
        this.client.put("orders/" + orderId + "/deliveryAddress", address, OrderService)
    }

    def DeliveryOption getDeliveryOption(String orderId) {
        Order myOrder = this.client.get("orders/" + orderId, Order.class, OrderService)
        return myOrder.getRequirements().getDeliveryOption()
    }

    def setDeliveryOption(String orderId, DeliveryOption deliveryOption) {
        this.client.put("orders/" + orderId + "/deliveryOption", deliveryOption, OrderService)
    }

}
