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
@Schema(name = "Customer", description = FieldsDescriptions.customer)
public record CustomerDto(@Schema(description = FieldsDescriptions.complyt_id + "customer") UUID complytId,
                          @Schema(description = FieldsDescriptions.external_id) @NotNull(message = "externalId" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "externalId" + StringErrorMessages.minmax_256_error) String externalId,
                          @Schema(description = FieldsDescriptions.source) @NotNull(message = "source" + DtoErrorMessages.not_null_error) @Pattern(regexp = "[1-9]", message = "source" + StringErrorMessages.single_digit_error) String source,
                          @NotNull(message = "name" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "name" + StringErrorMessages.minmax_256_error) String name,
                          @Schema(ref = "addressOfCustomer") @Valid OptionalAddressDto address,
                          @NotNull(message = "customerType" + DtoErrorMessages.not_null_error) CustomerTypeDto customerType,
                          @Schema(ref = "internalTimestamps") @Valid TimestampsDto internalTimestamps,
                          @Schema(ref = "externalTimestamps") @Valid @NotNull(message = "externalTimestamps" + DtoErrorMessages.not_null_error) TimestampsDto externalTimestamps) implements SourceCheckable, ExternalIdCheckable {
}