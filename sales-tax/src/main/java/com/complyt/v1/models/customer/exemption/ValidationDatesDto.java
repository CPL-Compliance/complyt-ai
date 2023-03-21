package com.complyt.v1.models.customer.exemption;

import com.complyt.utils.regex.ISO8601Regex;
import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.With;

@With
@Schema(name = "validationDates", description = FieldsDescriptions.validation_dates)
public record ValidationDatesDto(
        @Valid @NotNull(message = "ValidationDates.fromDate" + DtoErrorMessages.not_null_error) @Pattern(regexp = ISO8601Regex.expression, message = "ValidationDates.fromDate" + DtoErrorMessages.date_format_error) String fromDate,
        @Valid @NotNull(message = "ValidationDates.toDate" + DtoErrorMessages.not_null_error) @Pattern(regexp = ISO8601Regex.expression, message = "ValidationDates.toDate" + DtoErrorMessages.date_format_error)  String toDate) {

}
