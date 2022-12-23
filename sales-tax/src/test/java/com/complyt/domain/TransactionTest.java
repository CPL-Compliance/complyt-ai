package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {

    private Transaction transaction;
    private String externalId;
    private LocalDateTime localDateTime;
    private ObjectId customerId;
    private String tenantId;
    private String transactionId;

    @Test
    void testingAmountOfPropertiesInTransaction() {
        /* In case there is a new property added, If its of type Taxable - handle rates and amount calculation for it */
        Field[] fields = Transaction.class.getDeclaredFields();
        Assertions.assertEquals(15, fields.length);
    }

    @BeforeEach
    void setup() {
        tenantId = UUID.randomUUID().toString();
        externalId = UUID.randomUUID().toString();
        localDateTime = LocalDateTime.now();
        customerId = new ObjectId();
        transactionId = UUID.randomUUID().toString();
        transaction = createTransaction(transactionId);
    }

    private Transaction createTransaction(String id) {
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }
        };
        ComplytTimestamp complytTimestamp = new ComplytTimestamp(localDateTime);
        Timestamps timeStamps = new Timestamps(complytTimestamp, complytTimestamp);
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee, null);
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, rules, null, "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0, null);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Transaction(id=" + transaction.getId() +
                ", externalId=" + transaction.getExternalId() +
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
                ", createdFrom=" + transaction.getCreatedFrom() + ")";

        // When
        String actualString = transaction.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withId_DifferentId_ReturnTransaction() {
        // Given
        String differentId;
        do differentId = UUID.randomUUID().toString();
        while (differentId == transactionId);
        Transaction expectedTransaction = createTransaction(differentId);

        // When
        Transaction actualTransaction = transaction.withId(differentId);

        // Then
        assertEquals(expectedTransaction, actualTransaction);
    }

    @Test
    void Builder_Build_ReturnTransaction() {
        // Given
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }
        };
        ComplytTimestamp complytTimestamp = new ComplytTimestamp(localDateTime);
        Timestamps timeStamps = new Timestamps(complytTimestamp, complytTimestamp);
        Transaction.TransactionBuilder transactionBuilder = Transaction.builder();
        ShippingFee shippingFee = createShippingFee();

        // When
        Transaction actualTransaction = transactionBuilder
                .id(transactionId)
                .externalId(externalId)
                .items(items)
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .customerId(customerId)
                .customer(null)
                .salesTax(null)
                .transactionStatus(TransactionStatus.ACTIVE)
                .tenantId(tenantId)
                .internalTimestamps(timeStamps)
                .externalTimestamps(timeStamps)
                .transactionType(TransactionType.INVOICE)
                .shippingFee(shippingFee).build();

        // Then
        assertEquals(transaction, actualTransaction);
    }
}
