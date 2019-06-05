package from_ecom.browser.webclient.Domain

class TransactCreditCheckDetails {
    String creditCheckResultCode
    String totalMonthlyCharge
    String creditLimit
    ConditionalBars conditionalBars;

    TransactCreditCheckDetails(String creditCheckResultCode, String totalMonthlyCharge, String creditLimit, ConditionalBars conditionalBars) {
        this.creditCheckResultCode = creditCheckResultCode
        this.totalMonthlyCharge = totalMonthlyCharge
        this.creditLimit = creditLimit
        this.conditionalBars = conditionalBars
    }
}
