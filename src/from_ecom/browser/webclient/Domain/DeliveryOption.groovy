package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator

class DeliveryOption {

    String amount
    String period
    String oneOff
    String description
    String met
    String type

    @JsonCreator
    DeliveryOption(){

    }
}
