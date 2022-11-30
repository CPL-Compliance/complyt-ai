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

    @Test
    void test() {
        /* In case there is a new property added, If its of type Taxable - handle rates and amount calculation for it */
        Field[] fields = Transaction.class.getDeclaredFields();
        Assertions.assertEquals(14, fields.length);
    }

    private Transaction transaction;
    private String externalId;
    private LocalDateTime localDateTime;
    private ObjectId customerId;
    private String tenantId;

    @BeforeEach
    void setup() {
        tenantId = UUID.randomUUID().toString();
        externalId = UUID.randomUUID().toString();
        localDateTime = LocalDateTime.now();
        customerId = new ObjectId();
        transaction = createTransaction("1111");
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Transaction(id=1111, externalId=" + externalId +
                ", items=[Item(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=taxCode, jurisdictionalSalesTaxRules=null, salesTaxRate=SalesTaxRate(cityDistrictRate=0.5, cityRate=0.5, countyDistrictRate=0.5, countyRate=0.5, stateRate=0.5, taxRate=0.5), manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=INTANGIBLE, taxableCategory=NOT_TAXABLE)], billingAddress=Address(city=City, country=Country, county=County, state=State, street=Street, zip=Zip), shippingAddress=Address(city=City, country=Country, county=County, state=State, street=Street, zip=Zip), customerId=" + customerId +
                ", customer=null, salesTax=null, transactionStatus=ACTIVE, tenantId=" + tenantId + ", internalTimeStamps=TimeStamps(createdDate=" + localDateTime +
                ", updatedDate=" + localDateTime +
                "), externalTimeStamps=TimeStamps(createdDate=" + localDateTime +
                ", updatedDate=" + localDateTime +
                "), transactionType=INVOICE, shippingFee=ShippingFee(manualSalesTax=false, manualSalesTaxRate=0.0, totalPrice=1000.0, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.0, cities=null), salesTaxRate=null, taxCode=C6S1, taxableCategory=TAXABLE, tangibleCategory=INTANGIBLE))";

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
    void builder_build_ReturnTransaction() {
        // Given
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }};
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
        assertEquals(transaction,actualTransaction);
    }

    @Test void builder_ToString_ReturnString() {
        // Given
        String expectedString = "Transaction.TransactionBuilder(id=null, externalId=null, items=null, billingAddress=null, shippingAddress=null, customerId=null, customer=null, salesTax=null, transactionStatus=null, tenantId=null, internalTimeStamps=null, externalTimeStamps=null, transactionType=null, shippingFee=null)";
        Transaction.TransactionBuilder transactionBuilder = new Transaction.TransactionBuilder();

        // When
        String actualString = transactionBuilder.toString();

        // Then
        assertEquals(expectedString, actualString);
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
}
