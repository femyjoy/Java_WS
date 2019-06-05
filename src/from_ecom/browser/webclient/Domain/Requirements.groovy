package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator

class Requirements {
    DeliveryAddress deliveryAddress
    DeliveryOption deliveryOption

    @JsonCreator
    requirements(){
    }
}
