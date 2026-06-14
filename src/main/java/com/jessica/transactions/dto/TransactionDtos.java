package com.jessica.transactions.dto;

import com.jessica.transactions.model.Category;
import com.jessica.transactions.model.TransactionSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class TransactionDtos {

    private TransactionDtos() {}

    // ── Request DTOs ─────────────────────────────────────────────────────────

    @Value
    @Builder
    public static class CreateTransactionRequest {
        @NotBlank String customerId;
        @NotBlank String merchant;
        @NotNull @Positive BigDecimal amount;
        @NotNull TransactionSource source;
        @NotNull LocalDateTime date;
        String currency;
        String externalId;
    }

    // ── Response DTOs ────────────────────────────────────────────────────────

    @Value
    @Builder
    public static class TransactionResponse {
        String id;
        String customerId;
        String merchant;
        BigDecimal amount;
        String currency;
        Category category;
        TransactionSource source;
        LocalDateTime date;
        LocalDateTime createdAt;
    }

    @Value
    @Builder
    public static class PagedTransactionResponse {
        List<TransactionResponse> transactions;
        int page;
        int size;
        long totalElements;
        int totalPages;
    }

    @Value
    @Builder
    public static class CategorySummaryResponse {
        String customerId;
        Map<String, BigDecimal> totalsByCategory;
        BigDecimal grandTotal;
        LocalDateTime from;
        LocalDateTime to;
    }

    @Value
    @Builder
    public static class AggregationResultResponse {
        String customerId;
        int transactionsIngested;
        int duplicatesSkipped;
        String source;
    }

    // ── Error DTO ────────────────────────────────────────────────────────────

    @Value
    @Builder
    public static class ErrorResponse {
        int status;
        String error;
        String message;
        LocalDateTime timestamp;
    }
}
