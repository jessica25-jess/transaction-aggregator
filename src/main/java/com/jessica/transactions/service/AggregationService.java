package com.jessica.transactions.service;

import com.jessica.transactions.dto.TransactionDtos.AggregationResultResponse;

public interface AggregationService {

    /**
     * Pulls transactions from all registered external data sources for the given customer,
     * categorises them, and persists any that have not been seen before.
     *
     * @param customerId the customer to aggregate for
     * @return a summary of how many transactions were ingested and how many were skipped as duplicates
     */
    AggregationResultResponse aggregateForCustomer(String customerId);
}
