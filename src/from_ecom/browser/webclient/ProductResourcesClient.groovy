package from_ecom.browser.webclient

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import net.sf.json.JSONObject

import static framework.Configuration.productCatalogueUrl
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.DELETE
import static webclient.ProductResourcesClient.ServiceType.EcommFixture

public class ProductResourcesClient {

    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> headers;

    public ProductResourcesClient(Map<String, String> clientHeaders) {
        try {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            headers = clientHeaders;
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not build client " + e.getMessage());
        }
    }

    // FIXME : doesn't seem to work when url contains query params, also doesn't seem to work when result is a json array rather than a json object
    public <T> T get(String endpoint, Class<T> parseJsonAsType, ServiceType serviceType) {
        def responseJson = this.createHttpBuilder().get(path: handleAbsoluteUrl(endpoint, serviceType)) as JSONObject
        mapper.readValue(responseJson.toString(), parseJsonAsType)
    }

    def getAsMap(String endpoint, ServiceType serviceType) {
        this.createHttpBuilder().get(path: handleAbsoluteUrl(endpoint, serviceType))
    }

    public String getAsString(String endpoint, ServiceType serviceType) {
        def responseJson = this.createHttpBuilder().get(path: handleAbsoluteUrl(endpoint, serviceType)) as JSONObject
        responseJson.toString()
    }

    public void getWithNoResult(String endpoint) {
        this.createHttpBuilder().get(path: this.handleAbsoluteUrl(endpoint, ServiceType.EcommFixture))
    }

    public void postToFakes(String endpoint, String query) {
        this.createHttpBuilder().post(path: this.handleAbsoluteUrl(endpoint, ServiceType.EcommFixture), queryString: query)
    }

    public void put(String endPoint, Object data, ServiceType serviceType) {
        def putMap = [path: handleAbsoluteUrl(endPoint, serviceType), contentType: JSON]
        if (data != null) {
            putMap.put("body", mapper.writeValueAsString(data))
        }

        this.createRestClient().put(putMap)
    }

    public void putAsString(String endPoint, String data, ServiceType serviceType) {
        def putMap = [path: handleAbsoluteUrl(endPoint, serviceType), contentType: JSON]
        if (data != null) {
            putMap.put("body", data)
        }

        this.createRestClient().put(putMap)
    }

    public <T> T postData(String endPoint, Object data, Class<T> parseResponseAsType, ServiceType serviceType) {
        def postMap = [path: handleAbsoluteUrl(endPoint, serviceType),
                contentType: JSON,
                body: mapper.writeValueAsString(data)]
        def responseJson = this.createHttpBuilder().post(postMap) as JSONObject
        mapper.readValue(responseJson.toString(), parseResponseAsType)
    }

    public void postData(String endPoint, Object data, ServiceType serviceType) {
        def postMap = [path: handleAbsoluteUrl(endPoint, serviceType),
                contentType: JSON,
                body: mapper.writeValueAsString(data)]
        this.createHttpBuilder().post(postMap)
    }

    public <T> T post(String endPoint, Class<T> parseResponseAsType, ServiceType serviceType) {
        def postMap = [path: handleAbsoluteUrl(endPoint, serviceType), contentType: JSON]
        def responseJson = this.createHttpBuilder().post(postMap) as JSONObject
        mapper.readValue(responseJson.toString(), parseResponseAsType)
    }

    public void post(String endPoint, ServiceType serviceType) {
        def postMap = [path: handleAbsoluteUrl(endPoint, serviceType), contentType: JSON]
        this.createHttpBuilder().post(postMap)
    }

    public void post(String endPoint, ServiceType serviceType, Map queryParams) {
        def postMap = [path: handleAbsoluteUrl(endPoint, serviceType), query: queryParams, contentType: JSON]
        this.createHttpBuilder().post(postMap)
    }

    public void delete(String endPoint, ServiceType serviceType) {
        this.createHttpBuilder().request(DELETE) {
            uri.path = handleAbsoluteUrl(endPoint, serviceType)

            response.failure = { resp ->
                throw new RuntimeException("Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}");
            }
        }
    }

    enum ServiceType {
        ProductService('productService/'),
        CustomerService('customerService/'),
        BasketService('basketService/'),
        OrderService('orderService/v2/'),
        VoucherSimulator('voucherSimulator/'),
        EcommFixture('ecommFixtures/'),
        ExternalFakes('ecommExternalFakes/');

        private final String url

        ServiceType(String service) {
            this.url = productCatalogueUrl + service
        }

        String getUrl() {
            url
        }
    }

    private String handleAbsoluteUrl(String url, ServiceType serviceType) {
        url.contains("http") ?: serviceType.url + url
    }

    private HTTPBuilder createHttpBuilder() {
        def builder = new HTTPBuilder(productCatalogueUrl)
        builder.setHeaders(this.headers)
        builder.ignoreSSLIssues()
        return builder
    }

    private RESTClient createRestClient() {
        def client = new RESTClient(productCatalogueUrl)
        client.setHeaders(this.headers)
        client.ignoreSSLIssues()
        return client
    }
}
