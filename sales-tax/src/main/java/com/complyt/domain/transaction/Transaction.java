package com.complyt.domain.transaction;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.timestamps.Timestamps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Value
@Builder
@With
@Document(collection = "transaction")
@AllArgsConstructor
public class Transaction implements ComplytIdProperty {

    UUID complytId;
    @Id
    String id;
    String externalId;
    String source;
    String documentName;
    List<Item> items;
    Address billingAddress;
    Address shippingAddress;
    UUID customerId;
    Customer customer;
    SalesTax salesTax;
    TransactionStatus transactionStatus;
    String tenantId;
    Timestamps internalTimestamps;
    Timestamps externalTimestamps;
    TransactionType transactionType;
    ShippingFee shippingFee;
    String createdFrom;
    BigDecimal taxableItemsAmount;
    BigDecimal tangibleItemsAmount;
    BigDecimal totalItemsAmount;
    BigDecimal totalDiscount;
    TransactionFilingStatus transactionFilingStatus;
}