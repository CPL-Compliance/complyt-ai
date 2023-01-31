package com.complyt.v1.models;

import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionDtoTest {

    private TransactionDto transactionDto;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        String transactionId = UUID.randomUUID().toString();
        transactionDto = objectStub.createTransactionDto(transactionId);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0, null);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TransactionDto(complytId=" + transactionDto.getComplytId() +
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
                ", createdFrom=" + transactionDto.getCreatedFrom() +
                ", taxableItemsAmount=" + transactionDto.getTaxableItemsAmount() +
                ", tangibleItemsAmount=" + transactionDto.getTangibleItemsAmount() +
                ", totalItemsAmount=" + transactionDto.getTotalItemsAmount() + ")";

        // When
        String actualString = transactionDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withComplytId_DifferentId_ReturnTransactionDto() {
        // Given
        UUID differentId = UUID.randomUUID();
        TransactionDto expectedTransactionDto = objectStub.createTransactionDto(transactionDto.getExternalId())
                .withComplytId(differentId)
                .withExternalId(transactionDto.getExternalId())
                .withCustomerId(transactionDto.getCustomerId())
                .withCustomer(transactionDto.getCustomer());

        // When
        TransactionDto actualTransactionDto = transactionDto.withComplytId(differentId);

        // Then
        assertEquals(expectedTransactionDto, actualTransactionDto);
    }

}