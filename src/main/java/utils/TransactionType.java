package utils;

import java.util.stream.Stream;

public enum TransactionType {


    CASH_DEPOSIT("CASH_DEPOSIT","DC_PIN_CD",2), CASH_WITHDRAWAL("CASH_WITHDRAWAL","DC_PIN_CW",2), BALANCE_ENQUIRY("BALANCE_ENQUIRY","DC_PIN_BE",1), MINI_STATEMENT("MINI_STATEMENT","DC_PIN_MS",1),FUND_TRANSFER("FUND_TRANSFER","DC_PIN_FT",2), CHANGE_PIN("CHANGE_PIN","DC_PIN_CPIN",1), POS_INITIAL_SETUP("POS_INITIAL_SETUP","DC_PIN_POS",1);
    private String dcTxnType;
    private String dcTxnCategory;
    private int dcTxnTypeVal;
    TransactionType(String dcTxnCategory,String dcPinCd, int dcTxnTypeVal) {
        this.dcTxnType = dcPinCd;
        this.dcTxnCategory = dcTxnCategory;
        this.dcTxnTypeVal = dcTxnTypeVal;
    }

    public String getDCTransactionType() {
        return this.dcTxnType;
    }

    public String getDCTransactionCategory() {
        return this.dcTxnCategory;
    }

    public int getDcTxnTypeVal() { return this.dcTxnTypeVal; }

    public static Stream<TransactionType> transactionTypeStream() {
        return Stream.of(TransactionType.values());
    }
}
