package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.config.regex.ISO8601Regex;
import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.With;

@With
@Schema(name = "validationDates", description = FieldsDescriptions.VALIDATION_DATES)
public record ValidationDatesDto(
        @Schema(ref = "timestamp") @NotNull(message = "ValidationDates.fromDate " + DtoErrorMessages.NOT_NULL_ERROR) @Pattern(regexp = ISO8601Regex.expression, message = "ValidationDates.fromDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR) String fromDate,
        @Schema(ref = "timestamp") @Pattern(regexp = ISO8601Regex.expression, message = "ValidationDates.toDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR) String toDate) {

}
