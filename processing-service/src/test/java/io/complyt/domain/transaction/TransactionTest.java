package io.complyt.domain.transaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionTest {

    UnitTestUtilities testUtilities;
    LocalDateTime localDateTime;
    private Transaction transaction;
    private String transactionId;

    @Test
    void testingAmountOfPropertiesInTransaction() {
        /* In case there is a new property added, If it's of type Taxable - handle rates and amount calculation for it */
        Field[] fields = Transaction.class.getDeclaredFields();
        Assertions.assertEquals(32, fields.length);
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
        String expectedString = "Transaction[complytId=" + transaction.complytId() +
                ", id=" + transaction.id() +
                ", externalId=" + transaction.externalId() +
                ", source=" + transaction.source() +
                ", documentName=" + transaction.documentName() +
                ", items=" + transaction.items() +
                ", isTaxInclusive=" + transaction.isTaxInclusive() +
                ", billingAddress=" + transaction.billingAddress() +
                ", shippingAddress=" + transaction.shippingAddress() +
                ", customerId=" + transaction.customerId() +
                ", customer=" + transaction.customer() +
                ", salesTax=" + transaction.salesTax() +
                ", transactionStatus=" + transaction.transactionStatus() +
                ", tenantId=" + transaction.tenantId() +
                ", internalTimestamps=" + transaction.internalTimestamps() +
                ", externalTimestamps=" + transaction.externalTimestamps() +
                ", transactionType=" + transaction.transactionType() +
                ", shippingFee=" + transaction.shippingFee() +
                ", createdFrom=" + transaction.createdFrom() +
                ", taxableItemsAmount=" + transaction.taxableItemsAmount() +
                ", tangibleItemsAmount=" + transaction.tangibleItemsAmount() +
                ", totalItemsAmount=" + transaction.totalItemsAmount() +
                ", finalTransactionAmount=" + transaction.finalTransactionAmount() +
                ", totalDiscount=" + transaction.totalDiscount() +
                ", transactionLevelDiscount=" + transaction.transactionLevelDiscount() +
                ", transactionFilingStatus=" + transaction.transactionFilingStatus() +
                ", currency=" + transaction.currency() +
                ", refRate=" + transaction.refRate() +
                ", exchangeRateInfo=" + transaction.exchangeRateInfo() +
                ", subsidiary=" + transaction.subsidiary() +
                ", isRefundLinked=" + transaction.isRefundLinked() +
                ", refundLinkedPercentage=" + transaction.refundLinkedPercentage() +
                "]";

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
                .withComplytId(transaction.complytId())
                .withExternalId(transaction.externalId())
                .withCustomer(transaction.customer());

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
                .complytId(transaction.complytId())
                .id(transactionId)
                .externalId(transaction.externalId())
                .source(transaction.source())
                .documentName(transaction.documentName())
                .items(transaction.items())
                .isTaxInclusive(transaction.isTaxInclusive())
                .billingAddress(transaction.billingAddress())
                .shippingAddress(transaction.shippingAddress())
                .customerId(transaction.customerId())
                .customer(transaction.customer())
                .salesTax(transaction.salesTax())
                .transactionStatus(transaction.transactionStatus())
                .tenantId(transaction.tenantId())
                .internalTimestamps(transaction.internalTimestamps())
                .externalTimestamps(transaction.externalTimestamps())
                .transactionType(transaction.transactionType())
                .shippingFee(transaction.shippingFee())
                .tangibleItemsAmount(transaction.tangibleItemsAmount())
                .taxableItemsAmount(transaction.taxableItemsAmount())
                .totalItemsAmount(transaction.totalItemsAmount())
                .transactionFilingStatus(transaction.transactionFilingStatus())
                .finalTransactionAmount(transaction.finalTransactionAmount())
                .totalDiscount(transaction.totalDiscount())
                .transactionLevelDiscount(transaction.transactionLevelDiscount())
                .currency(transaction.currency())
                .subsidiary(transaction.subsidiary())
                .isRefundLinked(transaction.isRefundLinked())
                .build();

        // Then
        assertEquals(transaction, actualTransaction);
    }
}
