package com.complyt.v1.mappers;

import com.complyt.domain.transaction.Transaction;
import com.complyt.v1.models.transaction.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, uses = {TimestampsMapper.class, ItemMapper.class, ShippingFeeMapper.class})
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    Transaction transactionDtoToTransaction(TransactionDto transactionDto);

    TransactionDto transactionToTransactionDto(Transaction transaction);
}