package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator

class CustomerLookupMsisdnDetails {
    String msisdn;

    @JsonCreator
    CustomerLookupMsisdnDetails() {
    }

    public List<Link> links;
}
