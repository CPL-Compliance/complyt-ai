package com.complyt.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bson.Document;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class TransactionProjectionStageTest {
    ArrayList<String> expectedPropertiesList = new ArrayList<>() {{
        add("complytId");
        add("externalId");
        add("source");
        add("customerId");
        add("documentName");
        add("subsidiary");
        add("currency");
        add("tangibleItemsAmount");
        add("taxableItemsAmount");
        add("totalDiscount");
        add("totalItemsAmount");
        add("finalTransactionAmount");
        add("transactionStatus");
        add("tenantId");
        add("billingAddress");
        add("shippingAddress");
        add("internalTimestamps");
        add("externalTimestamps");
        add("transactionType");
        add("shippingFee.manualSalesTax");
        add("shippingFee.manualSalesTaxRate");
        add("shippingFee.totalPrice");
        add("shippingFee.salesTaxRates.taxRate");
        add("shippingFee.gtRates.taxRate");
        add("shippingFee.taxCode");
        add("shippingFee.taxableCategory");
        add("shippingFee.tangibleCategory");
        add("shippingFee.calculatedTotal");
        add("items");
        add("salesTax.amount");
        add("salesTax.rate");
        add("salesTax.salesTaxRates.taxRate");
        add("salesTax.gtRates.taxRate");
        add("customer.complytId");
        add("customer.externalId");
        add("customer.name");
        add("customer.customerType");
        add("exchangeRateInfo.finalTransactionAmountInUsd");
        add("exchangeRateInfo.fxRate");
        add("exchangeRateInfo.fromCurrency");
        add("exchangeRateInfo.toCurrency");
        add("isRefundLinked");
        add("refundLinkedPercentage");
    }};

    @Test
    public void propertiesList_VerifyNumberOfProperties() {
        assertEquals(expectedPropertiesList.size(),
                TransactionProjectionStage.propertiesList.size());
    }

    @Test
    public void propertiesList_VerifyOfProperties() {
        assertEquals(expectedPropertiesList, TransactionProjectionStage.propertiesList);
    }

    @Test
    public void projectionStageDocument_VerifyReturnedDocument() {
        Document projectionPropertiesAppendedList = new Document();
        expectedPropertiesList.forEach(key -> projectionPropertiesAppendedList.append(key, 1));
        Document expectedDocument = new Document("$project", projectionPropertiesAppendedList);
        assertEquals(expectedDocument, TransactionProjectionStage.projectionStageDocument());
    }

    @Test
    public void itemsMapAddFeildStageDocument_VerifyReturnedDocument() {
        Document itemsDocument = new Document("$map",
                new Document("input", "$items")
                        .append("as", "item")
                        .append("in",
                                new Document("unitPrice", "$$item.unitPrice")
                                        .append("quantity", "$$item.quantity")
                                        .append("calculatedTotal", "$$item.calculatedTotal")
                                        .append("discount", "$$item.discount")
                                        .append("description", "$$item.description")
                                        .append("name", "$$item.name")
                                        .append("taxCode", "$$item.taxCode")
                                        .append("manualSalesTax", "$$item.manualSalesTax")
                                        .append("manualSalesTaxRate", "$$item.manualSalesTaxRate")
                                        .append("totalPrice", "$$item.totalPrice")
                                        .append("tangibleCategory", "$$item.tangibleCategory")
                                        .append("taxableCategory", "$$item.taxableCategory")
                                        .append("salesTaxRates", new Document("$cond", new Document()
                                                .append("if", new Document("$ne", Arrays.asList(new Document("$type", "$$item.salesTaxRates"), "missing")))
                                                .append("then", new Document()
                                                        .append("taxRate", "$$item.salesTaxRates.taxRate"))
                                                .append("else", "$$REMOVE")
                                        )).append("gtRates", new Document("$cond", new Document()
                                                .append("if", new Document("$ne", Arrays.asList(new Document("$type", "$$item.gtRates"), "missing")))
                                                .append("then", new Document()
                                                        .append("taxRate", "$$item.gtRates.taxRate"))
                                                .append("else", "$$REMOVE")
                                        ))
                        )
        );

        assertEquals(itemsDocument, TransactionProjectionStage.itemsMapAddFeildStageDocument());
    }
}
