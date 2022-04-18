package com.complyt.v1.model;

import io.swagger.annotations.ApiModel;
import lombok.*;
import org.bson.types.ObjectId;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ApiModel("Order")
public class OrderDto {
    private String externalId;
    private List<ItemDto> items;
    private AddressDto billingAddress;
    private AddressDto shippingAddress;
    private ObjectId customerId;

}
