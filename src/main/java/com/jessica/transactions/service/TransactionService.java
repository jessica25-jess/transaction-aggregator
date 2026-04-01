package com.jessica.transactions.service;

import com.jessica.transactions.model.Transaction;
import com.jessica.transactions.repository.TransactionRepository;
import com.jessica.transactions.util.TransactionCategorizer;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final TransactionCategorizer categorizer;

    public TransactionService(TransactionRepository repository,
                              TransactionCategorizer categorizer){
        this.repository = repository;
        this.categorizer = categorizer;
    }

    public Transaction createTransaction(Transaction transaction){

        transaction.setId(UUID.randomUUID().toString());
        transaction.setDate(LocalDate.now());

        transaction.setCategory(
                categorizer.categorize(transaction.getMerchant())
        );

        return repository.save(transaction);
    }

    public List<Transaction> getTransactions(String customerId){
        return repository.findByCustomerId(customerId);
    }
}
