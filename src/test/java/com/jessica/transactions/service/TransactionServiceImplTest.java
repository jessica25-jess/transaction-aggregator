package com.jessica.transactions.service;

import com.jessica.transactions.dto.TransactionDtos.*;
import com.jessica.transactions.exception.ResourceNotFoundException;
import com.jessica.transactions.model.Category;
import com.jessica.transactions.model.Transaction;
import com.jessica.transactions.model.TransactionSource;
import com.jessica.transactions.repository.TransactionRepository;
import com.jessica.transactions.repository.TransactionRepository.CategoryTotal;
import com.jessica.transactions.service.impl.TransactionServiceImpl;
import com.jessica.transactions.util.TransactionCategorizer;
import com.jessica.transactions.util.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionServiceImpl")
class TransactionServiceImplTest {

    @Mock TransactionRepository repository;
    @Mock TransactionCategorizer categorizer;
    @Mock TransactionMapper mapper;

    @InjectMocks
    TransactionServiceImpl service;

    private CreateTransactionRequest validRequest;
    private Transaction savedTransaction;
    private TransactionResponse transactionResponse;

    @BeforeEach
    void setUp() {
        validRequest = CreateTransactionRequest.builder()
                .customerId("cust-1")
                .merchant("Checkers Hyper")
                .amount(new BigDecimal("500.00"))
                .source(TransactionSource.BANK_ACCOUNT)
                .date(LocalDateTime.now())
                .build();

        savedTransaction = Transaction.builder()
                .id("tx-1")
                .customerId("cust-1")
                .merchant("Checkers Hyper")
                .amount(new BigDecimal("500.00"))
                .category(Category.GROCERIES)
                .source(TransactionSource.BANK_ACCOUNT)
                .date(LocalDateTime.now())
                .build();

        transactionResponse = TransactionResponse.builder()
                .id("tx-1")
                .customerId("cust-1")
                .merchant("Checkers Hyper")
                .amount(new BigDecimal("500.00"))
                .category(Category.GROCERIES)
                .build();
    }

    @Test
    @DisplayName("createTransaction persists and returns response")
    void createTransaction_success() {
        when(mapper.toEntity(validRequest)).thenReturn(savedTransaction);
        when(categorizer.categorize("Checkers Hyper")).thenReturn(Category.GROCERIES);
        when(repository.save(any())).thenReturn(savedTransaction);
        when(mapper.toResponse(savedTransaction)).thenReturn(transactionResponse);

        TransactionResponse result = service.createTransaction(validRequest);

        assertThat(result.getId()).isEqualTo("tx-1");
        verify(repository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("createTransaction skips duplicate externalId")
    void createTransaction_duplicateExternalId_skipsAndReturnsExisting() {
        CreateTransactionRequest requestWithExternalId = CreateTransactionRequest.builder()
                .customerId("cust-1")
                .merchant("Netflix")
                .amount(new BigDecimal("199.00"))
                .source(TransactionSource.EXTERNAL_API)
                .date(LocalDateTime.now())
                .externalId("STITCH-003-cust-1")
                .build();

        when(repository.existsByExternalId("STITCH-003-cust-1")).thenReturn(true);
        when(repository.findByExternalId("STITCH-003-cust-1"))
                .thenReturn(Optional.of(savedTransaction));
        when(mapper.toResponse(savedTransaction)).thenReturn(transactionResponse);

        service.createTransaction(requestWithExternalId);

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("getCategorySummary throws ResourceNotFoundException when no transactions exist")
    void getCategorySummary_noTransactions_throwsNotFound() {
        when(repository.sumAmountByCategory("cust-unknown")).thenReturn(List.of());

        assertThatThrownBy(() -> service.getCategorySummary("cust-unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("cust-unknown");
    }

    @Test
    @DisplayName("getCategorySummary returns correct totals and grand total")
    void getCategorySummary_returnsSummary() {
        CategoryTotal groceries = mockCategoryTotal(Category.GROCERIES, new BigDecimal("1000.00"));
        CategoryTotal transport = mockCategoryTotal(Category.TRANSPORT, new BigDecimal("300.00"));

        when(repository.sumAmountByCategory("cust-1")).thenReturn(List.of(groceries, transport));

        CategorySummaryResponse result = service.getCategorySummary("cust-1");

        assertThat(result.getGrandTotal()).isEqualByComparingTo(new BigDecimal("1300.00"));
        assertThat(result.getTotalsByCategory()).containsKeys("GROCERIES", "TRANSPORT");
    }

    @Test
    @DisplayName("getCategorySummaryByDateRange throws when from is after to")
    void getCategorySummaryByDateRange_invalidRange_throwsIllegalArgument() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.minusDays(1);

        assertThatThrownBy(() -> service.getCategorySummaryByDateRange("cust-1", from, to))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("from");
    }

    @Test
    @DisplayName("getTransactions returns paged response")
    void getTransactions_returnsPaged() {
        var page = new PageImpl<>(List.of(savedTransaction), PageRequest.of(0, 20), 1);
        when(repository.findByCustomerId("cust-1", PageRequest.of(0, 20))).thenReturn(page);
        when(mapper.toResponse(savedTransaction)).thenReturn(transactionResponse);

        PagedTransactionResponse result = service.getTransactions("cust-1", PageRequest.of(0, 20));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTransactions()).hasSize(1);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private CategoryTotal mockCategoryTotal(Category category, BigDecimal total) {
        return new CategoryTotal() {
            public Category getCategory() { return category; }
            public BigDecimal getTotal() { return total; }
        };
    }
}
