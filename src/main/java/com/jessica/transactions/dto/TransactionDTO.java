package com.jessica.transactions.dto;

import java.time.LocalDate;

public class TransactionDTO {

    private String id;
    private String merchant;
    private Double amount;
    private LocalDate date;

    public TransactionDTO(){}

    public TransactionDTO(String id, String merchant, Double amount, LocalDate date){
        this.id = id;
        this.merchant = merchant;
        this.amount = amount;
        this.date = date;

    }

    public String getId(){return id;}
    public String getMerchant(){return merchant;}
    public Double getAmount(){return amount; }
    public LocalDate getDate(){return date; }
}
