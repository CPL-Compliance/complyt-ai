package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.checkables.ExternalIdCheckable;
import com.complyt.v1.models.checkables.SourceCheckable;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.With;

import java.util.List;
import java.util.UUID;

@With
@Schema(name = "transaction", description = FieldsDescriptions.TRANSACTION)
public record TransactionDto(@Schema(description = FieldsDescriptions.COMPLYT_ID + "transaction") UUID complytId,
                             @Schema(description = FieldsDescriptions.EXTERNAL_ID) @NotNull(message = "externalId " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "externalId " + StringErrorMessages.MINMAX_256_ERROR) String externalId,
                             @Schema(description = FieldsDescriptions.SOURCE) @NotNull(message = "source " + DtoErrorMessages.NOT_NULL_ERROR) @Pattern(regexp = "[1-9]", message = "source " + StringErrorMessages.SINGLE_DIGIT_ERROR) String source,
                             @ArraySchema(schema = @Schema(description = FieldsDescriptions.ITEM)) @NotEmpty(message = "items" + DtoErrorMessages.LIST_NOT_EMPTY_ERROR) @NotNull(message = "items " + DtoErrorMessages.NOT_NULL_ERROR) List<@Valid ItemDto> items,
                             @Schema(ref = "billingAddress") @Valid OptionalAddressDto billingAddress,
                             @Schema(ref = "shippingAddress") @Valid @NotNull(message = "shippingAddress " + DtoErrorMessages.NOT_NULL_ERROR) MandatoryAddressDto shippingAddress,
                             @Schema(description = FieldsDescriptions.CUSTOMER_ID + "transaction") @NotNull(message = "customerId " + DtoErrorMessages.NOT_NULL_ERROR) UUID customerId,
                             @Valid CustomerDto customer,
                             @Valid SalesTaxDto salesTax,
                             @Schema(description = FieldsDescriptions.TRANSACTION_STATUS) @NotNull(message = "transactionStatus " + DtoErrorMessages.NOT_NULL_ERROR) TransactionStatusDto transactionStatus,
                             @Schema(ref = "internalTimestamps") @Valid TimestampsDto internalTimestamps,
                             @Schema(ref = "externalTimestamps") @Valid @NotNull(message = "externalTimestamps " + DtoErrorMessages.NOT_NULL_ERROR) TimestampsDto externalTimestamps,
                             @NotNull(message = "transactionType " + DtoErrorMessages.NOT_NULL_ERROR) TransactionTypeDto transactionType,
                             @Valid ShippingFeeDto shippingFee,
                             @Schema(description = FieldsDescriptions.CREATED_FROM) @Size(max = 256, message = "createdFrom " + StringErrorMessages.MINMAX_256_ERROR) String createdFrom,
                             @Schema(description = FieldsDescriptions.TAXABLE_ITEMS_AMOUNT) float taxableItemsAmount,
                             @Schema(description = FieldsDescriptions.TANGIBLE_ITEMS_AMOUNT) float tangibleItemsAmount,
                             float totalItemsAmount)
        implements SourceCheckable, ExternalIdCheckable {
}