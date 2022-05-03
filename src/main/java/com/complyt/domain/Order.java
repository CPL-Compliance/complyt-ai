package com.complyt.domain;

import com.complyt.domain.sales_tax.SalesTax;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "order")
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Builder
public class Order {
    @Id
    private String id;
    private String externalId;
    private List<Item> items;
    private Address billingAddress;
    private Address shippingAddress;
    private ObjectId customerId;
    private SalesTax salesTax;
    private OrderStatus orderStatus;
}
