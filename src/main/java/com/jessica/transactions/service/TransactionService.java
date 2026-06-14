package com.jessica.transactions.service;

import com.jessica.transactions.dto.TransactionDtos.*;
import com.jessica.transactions.model.Category;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TransactionService {

    TransactionResponse createTransaction(CreateTransactionRequest request);

    PagedTransactionResponse getTransactions(String customerId, Pageable pageable);

    PagedTransactionResponse getTransactionsByCategory(
            String customerId, Category category, Pageable pageable);

    PagedTransactionResponse getTransactionsByDateRange(
            String customerId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    CategorySummaryResponse getCategorySummary(String customerId);

    CategorySummaryResponse getCategorySummaryByDateRange(
            String customerId, LocalDateTime from, LocalDateTime to);
}
