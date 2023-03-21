package com.complyt.v1.models.timestamps;

import com.complyt.utils.regex.ISO8601Regex;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.With;


@With
public record TimestampsDto(
        @Valid @NotNull(message = "Timestamps.createdDate" + DtoErrorMessages.not_null_error) @Pattern(regexp = ISO8601Regex.expression, message = "Timestamps.createdDate" + DtoErrorMessages.date_format_error) @NotNull(message = "Timestamps.createdDate" + DtoErrorMessages.not_null_error) String createdDate,
        @Valid @NotNull(message = "Timestamps.updatedDate" + DtoErrorMessages.not_null_error) @Pattern(regexp = ISO8601Regex.expression, message = "Timestamps.updatedDate" + DtoErrorMessages.date_format_error) @NotNull(message = "Timestamps.updatedDate" + DtoErrorMessages.not_null_error) String updatedDate) {

}
