package com.complyt.v1.models;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(name = "State")
public record StateDto(
        @NotNull(message = "State.abbreviation" + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "State.abbreviation" + StringErrorMessages.MINMAX_256_ERROR) String abbreviation,
        @NotNull(message = "State.code" + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "State.code" + StringErrorMessages.MINMAX_256_ERROR) String code,
        @NotNull(message = "State.name" + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "State.name" + StringErrorMessages.MINMAX_256_ERROR) String name) {
}