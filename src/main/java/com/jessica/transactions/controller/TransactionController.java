package com.jessica.transactions.controller;

import com.jessica.transactions.model.Transaction;
import com.jessica.transactions.service.TransactionService;
import com.jessica.transactions.service.AggregationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService service;
    private final AggregationService aggregationService;

    public TransactionController(TransactionService service,
                                 AggregationService aggregationService){
        this.service = service;
        this.aggregationService = aggregationService;
    }
// Get stored transactions
    @GetMapping
    public List<Transaction>getTransactions(@RequestParam String customerId){
        return service.getTransactions(customerId);
    }
    // Trigger aggregation from all sources
    @PostMapping("/aggregate")
    public String aggregate(@RequestParam String customerId){
        aggregationService.aggregate(customerId);
        return "Aggregation completed successfully";
    }
}

