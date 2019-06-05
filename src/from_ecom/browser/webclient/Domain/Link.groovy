package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator

class Link {
    String rel;
    String href;

    @JsonCreator
    Link() {
    }
}
