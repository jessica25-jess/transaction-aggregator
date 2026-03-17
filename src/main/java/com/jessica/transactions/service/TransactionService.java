package com.jessica.transactions.service;

import com.jessica.transactions.model.Transaction;
import com.jessica.transactions.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository){
        this.repository = repository;
    }

    public List<Transaction> getTransactions(String customerId){
        return repository.findByCustomerId(customerId);
    }
}
