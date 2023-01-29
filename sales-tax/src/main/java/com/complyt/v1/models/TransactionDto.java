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

    @NonNull
    @NotBlank(message = "External ID may not be blank")
    @Size(min = 1, max = 256, message = "External ID length should be 1-256 characters maximum")
    String externalId;

    @NotEmpty(message = "Items list cannot be empty")
    @NonNull
    List<@Valid ItemDto> items;

    @NonNull
    @Valid
    @NotNull(message = "Billing address may not be null")
    AddressDto billingAddress;

    @NonNull
    @Valid
    @NotNull(message = "Shipping address may not be null")
    AddressDto shippingAddress;

    @NonNull
    @NotNull(message = "Customer Id may not be null")
    ObjectId customerId;

    @NonNull
    @Valid
    @NotNull(message = "Customer may not be null")
    CustomerDto customer;

    @NonNull
    @Valid
    @NotNull(message = "Sales Tax may not be null")
    SalesTaxDto salesTax;

    @NonNull
    @NotNull(message = "Transaction Status type may not be null")
    TransactionStatusDto transactionStatus;

    @NonNull
    @Valid
    @NotNull(message = "Internal timestamps may not be null")
    TimestampsDto internalTimestamps;

    @NonNull
    @Valid
    @NotNull(message = "External timestamps may not be null")
    TimestampsDto externalTimestamps;

    @NonNull
    @NotNull(message = "Transaction Type type may not be null")
    TransactionTypeDto transactionType;

    @NonNull
    @Valid
    @NotNull(message = "Shipping Fee may not be null")
    ShippingFeeDto shippingFee;

    @NonNull
    @NotBlank(message = "Created From may not be blank")
    @Size(min = 1, max = 256, message = "Created From should be 1-256 characters maximum")
    String createdFrom;

    @Min(value = 0, message = "Taxable items amount's minimum value is 0")
    float taxableItemsAmount;

    @Min(value = 0, message = "Tangible items amount's minimum value is 0")
    float tangibleItemsAmount;

    @Min(value = 0, message = "Total items amount's minimum value is 0")
    float totalItemsAmount;
}