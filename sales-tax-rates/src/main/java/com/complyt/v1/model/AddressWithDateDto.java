package com.complyt.v1.model;

import com.complyt.utils.ISO8601Regex;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.With;

@With
public record AddressWithDateDto(
        @Valid AddressDto address,
        @Schema(ref = "effectiveDate")
        @Valid @NotBlank(message = "effectiveDate " + DtoErrorMessages.NOT_BLANK_ERROR)
        @Pattern(regexp = ISO8601Regex.expression, message = "effectiveDate " + DtoErrorMessages.DATE_FORMAT_ERROR) String effectiveDate
        ) {
}
