package com.jessica.transactions.service.datasource;

import com.jessica.transactions.dto.TransactionDtos.CreateTransactionRequest;
import com.jessica.transactions.model.TransactionSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Stub implementation of an Open Banking data source (e.g. Stitch Money API).
 *
 * In production, replace this with a real WebClient-based implementation that:
 *   1. Authenticates with the provider (OAuth2 / API key)
 *   2. Calls the provider's transaction endpoint
 *   3. Maps the provider's schema to CreateTransactionRequest
 *
 * The interface contract (ExternalDataSource) remains unchanged regardless of
 * which provider is used, making the swap a drop-in replacement.
 */
@Slf4j
@Component
public class StitchDataSource implements ExternalDataSource {

    @Override
    public String sourceName() {
        return "STITCH";
    }

    @Override
    public List<CreateTransactionRequest> fetchTransactions(String customerId) {
        log.info("Fetching transactions from Stitch for customerId={}", customerId);

        // Simulates a realistic set of transactions a bank API would return
        return List.of(
                buildRequest(customerId, "Checkers Hyper Pretoria",        new BigDecimal("1250.00"), "STITCH-001"),
                buildRequest(customerId, "Uber* Trip",                     new BigDecimal("89.50"),   "STITCH-002"),
                buildRequest(customerId, "Netflix.com",                    new BigDecimal("199.00"),  "STITCH-003"),
                buildRequest(customerId, "Salary Payment - Employer Co",   new BigDecimal("35000.00"),"STITCH-004"),
                buildRequest(customerId, "Dischem Pharmacy",               new BigDecimal("342.75"),  "STITCH-005"),
                buildRequest(customerId, "Woolworths Food",                new BigDecimal("678.30"),  "STITCH-006"),
                buildRequest(customerId, "Nando's Menlyn",                 new BigDecimal("220.00"),  "STITCH-007"),
                buildRequest(customerId, "Takealot Order",                 new BigDecimal("1100.00"), "STITCH-008"),
                buildRequest(customerId, "Vodacom Airtime",                new BigDecimal("100.00"),  "STITCH-009"),
                buildRequest(customerId, "Bolt Ride",                      new BigDecimal("65.00"),   "STITCH-010")
        );
    }

    private CreateTransactionRequest buildRequest(
            String customerId, String merchant, BigDecimal amount, String externalIdSuffix) {
        return CreateTransactionRequest.builder()
                .customerId(customerId)
                .merchant(merchant)
                .amount(amount)
                .source(TransactionSource.EXTERNAL_API)
                .date(LocalDateTime.now().minusDays((long) (Math.random() * 30)))
                .currency("ZAR")
                .externalId(externalIdSuffix + "-" + customerId)
                .build();
    }
}
