package com.complyt.domain;

import com.complyt.domain.nexus.NexusTracking;
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
@Document(collection = "order")
public class Order {
    @Id
    private String id;
    private String externalId;
    private List<Item> items;
    private Address billingAddress;
    private Address shippingAddress;
    private ObjectId customerId;
    private Customer customer;
    private SalesTax salesTax;
    private OrderStatus orderStatus;
    private ObjectId clientId;
    private TimeStamps internalTimeStamps;
    private TimeStamps externalTimeStamps;
    private NexusTracking nexusTracking;
}