package com.complyt.v1.model;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class OrderDto {
    private String externalId;
    private Address billingAddress;
    private Address shippingAddress;
    private ObjectId customerId;
    private Item[] items;
}
