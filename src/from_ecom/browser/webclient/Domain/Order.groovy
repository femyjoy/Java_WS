package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator

class Order {
    String id;
    Total total;
    Requirements requirements;

    @JsonCreator
    public Order(){

    }

    public static class Total {
        Integer oneOff;
        Integer monthly;
    }
}