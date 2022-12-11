package com.complyt.domain;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.sales_tax.SalesTax;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Builder
@Document(collection = "transaction")
public class Transaction {
    @Id
    private final String id;
    private final String externalId;
    private final List<Item> items;
    private final Address billingAddress;
    private final Address shippingAddress;
    private final ObjectId customerId;
    private final Customer customer;
    private final SalesTax salesTax;
    private final TransactionStatus transactionStatus;
    private final String tenantId;
    private final TimeStamps internalTimeStamps;
    private final TimeStamps externalTimeStamps;
    private final TransactionType transactionType;
    private final ShippingFee shippingFee;
    private final String createdFrom;
}