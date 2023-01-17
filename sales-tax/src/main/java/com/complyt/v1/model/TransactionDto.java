package com.complyt.v1.model;

import com.complyt.v1.model.customer.CustomerDto;
import com.complyt.v1.model.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@With
@Value
@Schema(name = "Transaction")
public class TransactionDto {
    UUID complytId;
    String externalId;
    String source;
    List<ItemDto> items;
    AddressDto billingAddress;
    AddressDto shippingAddress;
    UUID customerId;
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