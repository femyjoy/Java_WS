package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator

class CustomerLookup {

    @JsonCreator
    CustomerLookup() {
    }

    public List<CustomerLookupMsisdnDetails> msisdns;

    public List<CustomerLookupPortal> portals;
}
