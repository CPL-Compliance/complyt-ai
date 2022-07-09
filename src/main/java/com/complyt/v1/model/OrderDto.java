package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Schema(name = "Order")
public class OrderDto {
    private String externalId;
    private List<ItemDto> items;
    private AddressDto billingAddress;
    private AddressDto shippingAddress;
    private ObjectId customerId;
    private CustomerDto customer;
    private SalesTaxDto salesTax;
    private OrderStatusDto orderStatus;
    private TimeStampsDto internalTimeStamps;
    private TimeStampsDto externalTimeStamps;
    private CustomerTypeDto customerType;
}