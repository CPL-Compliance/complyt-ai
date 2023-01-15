package com.complyt.v1.model;

import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionDtoTest {

    private TransactionDto transactionDto;
    private String transactionId;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setup() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transactionId = UUID.randomUUID().toString();
        transactionDto = domainObjectStub.createTransactionDto(transactionId);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TransactionDto(complytId=" + transactionDto.getComplytId() +
                ", id=" + transactionDto.getId() +
                ", externalId=" + transactionDto.getExternalId() +
                ", source=" + transactionDto.getSource() +
                ", items=" + transactionDto.getItems() +
                ", billingAddress=" + transactionDto.getBillingAddress() +
                ", shippingAddress=" + transactionDto.getShippingAddress() +
                ", customerId=" + transactionDto.getCustomerId() +
                ", customer=" + transactionDto.getCustomer() +
                ", salesTax=" + transactionDto.getSalesTax() +
                ", transactionStatus=" + transactionDto.getTransactionStatus() +
                ", internalTimestamps=" + transactionDto.getInternalTimestamps() +
                ", externalTimestamps=" + transactionDto.getExternalTimestamps() +
                ", transactionType=" + transactionDto.getTransactionType() +
                ", shippingFee=" + transactionDto.getShippingFee() +
                ", createdFrom=" + transactionDto.getCreatedFrom() + ")";

        // When
        String actualString = transactionDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withId_DifferentId_ReturnTransactionDto() {
        // Given
        String differentId = UUID.randomUUID().toString();
        TransactionDto expectedTransactionDto = domainObjectStub.createTransactionDto(differentId)
                .withComplytId(transactionDto.getComplytId())
                .withExternalId(transactionDto.getExternalId())
                .withCustomerId(transactionDto.getCustomerId())
                .withCustomer(transactionDto.getCustomer());

        // When
        TransactionDto actualTransactionDto = transactionDto.withId(differentId);

        // Then
        assertEquals(expectedTransactionDto, actualTransactionDto);
    }

}