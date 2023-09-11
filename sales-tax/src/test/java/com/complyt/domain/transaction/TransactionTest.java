package com.complyt.domain.transaction;

import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {

    UnitTestUtilities testUtilities;
    LocalDateTime localDateTime;
    private Transaction transaction;
    private String transactionId;

    @Test
    void testingAmountOfPropertiesInTransaction() {
        /* In case there is a new property added, If its of type Taxable - handle rates and amount calculation for it */
        Field[] fields = Transaction.class.getDeclaredFields();
        Assertions.assertEquals(22, fields.length);
    }

    @BeforeEach
    void setup() {
        localDateTime = LocalDateTime.now();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transactionId = UUID.randomUUID().toString();
        transaction = testUtilities.createTransaction(transactionId);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Transaction(complytId=" + transaction.getComplytId() +
                ", id=" + transaction.getId() +
                ", externalId=" + transaction.getExternalId() +
                ", source=" + transaction.getSource() +
                ", documentName=" + transaction.getDocumentName() +
                ", items=" + transaction.getItems() +
                ", billingAddress=" + transaction.getBillingAddress() +
                ", shippingAddress=" + transaction.getShippingAddress() +
                ", customerId=" + transaction.getCustomerId() +
                ", customer=" + transaction.getCustomer() +
                ", salesTax=" + transaction.getSalesTax() +
                ", transactionStatus=" + transaction.getTransactionStatus() +
                ", tenantId=" + transaction.getTenantId() +
                ", internalTimestamps=" + transaction.getInternalTimestamps() +
                ", externalTimestamps=" + transaction.getExternalTimestamps() +
                ", transactionType=" + transaction.getTransactionType() +
                ", shippingFee=" + transaction.getShippingFee() +
                ", createdFrom=" + transaction.getCreatedFrom() +
                ", taxableItemsAmount=" + transaction.getTaxableItemsAmount() +
                ", tangibleItemsAmount=" + transaction.getTangibleItemsAmount() +
                ", totalItemsAmount=" + transaction.getTotalItemsAmount() +
                ", transactionFilingStatus=" + transaction.getTransactionFilingStatus() + ")";

        // When
        String actualString = transaction.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withId_DifferentId_ReturnTransaction() {
        // Given
        String differentId = UUID.randomUUID().toString();
        Transaction expectedTransaction = testUtilities.createTransaction(differentId)
                .withComplytId(transaction.getComplytId())
                .withExternalId(transaction.getExternalId())
                .withCustomer(transaction.getCustomer());
        // When
        Transaction actualTransaction = transaction.withId(differentId);

        // Then
        assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    void Builder_Build_ReturnTransaction() {
        // Given
        Transaction.TransactionBuilder transactionBuilder = Transaction.builder();

        // When
        Transaction actualTransaction = transactionBuilder
                .complytId(transaction.getComplytId())
                .id(transactionId)
                .externalId(transaction.getExternalId())
                .source(transaction.getSource())
                .documentName(transaction.getDocumentName())
                .items(transaction.getItems())
                .billingAddress(transaction.getBillingAddress())
                .shippingAddress(transaction.getShippingAddress())
                .customerId(transaction.getCustomerId())
                .customer(transaction.getCustomer())
                .salesTax(transaction.getSalesTax())
                .transactionStatus(transaction.getTransactionStatus())
                .tenantId(transaction.getTenantId())
                .internalTimestamps(transaction.getInternalTimestamps())
                .externalTimestamps(transaction.getExternalTimestamps())
                .transactionType(transaction.getTransactionType())
                .shippingFee(transaction.getShippingFee())
                .tangibleItemsAmount(transaction.getTangibleItemsAmount())
                .taxableItemsAmount(transaction.getTaxableItemsAmount())
                .totalItemsAmount(transaction.getTotalItemsAmount())
                .transactionFilingStatus(transaction.getTransactionFilingStatus()).build();

        // Then
        assertEquals(transaction, actualTransaction);
    }
}
