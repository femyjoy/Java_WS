package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator


/**
 * Created with IntelliJ IDEA.
 * User: ee
 * Date: 13/08/13
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
class ProductRelationship {
    String id;
    String type;
    List<String> categories;
    Price price;
    List<Price> prices;

    @JsonCreator
    ProductRelationship() {
    }
}
