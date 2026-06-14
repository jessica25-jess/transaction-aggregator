package com.jessica.transactions.util;

import com.jessica.transactions.dto.TransactionDtos.CreateTransactionRequest;
import com.jessica.transactions.dto.TransactionDtos.TransactionResponse;
import com.jessica.transactions.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "currency", defaultValue = "ZAR")
    Transaction toEntity(CreateTransactionRequest request);

    TransactionResponse toResponse(Transaction transaction);
}
