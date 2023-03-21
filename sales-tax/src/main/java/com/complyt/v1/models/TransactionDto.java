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
@Schema(name = "transaction", description = FieldsDescriptions.transaction)
public record TransactionDto(@Schema(description = FieldsDescriptions.complyt_id + "transaction") UUID complytId,
                             @Schema(description = FieldsDescriptions.external_id) @NotNull(message = "externalId" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "externalId" + StringErrorMessages.minmax_256_error) String externalId,
                             @Schema(description = FieldsDescriptions.source) @NotNull(message = "source" + DtoErrorMessages.not_null_error) @Pattern(regexp = "[1-9]", message = "source" + StringErrorMessages.single_digit_error) String source,
                             @ArraySchema(schema = @Schema(description = FieldsDescriptions.item)) @NotEmpty(message = "items" + DtoErrorMessages.list_not_empty_error) @NotNull(message = "items" + DtoErrorMessages.not_null_error) List<@Valid ItemDto> items,
                             @Schema(ref = "billingAddress") @Valid OptionalAddressDto billingAddress,
                             @Schema(ref = "shippingAddress") @Valid @NotNull(message = "shippingAddress" + DtoErrorMessages.not_null_error) MandatoryAddressDto shippingAddress,
                             @Schema(description = FieldsDescriptions.customer_id + "transaction") @NotNull(message = "customerId" + DtoErrorMessages.not_null_error) UUID customerId,
                             @Valid CustomerDto customer,
                             @Valid SalesTaxDto salesTax,
                             @Schema(description = FieldsDescriptions.transaction_status) @NotNull(message = "transactionStatus" + DtoErrorMessages.not_null_error) TransactionStatusDto transactionStatus,
                             @Schema(ref = "internalTimestamps") @Valid TimestampsDto internalTimestamps,
                             @Schema(ref = "externalTimestamps") @Valid @NotNull(message = "externalTimestamps" + DtoErrorMessages.not_null_error) TimestampsDto externalTimestamps,
                             @NotNull(message = "transactionType" + DtoErrorMessages.not_null_error) TransactionTypeDto transactionType,
                             @Valid ShippingFeeDto shippingFee,
                             @Schema(description = FieldsDescriptions.created_from) @Size(min = 1, max = 256, message = "createdFrom" + StringErrorMessages.minmax_256_error) String createdFrom,
                             @Schema(description = FieldsDescriptions.taxable_items_amount) float taxableItemsAmount,
                             @Schema(description = FieldsDescriptions.tangible_items_amount) float tangibleItemsAmount,
                             float totalItemsAmount)
        implements SourceCheckable, ExternalIdCheckable {
}