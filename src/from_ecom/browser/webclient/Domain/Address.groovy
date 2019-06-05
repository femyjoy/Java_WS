package from_ecom.browser.webclient.Domain

import org.codehaus.jackson.annotate.JsonCreator

class Address {
    String organisation
    String subBuildingName
    String buildingName
    String buildingNumber
    String dependentStreet
    String street
    String dependentLocality
    String locality
    String postTown
    String county
    String postcode
    String country

    @JsonCreator
    Address(){

    }
}
