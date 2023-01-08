package com.complyt.domain;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.timestamps.Timestamps;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Document(collection = "transaction")
public class Transaction {
    @Id
    String id;
    String externalId;
    List<Item> items;
    Address billingAddress;
    Address shippingAddress;
    ObjectId customerId;
    Customer customer;
    SalesTax salesTax;
    TransactionStatus transactionStatus;
    String tenantId;
    Timestamps internalTimestamps;
    Timestamps externalTimestamps;
    TransactionType transactionType;
    ShippingFee shippingFee;
    String createdFrom;
    float taxablesAmount;
    float tangiblesAmount;
    float totalItemsAmount;

}