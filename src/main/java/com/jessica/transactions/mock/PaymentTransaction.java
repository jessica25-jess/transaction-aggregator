package com.jessica.transactions.mock;

public class PaymentTransaction {
    public String paymentId;
    public String store;
    public Double cost;
    public String time;

    public PaymentTransaction(String paymentId, String store, Double cost, String time){
        this.paymentId = paymentId;
        this.store = store;
        this.cost = cost;
        this.time = time;
    }
}
