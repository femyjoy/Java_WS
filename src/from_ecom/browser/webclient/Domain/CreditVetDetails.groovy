package from_ecom.browser.webclient.Domain

import java.text.DateFormat
import java.text.SimpleDateFormat

public class CreditVetDetails {
    String creditVetNumber
    String creditScore
    String lastCreditCheckDate
    String referralReason
    Integer creditClassId
    TransactCreditCheckDetails transactCreditCheckDetails
    DepositDetails depositDetails

    CreditVetDetails(String creditVetNumber, String creditScore, Date lastCreditCheckDate, String referralReason, Integer creditClassId, TransactCreditCheckDetails transactCreditCheckDetails, DepositDetails depositDetails) {
        this.creditVetNumber = creditVetNumber
        this.creditScore = creditScore
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+01:00");
        this.lastCreditCheckDate = df.format(lastCreditCheckDate)
        this.referralReason = referralReason
        this.creditClassId = creditClassId
        this.transactCreditCheckDetails = transactCreditCheckDetails
        this.depositDetails = depositDetails
    }
}
