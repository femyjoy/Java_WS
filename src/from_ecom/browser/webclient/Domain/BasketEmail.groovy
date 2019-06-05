package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator

class BasketEmail {

    String basketId
    String uniqueReferenceNumber
    String basketUrl

    @JsonCreator
    BasketEmail() {
    }
}
