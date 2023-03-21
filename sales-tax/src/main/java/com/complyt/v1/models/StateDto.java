package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "State")
public record StateDto(
        @NotNull(message = "State.abbreviation" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "State.abbreviation" + StringErrorMessages.minmax_256_error) String abbreviation,
        @NotNull(message = "State.code" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "State.code" + StringErrorMessages.minmax_256_error) String code,
        @NotNull(message = "State.name" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "State.name" + StringErrorMessages.minmax_256_error) String name) {
}