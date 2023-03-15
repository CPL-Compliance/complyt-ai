package com.complyt.v1.models;

import com.complyt.v1.models.checkables.ExternalIdCheckable;
import com.complyt.v1.models.checkables.SourceCheckable;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.With;

import java.util.List;
import java.util.UUID;
@With
@Schema(name = "Transaction")
public record TransactionDto(UUID complytId,
                             @Schema(description = "the attribute designed to receive a unique identifier provided by API consumers themselves (e.g. pass your own transaction ID)") @NotBlank(message = "External ID may not be blank") @Size(min = 1, max = 256, message = "External ID should be 1-256 characters maximum") String externalId,
                             @NotBlank(message = "Source may not be blank") @Pattern(regexp = "[1-9]", message = "Source should be a single digit") String source,
                             @NotEmpty(message = "Items list cannot be empty") @NotNull(message = "Items may not be null") List<@Valid ItemDto> items,
                             @Valid OptionalAddressDto billingAddress,
                             @Valid @NotNull(message = "Shipping address may not be null") MandatoryAddressDto shippingAddress,
                             @NotNull(message = "Customer Id may not be null") UUID customerId,
                             @Valid CustomerDto customer, @Valid SalesTaxDto salesTax,
                             @NotNull(message = "Transaction Status type may not be null") TransactionStatusDto transactionStatus,
                             @Valid TimestampsDto internalTimestamps,
                             @Valid @NotNull(message = "External Timestamps may not be null") TimestampsDto externalTimestamps,
                             @NotNull(message = "Transaction Type may not be null") TransactionTypeDto transactionType,
                             @Valid ShippingFeeDto shippingFee,
                             @Size(min = 1, max = 256, message = "Created From should be 1-256 characters maximum") String createdFrom,
                             float taxableItemsAmount, float tangibleItemsAmount, float totalItemsAmount)
        implements SourceCheckable, ExternalIdCheckable {

}