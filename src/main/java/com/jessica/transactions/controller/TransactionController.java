package com.jessica.transactions.controller;

import com.jessica.transactions.model.Transaction;
import com.jessica.transactions.service.TransactionService;
import com.jessica.transactions.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;
    private final AnalyticsService analyticsService;

    public TransactionController(TransactionService service,
                                 AnalyticsService analyticsService){
        this.service = service;
        this.analyticsService = analyticsService;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction){
        return service.createTransaction(transaction);
    }

    @GetMapping
    public List<Transaction> getTransactions(
            @RequestParam String customerId){
        return service.getTransactions(customerId);
    }

    @GetMapping("/summary")
    public Map<String, Double> summary(
            @RequestParam String customerId){
        return analyticsService.totalByCategory(customerId);
    }
}

