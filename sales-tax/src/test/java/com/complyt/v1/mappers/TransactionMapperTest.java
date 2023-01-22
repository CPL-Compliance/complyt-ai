package com.complyt.v1.mappers;

import com.complyt.domain.Transaction;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.model.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TransactionMapperTest {

    private Transaction transaction;
    private Transaction transactionNoTenantNorId;
    private TransactionDto transactionDto;
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setup() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transaction = domainObjectStub.createTransaction(UUID.randomUUID().toString());
        transactionNoTenantNorId = transaction.withTenantId(null).withCustomer(transaction.getCustomer().withTenantId(null).withId(null)).withId(null);
        transactionDto = domainObjectStub.createTransactionDto(transaction.getId())
                .withComplytId(transaction.getComplytId())
                .withCustomer(CustomerMapper.INSTANCE.customerToCustomerDto(transaction.getCustomer()));
    }

    @Test
    void transactionToTransactionDto_Transaction_returnTransactionDto() {

        // Given
        Transaction givenTransaction = transaction;

        // When
        TransactionDto actualTransactionDto = TransactionMapper.INSTANCE.transactionToTransactionDto(givenTransaction);

        // Then
        assertEquals(transactionDto, actualTransactionDto);
    }

    @Test
    void transactionDtoToTransaction_TransactionDto_returnTransaction() {

        // Given
        TransactionDto givenTransactionDto = transactionDto;

        // When
        Transaction actualTransaction = TransactionMapper.INSTANCE.transactionDtoToTransaction(givenTransactionDto);

        // Then
        assertEquals(transactionNoTenantNorId, actualTransaction);
    }

    @Test
    void mapping_NullState_ReturnNull() {
        // Given + When
        Transaction givenTransaction = TransactionMapper.INSTANCE.transactionDtoToTransaction(null);
        TransactionDto givenTransactionDto = TransactionMapper.INSTANCE.transactionToTransactionDto(null);

        // Then
        assertNull(givenTransaction);
        assertNull(givenTransactionDto);
    }
}
