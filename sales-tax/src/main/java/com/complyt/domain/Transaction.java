package com.complyt.domain;

import com.complyt.annotations.Default;
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
@EqualsAndHashCode(callSuper = true)
@ToString
@Builder
@With
@Document(collection = "transaction")
public class Transaction extends ComplytEntity {
    @Id
    private final String id;
    private final String externalId;
    private final String source;
    private final List<Item> items;
    private final Address billingAddress;
    private final Address shippingAddress;
    private final ObjectId customerId;
    private final Customer customer;
    private final SalesTax salesTax;
    private final TransactionStatus transactionStatus;
    private final String tenantId;
    private final Timestamps internalTimestamps;
    private final Timestamps externalTimestamps;
    private final TransactionType transactionType;
    private final ShippingFee shippingFee;
    private final String createdFrom;

    @Default
    public Transaction(final UUID complytId, final String id, final String externalId, final String source, final List<Item> items, final Address billingAddress, final Address shippingAddress, final ObjectId customerId, final Customer customer, final SalesTax salesTax, final TransactionStatus transactionStatus, final String tenantId, final Timestamps internalTimestamps, final Timestamps externalTimestamps, final TransactionType transactionType, final ShippingFee shippingFee, final String createdFrom) {
        super(complytId);
        this.id = id;
        this.externalId = externalId;
        this.source = source;
        this.items = items;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.customerId = customerId;
        this.customer = customer;
        this.salesTax = salesTax;
        this.transactionStatus = transactionStatus;
        this.tenantId = tenantId;
        this.internalTimestamps = internalTimestamps;
        this.externalTimestamps = externalTimestamps;
        this.transactionType = transactionType;
        this.shippingFee = shippingFee;
        this.createdFrom = createdFrom;
    }

    public Transaction(final String id, final String externalId, final String source, final List<Item> items, final Address billingAddress, final Address shippingAddress, final ObjectId customerId, final Customer customer, final SalesTax salesTax, final TransactionStatus transactionStatus, final String tenantId, final Timestamps internalTimestamps, final Timestamps externalTimestamps, final TransactionType transactionType, final ShippingFee shippingFee, final String createdFrom) {
        super(null);
        this.id = id;
        this.externalId = externalId;
        this.source = source;
        this.items = items;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.customerId = customerId;
        this.customer = customer;
        this.salesTax = salesTax;
        this.transactionStatus = transactionStatus;
        this.tenantId = tenantId;
        this.internalTimestamps = internalTimestamps;
        this.externalTimestamps = externalTimestamps;
        this.transactionType = transactionType;
        this.shippingFee = shippingFee;
        this.createdFrom = createdFrom;
    }

    @Override
    public Transaction withComplytId(UUID complytId) {
        return this.complytId == complytId ?
                this : new Transaction(
                complytId,
                this.id,
                this.externalId,
                this.source,
                this.items,
                this.billingAddress,
                this.shippingAddress,
                this.customerId,
                this.customer,
                this.salesTax,
                this.transactionStatus,
                this.tenantId,
                this.internalTimestamps,
                this.externalTimestamps,
                this.transactionType,
                this.shippingFee,
                this.createdFrom
        );
    }
}