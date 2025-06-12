package io.complyt.domain.transaction;

import com.fasterxml.jackson.annotation.*;
import io.complyt.domain.customer.Customer;
import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.domain.properties.InternalTimestampsProperty;
import io.complyt.domain.sales_tax.SalesTax;
import io.complyt.domain.timestamps.Timestamps;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@With
@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction implements ComplytIdProperty {

    UUID complytId;
    String id;
    String externalId;
    String source;
    String documentName;
    List<Item> items;
    Boolean isTaxInclusive;
    Address billingAddress;
    ShippingAddress shippingAddress;
    UUID customerId;
    Customer customer;
    SalesTax salesTax;
    TransactionStatus transactionStatus;
    String tenantId;
//    Timestamps internalTimestamps;
//    Timestamps externalTimestamps;
    TransactionType transactionType;
//    ShippingFee shippingFee;
    String createdFrom;
    BigDecimal taxableItemsAmount;
    BigDecimal tangibleItemsAmount;
    BigDecimal totalItemsAmount;
    BigDecimal finalTransactionAmount;
    BigDecimal totalDiscount;
    BigDecimal transactionLevelDiscount;
    TransactionFilingStatus transactionFilingStatus;
    String currency;
    BigDecimal refRate;
    ExchangeRateInfo exchangeRateInfo;
    String subsidiary;
    Boolean isRefundLinked;
    BigDecimal refundLinkedPercentage;

    @JsonCreator
    public Transaction(
            @JsonProperty("complytId") UUID complytId,
            @JsonProperty("id") String id,
            @JsonProperty("externalId") String externalId,
            @JsonProperty("source") String source,
            @JsonProperty("documentName") String documentName,
            @JsonProperty("items") List<Item> items,
            @JsonProperty("isTaxInclusive") Boolean isTaxInclusive,
            @JsonProperty("billingAddress") Address billingAddress,
            @JsonProperty("shippingAddress") ShippingAddress shippingAddress,
            @JsonProperty("customerId") UUID customerId,
            @JsonProperty("customer") Customer customer,
            @JsonProperty("salesTax") SalesTax salesTax,
            @JsonProperty("transactionStatus") TransactionStatus transactionStatus,
            @JsonProperty("tenantId") String tenantId,
//            @JsonProperty("internalTimestamps") Timestamps internalTimestamps,
//            @JsonProperty("externalTimestamps") Timestamps externalTimestamps,
            @JsonProperty("transactionType") TransactionType transactionType,
//            @JsonProperty("shippingFee") ShippingFee shippingFee,
            @JsonProperty("createdFrom") String createdFrom,
            @JsonProperty("taxableItemsAmount") BigDecimal taxableItemsAmount,
            @JsonProperty("tangibleItemsAmount") BigDecimal tangibleItemsAmount,
            @JsonProperty("totalItemsAmount") BigDecimal totalItemsAmount,
            @JsonProperty("finalTransactionAmount") BigDecimal finalTransactionAmount,
            @JsonProperty("totalDiscount") BigDecimal totalDiscount,
            @JsonProperty("transactionLevelDiscount") BigDecimal transactionLevelDiscount,
            @JsonProperty("transactionFilingStatus") TransactionFilingStatus transactionFilingStatus,
            @JsonProperty("currency") String currency,
            @JsonProperty("refRate") BigDecimal refRate,
            @JsonProperty("exchangeRateInfo") ExchangeRateInfo exchangeRateInfo,
            @JsonProperty("subsidiary") String subsidiary,
            @JsonProperty("isRefundLinked") Boolean isRefundLinked,
            @JsonProperty("refundLinkedPercentage") BigDecimal refundLinkedPercentage
    ) {
        this.complytId = complytId;
        this.id = id;
        this.externalId = externalId;
        this.source = source;
        this.documentName = documentName;
        this.items = items;
        this.isTaxInclusive = isTaxInclusive;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.customerId = customerId;
        this.customer = customer;
        this.salesTax = salesTax;
        this.transactionStatus = transactionStatus;
        this.tenantId = tenantId;
//        this.internalTimestamps = internalTimestamps;
//        this.externalTimestamps = externalTimestamps;
        this.transactionType = transactionType;
//        this.shippingFee = shippingFee;
        this.createdFrom = createdFrom;
        this.taxableItemsAmount = taxableItemsAmount;
        this.tangibleItemsAmount = tangibleItemsAmount;
        this.totalItemsAmount = totalItemsAmount;
        this.finalTransactionAmount = finalTransactionAmount;
        this.totalDiscount = totalDiscount;
        this.transactionLevelDiscount = transactionLevelDiscount;
        this.transactionFilingStatus = transactionFilingStatus;
        this.currency = currency;
        this.refRate = refRate;
        this.exchangeRateInfo = exchangeRateInfo;
        this.subsidiary = subsidiary;
        this.isRefundLinked = isRefundLinked;
        this.refundLinkedPercentage = refundLinkedPercentage;
    }

}