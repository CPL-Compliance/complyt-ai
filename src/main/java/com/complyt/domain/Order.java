package com.complyt.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "order")
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class Order {
    @Id
    private String id;
    private String externalId;
    private List<Item> items;
    private Address billingAddress;
    private Address shippingAddress;
    private ObjectId customerId;
}
