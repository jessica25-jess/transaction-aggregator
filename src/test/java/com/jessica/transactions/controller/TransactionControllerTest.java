package com.jessica.transactions.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jessica.transactions.dto.TransactionDtos.*;
import com.jessica.transactions.exception.GlobalExceptionHandler;
import com.jessica.transactions.exception.ResourceNotFoundException;
import com.jessica.transactions.model.Category;
import com.jessica.transactions.model.TransactionSource;
import com.jessica.transactions.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("TransactionController")
class TransactionControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean TransactionService transactionService;

    @Test
    @DisplayName("POST /transactions returns 201 with created transaction")
    void createTransaction_returns201() throws Exception {
        CreateTransactionRequest request = CreateTransactionRequest.builder()
                .customerId("cust-1")
                .merchant("Checkers")
                .amount(new BigDecimal("500.00"))
                .source(TransactionSource.BANK_ACCOUNT)
                .date(LocalDateTime.now())
                .build();

        TransactionResponse response = TransactionResponse.builder()
                .id("tx-1")
                .customerId("cust-1")
                .merchant("Checkers")
                .amount(new BigDecimal("500.00"))
                .category(Category.GROCERIES)
                .source(TransactionSource.BANK_ACCOUNT)
                .build();

        when(transactionService.createTransaction(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("tx-1"))
                .andExpect(jsonPath("$.category").value("GROCERIES"));
    }

    @Test
    @DisplayName("POST /transactions returns 400 when body is invalid")
    void createTransaction_missingFields_returns400() throws Exception {
        String invalidBody = "{\"merchant\": \"Checkers\"}"; // missing required fields

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /transactions returns paged list")
    void getTransactions_returnsPaged() throws Exception {
        PagedTransactionResponse paged = PagedTransactionResponse.builder()
                .transactions(List.of())
                .page(0).size(20).totalElements(0).totalPages(0)
                .build();

        when(transactionService.getTransactions(eq("cust-1"), any(Pageable.class)))
                .thenReturn(paged);

        mockMvc.perform(get("/api/v1/transactions").param("customerId", "cust-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    @DisplayName("GET /transactions returns 400 when customerId is blank")
    void getTransactions_blankCustomerId_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/transactions").param("customerId", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /transactions/summary returns 404 when customer has no transactions")
    void getCategorySummary_noTransactions_returns404() throws Exception {
        when(transactionService.getCategorySummary("cust-unknown"))
                .thenThrow(new ResourceNotFoundException("No transactions found for customerId: cust-unknown"));

        mockMvc.perform(get("/api/v1/transactions/summary").param("customerId", "cust-unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("GET /transactions/summary returns category totals")
    void getCategorySummary_returnsSummary() throws Exception {
        CategorySummaryResponse summary = CategorySummaryResponse.builder()
                .customerId("cust-1")
                .totalsByCategory(Map.of("GROCERIES", new BigDecimal("1000.00")))
                .grandTotal(new BigDecimal("1000.00"))
                .build();

        when(transactionService.getCategorySummary("cust-1")).thenReturn(summary);

        mockMvc.perform(get("/api/v1/transactions/summary").param("customerId", "cust-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grandTotal").value(1000.00))
                .andExpect(jsonPath("$.totalsByCategory.GROCERIES").value(1000.00));
    }
}
