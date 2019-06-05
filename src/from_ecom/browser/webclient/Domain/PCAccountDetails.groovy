package from_ecom.browser.webclient.Domain

class PCAccountDetails {
    String billingAccountName
    String userAccountNumber
    String bankSortCode
    String bankAccountName

    PCAccountDetails(String billingAccountName, String userAccountNumber, String bankSortCode, String bankAccountName){
        this.billingAccountName = billingAccountName;
        this.userAccountNumber = userAccountNumber;
        this.bankAccountName = bankAccountName;
        this.bankSortCode = bankSortCode;
    }
}
