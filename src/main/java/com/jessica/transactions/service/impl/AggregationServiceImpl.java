package com.jessica.transactions.service.impl;

import com.jessica.transactions.dto.TransactionDtos.AggregationResultResponse;
import com.jessica.transactions.dto.TransactionDtos.CreateTransactionRequest;
import com.jessica.transactions.repository.TransactionRepository;
import com.jessica.transactions.service.AggregationService;
import com.jessica.transactions.service.TransactionService;
import com.jessica.transactions.service.datasource.ExternalDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates transaction ingestion from all registered {@link ExternalDataSource} implementations.
 * Each data source is auto-discovered via Spring's dependency injection — adding a new provider
 * only requires implementing ExternalDataSource and annotating it with @Component.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationServiceImpl implements AggregationService {

    private final List<ExternalDataSource> dataSources;
    private final TransactionService transactionService;
    private final TransactionRepository repository;

    @Override
    public AggregationResultResponse aggregateForCustomer(String customerId) {
        log.info("Starting aggregation for customerId={} from {} source(s)",
                customerId, dataSources.size());

        int totalIngested = 0;
        int totalSkipped = 0;
        String sourceNames = dataSources.stream()
                .map(ExternalDataSource::sourceName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("none");

        for (ExternalDataSource dataSource : dataSources) {
            log.info("Fetching from source={} for customerId={}", dataSource.sourceName(), customerId);

            List<CreateTransactionRequest> incoming;
            try {
                incoming = dataSource.fetchTransactions(customerId);
            } catch (Exception e) {
                log.error("Failed to fetch from source={}: {}", dataSource.sourceName(), e.getMessage());
                continue;
            }

            for (CreateTransactionRequest request : incoming) {
                if (request.getExternalId() != null
                        && repository.existsByExternalId(request.getExternalId())) {
                    log.debug("Skipping duplicate externalId={}", request.getExternalId());
                    totalSkipped++;
                } else {
                    transactionService.createTransaction(request);
                    totalIngested++;
                }
            }

            log.info("Source={} complete: ingested={}, skipped={}",
                    dataSource.sourceName(), totalIngested, totalSkipped);
        }

        log.info("Aggregation complete for customerId={}: totalIngested={}, totalSkipped={}",
                customerId, totalIngested, totalSkipped);

        return AggregationResultResponse.builder()
                .customerId(customerId)
                .transactionsIngested(totalIngested)
                .duplicatesSkipped(totalSkipped)
                .source(sourceNames)
                .build();
    }
}
