package from_ecom.browser.webclient.Domain

import com.fasterxml.jackson.annotation.JsonCreator

public class CustomerLookupPortal {
    @JsonCreator
    CustomerLookupPortal() {
    }

    public List<Link> links;
}
