package com.complyt.domain;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.timestamps.Timestamps;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder
@With
@Document(collection = "transaction")
public class Transaction {

    private final UUID complytId;
    @Id
    private final String id;
    private final String externalId;
    private final String source;
    private final List<Item> items;
    private final Address billingAddress;
    private final Address shippingAddress;
    private final UUID customerId;
    private final Customer customer;
    private final SalesTax salesTax;
    private final TransactionStatus transactionStatus;
    private final String tenantId;
    private final Timestamps internalTimestamps;
    private final Timestamps externalTimestamps;
    private final TransactionType transactionType;
    private final ShippingFee shippingFee;
    private final String createdFrom;
}