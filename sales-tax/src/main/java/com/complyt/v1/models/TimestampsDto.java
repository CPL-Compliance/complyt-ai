package com.complyt.v1.models;

import com.complyt.utils.regex.ISO8601Regex;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.With;


@With
public record TimestampsDto(
        @Schema(ref = "timestamp") @Valid @Pattern(regexp = ISO8601Regex.expression, message = "Timestamps.createdDate " + DtoErrorMessages.DATE_FORMAT_ERROR) @NotNull(message = "Timestamps.createdDate " + DtoErrorMessages.NOT_NULL_ERROR) String createdDate,
        @Schema(ref = "timestamp") @Valid @Pattern(regexp = ISO8601Regex.expression, message = "Timestamps.updatedDate " + DtoErrorMessages.DATE_FORMAT_ERROR) @NotNull(message = "Timestamps.updatedDate " + DtoErrorMessages.NOT_NULL_ERROR) String updatedDate) {

}