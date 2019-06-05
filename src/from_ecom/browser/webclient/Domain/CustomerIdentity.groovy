package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator

class CustomerIdentity {
    String identityUID;
    String identityUsername;
    String customerName;

    @JsonCreator
    public CustomerIdentity(){

    }
}