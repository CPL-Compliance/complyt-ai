package com.complyt.domain.transaction;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.timestamps.Timestamps;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@With
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "transaction")
@Data
@Accessors(chain = true)
public class Transaction implements ComplytIdProperty {

    UUID complytId;
    @Id
    String id;
    String externalId;
    String source;
    String documentName;
    List<Item> items;
    Boolean isTaxInclusive;
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

}