package com.jessica.transactions.mock;

public class BankTransaction {
    public String transactionId;
    public String vendor;
    public Double value;
    public String transactionDate;

    public BankTransaction(String transactionId, String vendor, Double value, String transactionDate) {
        this.transactionId = transactionId;
        this.vendor = vendor;
        this.value = value;
        this.transactionDate = transactionDate;
    }

}
