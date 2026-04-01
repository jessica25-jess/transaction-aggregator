package com.jessica.transactions.service;

import com.jessica.transactions.model.Transaction;
import com.jessica.transactions.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final TransactionRepository repository;

    public AnalyticsService(TransactionRepository repository){
        this.repository = repository;
    }

    public Map<String, Double> totalByCategory(String customerId){

        List<Transaction> transactions =
                repository.findByCustomerId(customerId);

        return transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(Transaction::getAmount)
                ));
    }
}