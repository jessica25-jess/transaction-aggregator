package com.jessica.transactions.service.impl;

import com.jessica.transactions.dto.TransactionDtos.*;
import com.jessica.transactions.exception.ResourceNotFoundException;
import com.jessica.transactions.model.Category;
import com.jessica.transactions.model.Transaction;
import com.jessica.transactions.repository.TransactionRepository;
import com.jessica.transactions.repository.TransactionRepository.CategoryTotal;
import com.jessica.transactions.service.TransactionService;
import com.jessica.transactions.util.TransactionCategorizer;
import com.jessica.transactions.util.TransactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final TransactionCategorizer categorizer;
    private final TransactionMapper mapper;

    @Override
    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        log.debug("Creating transaction for customerId={}, merchant={}",
                request.getCustomerId(), request.getMerchant());

        // Idempotency: skip if this external ID has already been ingested
        if (request.getExternalId() != null
                && repository.existsByExternalId(request.getExternalId())) {
            log.info("Duplicate transaction skipped: externalId={}", request.getExternalId());
            return mapper.toResponse(
                    repository.findByExternalId(request.getExternalId()).orElseThrow());
        }

        Transaction transaction = mapper.toEntity(request);
        transaction.setCategory(categorizer.categorize(request.getMerchant()));

        Transaction saved = repository.save(transaction);
        log.info("Transaction created: id={}, customerId={}", saved.getId(), saved.getCustomerId());
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedTransactionResponse getTransactions(String customerId, Pageable pageable) {
        Page<Transaction> page = repository.findByCustomerId(customerId, pageable);
        return toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedTransactionResponse getTransactionsByCategory(
            String customerId, Category category, Pageable pageable) {
        Page<Transaction> page = repository.findByCustomerIdAndCategory(
                customerId, category, pageable);
        return toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedTransactionResponse getTransactionsByDateRange(
            String customerId, LocalDateTime from, LocalDateTime to, Pageable pageable) {
        validateDateRange(from, to);
        Page<Transaction> page = repository.findByCustomerIdAndDateBetween(
                customerId, from, to, pageable);
        return toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public CategorySummaryResponse getCategorySummary(String customerId) {
        List<CategoryTotal> totals = repository.sumAmountByCategory(customerId);

        if (totals.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No transactions found for customerId: " + customerId);
        }

        Map<String, BigDecimal> byCategory = totals.stream()
                .collect(Collectors.toMap(
                        ct -> ct.getCategory().name(),
                        CategoryTotal::getTotal
                ));

        BigDecimal grandTotal = byCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CategorySummaryResponse.builder()
                .customerId(customerId)
                .totalsByCategory(byCategory)
                .grandTotal(grandTotal)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CategorySummaryResponse getCategorySummaryByDateRange(
            String customerId, LocalDateTime from, LocalDateTime to) {
        validateDateRange(from, to);

        List<CategoryTotal> totals = repository.sumAmountByCategoryAndDateRange(
                customerId, from, to);

        if (totals.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No transactions found for customerId: " + customerId
                            + " in the specified date range");
        }

        Map<String, BigDecimal> byCategory = totals.stream()
                .collect(Collectors.toMap(
                        ct -> ct.getCategory().name(),
                        CategoryTotal::getTotal
                ));

        BigDecimal grandTotal = byCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CategorySummaryResponse.builder()
                .customerId(customerId)
                .totalsByCategory(byCategory)
                .grandTotal(grandTotal)
                .from(from)
                .to(to)
                .build();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private PagedTransactionResponse toPagedResponse(Page<Transaction> page) {
        return PagedTransactionResponse.builder()
                .transactions(page.getContent().stream()
                        .map(mapper::toResponse)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private void validateDateRange(LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("'from' date must not be after 'to' date");
        }
    }
}
