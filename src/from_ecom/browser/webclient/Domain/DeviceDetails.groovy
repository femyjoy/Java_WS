package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator


/**
 * Created with IntelliJ IDEA.
 * User: ee
 * Date: 12/08/13
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
class DeviceDetails {
    String id;
    String channelPermission;
    Prices prices

    @JsonCreator
    DeviceDetails() {
    }
}
