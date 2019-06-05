package from_ecom.browser.webclient.Domain

class DepositDetails {
    String depositKind
    Integer depositAmount
    Integer depositDuration
    String depositReference

    DepositDetails(String depositKind, Integer depositAmount, Integer depositDuration, String depositReference) {
        this.depositKind = depositKind
        this.depositAmount = depositAmount
        this.depositDuration = depositDuration
        this.depositReference = depositReference
    }
}
