package com.complyt.repositories;

import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;

public interface TransactionProjectionStage {
    ArrayList<String> propertiesList = new ArrayList<>() {{
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

    static Document projectionStageDocument() {
        Document projectionPropertiesAppendedList = new Document();
        propertiesList.forEach(key -> projectionPropertiesAppendedList.append(key, 1));
        return new Document("$project", projectionPropertiesAppendedList);
    }

    static Document itemsMapAddFeildStageDocument() {
        return new Document("$map",
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
    }
}
