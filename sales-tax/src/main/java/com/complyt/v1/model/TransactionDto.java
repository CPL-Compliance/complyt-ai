package com.complyt.v1.model;

import com.complyt.v1.model.customer.CustomerDto;
import com.complyt.v1.model.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.List;

@AllArgsConstructor
@ToString
@With
@Value
@Schema(name = "Transaction")
public class TransactionDto {
    String id;
    String externalId;
    List<ItemDto> items;
    AddressDto billingAddress;
    AddressDto shippingAddress;
    ObjectId customerId;
    CustomerDto customer;
    SalesTaxDto salesTax;
    TransactionStatusDto transactionStatus;
    TimestampsDto internalTimestamps;
    TimestampsDto externalTimestamps;
    TransactionTypeDto transactionType;
    ShippingFeeDto shippingFee;
    String createdFrom;
    float taxableItemsAmount;
    float tangibleItemsAmount;
    float totalItemsAmount;
}