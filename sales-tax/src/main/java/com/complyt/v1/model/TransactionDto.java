package com.complyt.v1.model;

import com.complyt.v1.model.customer.CustomerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Schema(name = "Transaction")
public class TransactionDto {
    private final String id;
    private final String externalId;
    private final List<ItemDto> items;
    private final AddressDto billingAddress;
    private final AddressDto shippingAddress;
    private final ObjectId customerId;
    private final CustomerDto customer;
    private final SalesTaxDto salesTax;
    private final TransactionStatusDto transactionStatus;
    private final TimeStampsDto internalTimeStamps;
    private final TimeStampsDto externalTimeStamps;
    private final TransactionTypeDto transactionType;
    private final ShippingFeeDto shippingFee;
    private final String createdFrom;
}