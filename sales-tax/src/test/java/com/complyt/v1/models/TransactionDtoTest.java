package com.complyt.v1.models;

import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionDtoTest {

    UnitTestUtilities testUtilities;
    private TransactionDto transactionDto;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        String transactionId = UUID.randomUUID().toString();
        transactionDto = testUtilities.createTransactionDto(transactionId);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", BigDecimal.ZERO, null);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TransactionDto[complytId=" + transactionDto.complytId() +
                ", externalId=" + transactionDto.externalId() +
                ", source=" + transactionDto.source() +
                ", documentName=" + transactionDto.documentName() +
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
                ", totalItemsAmount=" + transactionDto.totalItemsAmount() +
                ", transactionFilingStatus=" + transactionDto.transactionFilingStatus() + "]";

        // When
        String actualString = transactionDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withComplytId_DifferentId_ReturnTransactionDto() {
        // Given
        UUID differentId = UUID.randomUUID();
        TransactionDto expectedTransactionDto = testUtilities.createTransactionDto(transactionDto.externalId())
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