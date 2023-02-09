package com.complyt.v1.models;

import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionDtoTest {

    private TransactionDto transactionDto;
    private String transactionId;

    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transactionId = UUID.randomUUID().toString();
        transactionDto = objectStub.createTransactionDto(transactionId);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TransactionDto(complytId=" + transactionDto.complytId() +
                ", externalId=" + transactionDto.externalId() +
                ", source=" + transactionDto.source() +
                ", items=" + transactionDto.items() +
                ", billingAddress=" + transactionDto.billingAddress() +
                ", shippingAddress=" + transactionDto.shippingAddress() +
                ", customerId=" + transactionDto.customerId() +
                ", customer=" + transactionDto.customer() +
                ", salesTax=" + transactionDto.salesTax() +
                ", transactionStatus=" + transactionDto.transactionStatus() +
                ", internalTimestamps=" + transactionDto.internalTimestamps() +
                ", externalTimestamps=" + transactionDto.externalTimestamps() +
                ", transactionType=" + transactionDto.transactionType() +
                ", shippingFee=" + transactionDto.shippingFee() +
                ", createdFrom=" + transactionDto.createdFrom() +
                ", taxableItemsAmount=" + transactionDto.taxableItemsAmount() +
                ", tangibleItemsAmount=" + transactionDto.tangibleItemsAmount() +
                ", totalItemsAmount=" + transactionDto.totalItemsAmount() + ")";

        // When
        String actualString = transactionDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withConmplytId_DifferentId_ReturnTransactionDto() {
        // Given
        UUID differentId = UUID.randomUUID();
        TransactionDto expectedTransactionDto = objectStub.createTransactionDto(transactionDto.externalId())
                .withComplytId(differentId)
                .withExternalId(transactionDto.externalId())
                .withCustomerId(transactionDto.customerId())
                .withCustomer(transactionDto.customer());

        // When
        TransactionDto actualTransactionDto = transactionDto.withComplytId(differentId);

        // Then
        assertEquals(expectedTransactionDto, actualTransactionDto);
    }

}