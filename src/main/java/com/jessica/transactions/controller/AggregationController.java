package com.jessica.transactions.controller;

import com.jessica.transactions.dto.TransactionDtos.AggregationResultResponse;
import com.jessica.transactions.service.AggregationService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/aggregation")
@RequiredArgsConstructor
@Validated
public class AggregationController {

    private final AggregationService aggregationService;

    /**
     * POST /api/v1/aggregation/{customerId}
     * Triggers ingestion of transactions from all registered external data sources
     * for the given customer. Idempotent — duplicate transactions are skipped.
     */
    @PostMapping("/{customerId}")
    public ResponseEntity<AggregationResultResponse> aggregate(
            @PathVariable @NotBlank String customerId) {
        AggregationResultResponse result = aggregationService.aggregateForCustomer(customerId);
        return ResponseEntity.ok(result);
    }
}
