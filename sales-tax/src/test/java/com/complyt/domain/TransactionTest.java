package com.complyt.domain;

import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {

    private Transaction transaction;
    private String transactionId;
    ObjectStub objectStub;

    LocalDateTime localDateTime;

    @Test
    void testingAmountOfPropertiesInTransaction() {
        /* In case there is a new property added, If its of type Taxable - handle rates and amount calculation for it */
        Field[] fields = Transaction.class.getDeclaredFields();
        Assertions.assertEquals(20, fields.length);
    }

    @BeforeEach
    void setup() {
        localDateTime = LocalDateTime.now();
        objectStub = new ObjectStub(
                new ComplytTimestamp(localDateTime), UUID.randomUUID().toString());
        transactionId = UUID.randomUUID().toString();
        transaction = objectStub.createTransaction(transactionId);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Transaction(complytId=" + transaction.getComplytId() +
                ", id=" + transaction.getId() +
                ", externalId=" + transaction.getExternalId() +
                ", source=" + transaction.getSource() +
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
                ", totalItemsAmount=" + transaction.getTotalItemsAmount() + ")";

        // When
        String actualString = transaction.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withId_DifferentId_ReturnTransaction() {
        // Given
        String differentId = UUID.randomUUID().toString();
        Transaction expectedTransaction = objectStub.createTransaction(differentId)
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
                .shippingFee(transaction.getShippingFee()).build();

        // Then
        assertEquals(transaction, actualTransaction);
    }
}
