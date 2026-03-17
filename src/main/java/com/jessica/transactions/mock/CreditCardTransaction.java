package com.jessica.transactions.mock;

public class CreditCardTransaction {
    public String id;
    public String merchantName;
    public Double amount;
    public String date;

    public CreditCardTransaction(String id, String merchantName, Double amount, String date) {
        this.id = id;
        this.merchantName = merchantName;
        this.amount = amount;
        this.date = date;
    }
    }

