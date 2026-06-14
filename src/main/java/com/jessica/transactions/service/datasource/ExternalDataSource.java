package com.jessica.transactions.service.datasource;

import com.jessica.transactions.dto.TransactionDtos.CreateTransactionRequest;

import java.util.List;

/**
 * Contract for any external financial data source.
 * Implement this interface to plug in a new provider (e.g. Stitch, Plaid, Mono).
 */
public interface ExternalDataSource {

    /**
     * Human-readable name for this data source (used in logs and response metadata).
     */
    String sourceName();

    /**
     * Fetches raw transactions for a given customer from the external provider.
     *
     * @param customerId the internal customer identifier
     * @return list of normalised transaction requests ready for ingestion
     */
    List<CreateTransactionRequest> fetchTransactions(String customerId);
}
