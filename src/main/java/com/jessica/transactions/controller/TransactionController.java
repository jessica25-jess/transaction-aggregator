package com.jessica.transactions.controller;

import com.jessica.transactions.dto.TransactionDtos.*;
import com.jessica.transactions.model.Category;
import com.jessica.transactions.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * POST /api/v1/transactions
     * Manually create a single transaction.
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/transactions?customerId=&page=&size=&sort=
     * Retrieve all transactions for a customer, paginated.
     */
    @GetMapping
    public ResponseEntity<PagedTransactionResponse> getTransactions(
            @RequestParam @NotBlank String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return ResponseEntity.ok(transactionService.getTransactions(customerId, pageable));
    }

    /**
     * GET /api/v1/transactions/category?customerId=&category=&page=&size=
     * Retrieve transactions filtered by category.
     */
    @GetMapping("/category")
    public ResponseEntity<PagedTransactionResponse> getTransactionsByCategory(
            @RequestParam @NotBlank String customerId,
            @RequestParam Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return ResponseEntity.ok(
                transactionService.getTransactionsByCategory(customerId, category, pageable));
    }

    /**
     * GET /api/v1/transactions/range?customerId=&from=&to=&page=&size=
     * Retrieve transactions within a date range.
     */
    @GetMapping("/range")
    public ResponseEntity<PagedTransactionResponse> getTransactionsByDateRange(
            @RequestParam @NotBlank String customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return ResponseEntity.ok(
                transactionService.getTransactionsByDateRange(customerId, from, to, pageable));
    }

    /**
     * GET /api/v1/transactions/summary?customerId=
     * Get total spend grouped by category (all time).
     */
    @GetMapping("/summary")
    public ResponseEntity<CategorySummaryResponse> getCategorySummary(
            @RequestParam @NotBlank String customerId) {
        return ResponseEntity.ok(transactionService.getCategorySummary(customerId));
    }

    /**
     * GET /api/v1/transactions/summary/range?customerId=&from=&to=
     * Get total spend grouped by category within a date range.
     */
    @GetMapping("/summary/range")
    public ResponseEntity<CategorySummaryResponse> getCategorySummaryByDateRange(
            @RequestParam @NotBlank String customerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(
                transactionService.getCategorySummaryByDateRange(customerId, from, to));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        int safeSize = Math.min(size, 100); // cap page size to prevent abuse
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, safeSize, sort);
    }
}
