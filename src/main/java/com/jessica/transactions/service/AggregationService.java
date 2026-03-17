package com.jessica.transactions.service;

import com.jessica.transactions.client.*;
import com.jessica.transactions.mock.*;
import com.jessica.transactions.model.Transaction;
import com.jessica.transactions.repository.TransactionRepository;
import com.jessica.transactions.util.TransactionCategorizer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class AggregationService {

    private final TransactionRepository repository;
    private final TransactionCategorizer categorizer;

    private final BankApiClient bankClient;
    private final CreditCardApiClient creditCardClient;
    private final PaymentApiClient paymentClient;

    public AggregationService(TransactionRepository repository,
                              TransactionCategorizer categorizer,
                              BankApiClient bankClient,
                              CreditCardApiClient creditCardClient,
                              PaymentApiClient paymentClient) {
        this.repository = repository;
        this.categorizer = categorizer;
        this.bankClient = bankClient;
        this.creditCardClient = creditCardClient;
        this.paymentClient = paymentClient;
    }


    public void aggregate(String customerId) {
        //BANK
        for (BankTransaction bt : bankClient.getTransactions(customerId)) {
            save(bt.vendor, bt.value, "BANK");
        }
        //CREDIT CARD
        for (CreditCardTransaction ct : creditCardClient.getTransactions(customerId)) {
            save(ct.merchantName, ct.amount, "CREDIT_CARD");
        }
        //PAYMENT APP
        for (PaymentTransaction pt : paymentClient.getTransactions(customerId)) {
            save(pt.store, pt.cost, "PAYMENT_APP");
        }
    }

    public void save(String merchant, Double amount, String source) {

        Transaction t = new Transaction();

        t.setId(UUID.randomUUID().toString());
        t.setCustomerId("123");
        t.setMerchant(merchant);
        t.setAmount(amount);
        t.setSource(source);
        t.setDate(LocalDate.now());
        t.setCategory(categorizer.categorize(merchant));

        repository.save(t);
    }
}


