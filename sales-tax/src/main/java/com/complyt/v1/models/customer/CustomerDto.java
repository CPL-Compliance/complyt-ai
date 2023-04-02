package com.complyt.v1.models.customer;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.OptionalAddressDto;
import com.complyt.v1.models.checkables.ExternalIdCheckable;
import com.complyt.v1.models.checkables.SourceCheckable;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.With;

import java.util.UUID;

@With
@Schema(name = "Customer", description = FieldsDescriptions.CUSTOMER)
public record CustomerDto(@Schema(description = FieldsDescriptions.COMPLYT_ID + "customer") UUID complytId,
                          @Schema(description = FieldsDescriptions.EXTERNAL_ID) @NotNull(message = "externalId " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "externalId " + StringErrorMessages.MINMAX_256_ERROR) String externalId,
                          @Schema(description = FieldsDescriptions.SOURCE) @NotNull(message = "source " + DtoErrorMessages.NOT_NULL_ERROR) @Pattern(regexp = "[1-9]", message = "source " + StringErrorMessages.SINGLE_DIGIT_ERROR) String source,
                          @Schema(description = FieldsDescriptions.NAME_OF_CUSTOMER) @NotNull(message = "name " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "name " + StringErrorMessages.MINMAX_256_ERROR) String name,
                          @Schema(ref = "addressOfCustomer") @Valid OptionalAddressDto address,
                          @NotNull(message = "customerType " + DtoErrorMessages.NOT_NULL_ERROR) CustomerTypeDto customerType,
                          @Schema(ref = "internalTimestamps") @Valid TimestampsDto internalTimestamps,
                          @Schema(ref = "externalTimestamps") @Valid @NotNull(message = "externalTimestamps " + DtoErrorMessages.NOT_NULL_ERROR) TimestampsDto externalTimestamps) implements SourceCheckable, ExternalIdCheckable {
}