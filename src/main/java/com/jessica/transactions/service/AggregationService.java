package com.jessica.transactions.service;

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

    public AggregationService(TransactionRepository repository,
                              TransactionCategorizer categorizer) {
        this.repository = repository;
        this.categorizer = categorizer;
    }

    public void aggregate(String customerId) {

        save(customerId, "Uber", 120.0, "BANK");
        save(customerId, "Checkers", 450.0, "BANK");
        save(customerId, "Netflix", 99.0, "CREDIT_CARD");
    }

    public void save(String customerId, String merchant, Double amount, String source) {

        Transaction t = new Transaction();

        t.setId(UUID.randomUUID().toString());
        t.setCustomerId(customerId);
        t.setMerchant(merchant);
        t.setAmount(amount);
        t.setSource(source);
        t.setDate(LocalDate.now());
        t.setCategory(categorizer.categorize(merchant));

        repository.save(t);
    }
}
