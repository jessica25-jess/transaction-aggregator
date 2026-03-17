package com.jessica.transactions.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Transaction{

    @Id
    private String id;

    private String customerId;
    private String merchant;
    private Double amount;
    private String category;
    private String source;
    private LocalDate date;

    public Transaction(){}

    public String getId(){
        return id;}
    public void setId(String id) {
        this.id = id; }

    public String getCustomerId() {
        return customerId; }
    public void setCustomerId(String customerId) {
        this.customerId = customerId; }

    public String getMerchant() {
        return merchant;}
    public void setMerchant(String merchant) {
        this.merchant = merchant;}

    public Double getAmount() {
        return amount;}
    public void setAmount(Double amount) {
        this.amount = amount;}

    public String getCategory() {
        return category;}
    public void setCategory(String category) {
        this.category = category;}

    public String getSource() {
        return source;}
    public void setSource(String source) {
        this.source = source;}

    public LocalDate getDate(){
        return date;
    }
    public void setDate(LocalDate date){
        this.date = date; }
    }

