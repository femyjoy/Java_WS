package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator

/**
 * Created with IntelliJ IDEA.
 * User: ee
 * Date: 13/08/13
 * Time: 09:50
 * To change this template use File | Settings | File Templates.
 */
class Prices {
    Price price;
    List<ProductRelationship> withRelated

    @JsonCreator
    Prices() {
    }
}
