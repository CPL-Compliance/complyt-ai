package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
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

    @Test
    void test() {
        /* In case there is a new property added, If its of type Taxable - handle rates and amount calculation for it */
        Field[] fields = Transaction.class.getDeclaredFields();
        Assertions.assertEquals(14, fields.length);
    }

    @BeforeEach
    void setup() {
        tenantId = UUID.randomUUID().toString();
        externalId = UUID.randomUUID().toString();
        localDateTime = LocalDateTime.now();
        customerId = new ObjectId();
        transaction = createTransaction("1111");
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
        TimeStamps timeStamps = new TimeStamps(localDateTime, localDateTime);
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee);
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
                ", customer=null, salesTax=null, transactionStatus=" + transaction.getTransactionStatus() +
                ", tenantId=" + transaction.getTenantId() +
                ", internalTimeStamps=" + transaction.getInternalTimeStamps() +
                ", externalTimeStamps=" + transaction.getExternalTimeStamps() +
                ", transactionType=INVOICE, shippingFee=" + transaction.getShippingFee() + ")";

        // When
        String actualString = transaction.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withId_DifferentId_ReturnTransaction() {
        // Given
        Transaction expectedTransaction = createTransaction("2222");

        // When
        Transaction actualTransaction = transaction.withId("2222");

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
        TimeStamps timeStamps = new TimeStamps(localDateTime, localDateTime);
        Transaction.TransactionBuilder transactionBuilder = Transaction.builder();

        // When
        Transaction actualTransaction = transactionBuilder
                .id("1111")
                .externalId(externalId)
                .items(items)
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .customerId(customerId)
                .customer(null)
                .salesTax(null)
                .transactionStatus(TransactionStatus.ACTIVE)
                .tenantId(tenantId)
                .internalTimeStamps(timeStamps)
                .externalTimeStamps(timeStamps)
                .transactionType(TransactionType.INVOICE)
                .shippingFee(createShippingFee()).build();

        // Then
        assertEquals(transaction, actualTransaction);
    }

    @Test
    void builder_ToString_ReturnString() {
        // Given
        String expectedString = "Transaction.TransactionBuilder(id=null, externalId=null, items=null, billingAddress=null, shippingAddress=null, customerId=null, customer=null, salesTax=null, transactionStatus=null, tenantId=null, internalTimeStamps=null, externalTimeStamps=null, transactionType=null, shippingFee=null)";
        Transaction.TransactionBuilder transactionBuilder = new Transaction.TransactionBuilder();

        // When
        String actualString = transactionBuilder.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}
