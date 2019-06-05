package from_ecom.browser.webclient.Domain

class ConditionalBars {
    Boolean internationalBar
    Boolean premiumBar
    Boolean  roamingBar

    ConditionalBars(Boolean internationalBar, Boolean premiumBar, Boolean roamingBar) {
        this.internationalBar = internationalBar
        this.premiumBar = premiumBar
        this.roamingBar = roamingBar
    }
}