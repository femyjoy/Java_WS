package from_ecom.browser.webclient.Domain

import org.joda.time.DateTime

class CreditCheckData {
    String creditCheckStatus
    String contactNumber
    CreditVetDetails creditVetDetails

    private CreditCheckData(String creditCheckStatus, String contactNumber, CreditVetDetails creditVetDetails) {
        this.creditCheckStatus = creditCheckStatus
        this.contactNumber = contactNumber
        this.creditVetDetails = creditVetDetails
    }

    public static enum Factory {
        Accept("A", "1", null),
        Refer("R", "5", null),
        ReferWithFiftyDeposit("R", "5", new DepositDetails("SERVICE", 5000, 3, null)),
        DepositFiftyPounds("A", "14", new DepositDetails("SERVICE", 5000, 3, null));

        private CreditCheckData creditCheckData;

        private Factory(String status, String creditCheckStatusCode, DepositDetails depositDetails) {

            TransactCreditCheckDetails transactCreditCheckDetails = new TransactCreditCheckDetails(status, "0", "1000", null);
            CreditVetDetails vetDetails = new CreditVetDetails(
                    "952522832",
                    "747",
                    DateTime.now().toDate(),
                    null,
                    5,
                    transactCreditCheckDetails,
                    depositDetails);
            this.creditCheckData = new CreditCheckData(creditCheckStatusCode, "447917263505", vetDetails);
        }

        public CreditCheckData getData(){
            return this.creditCheckData;
        }
    }
}
