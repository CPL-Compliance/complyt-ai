package com.complyt.v1.models;

import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.List;

@AllArgsConstructor
@With
@Value
@Schema(name = "Transaction")
public class TransactionDto {
    @Size(min = 1, max = 256, message = "ID length should be 1-256 characters maximum")
    String id;

    @NotBlank(message = "External ID may not be blank")
    @Size(min = 1, max = 256, message = "External ID length should be 1-256 characters maximum")
    String externalId;

    @NotEmpty(message = "Items list cannot be empty")
    @NotNull(message = "Items may not be null")
    List<@Valid ItemDto> items;

    @Valid
    @NotNull(message = "Billing address may not be null")
    AddressDto billingAddress;

    @Valid
    @NotNull(message = "Shipping address may not be null")
    AddressDto shippingAddress;

    @NotNull(message = "Customer Id may not be null")
    ObjectId customerId;

    @Valid
    CustomerDto customer;

    @Valid
    SalesTaxDto salesTax;

    @NotNull(message = "Transaction Status type may not be null")
    TransactionStatusDto transactionStatus;

    @Valid
    TimestampsDto internalTimestamps;

    @Valid
    @NotNull(message = "External timestamps may not be null")
    TimestampsDto externalTimestamps;

    @NotNull(message = "Transaction Type may not be null")
    TransactionTypeDto transactionType;

    @Valid
    ShippingFeeDto shippingFee;

    @NotBlank(message = "Created From may not be blank")
    @Size(min = 1, max = 256, message = "Created From should be 1-256 characters maximum")
    String createdFrom;

    @NotNull(message = "Taxable items amount may not be null")
    @PositiveOrZero(message = "Taxable items amount can not be a negative number")
    float taxableItemsAmount;

    @NotNull(message = "Tangible items amount may not be null")
    @PositiveOrZero(message = "Tangible items amount can not be a negative number")
    float tangibleItemsAmount;

    @NotNull(message = "Total items amount may not be null")
    @PositiveOrZero(message = "Total items amount can not be a negative number")
    float totalItemsAmount;
}