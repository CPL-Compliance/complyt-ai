package com.complyt.v1.model;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class OrderDto {
    private String externalId;
    private List<ItemDto> items;
    private AddressDto billingAddress;
    private AddressDto shippingAddress;
    private ObjectId customerId;

}
