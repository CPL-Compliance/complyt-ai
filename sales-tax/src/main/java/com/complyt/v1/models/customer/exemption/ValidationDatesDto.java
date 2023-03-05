package com.complyt.v1.models.customer.exemption;

import com.complyt.utils.regex.ISO8601Regex;
import com.complyt.v1.error_messages.DateErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@With
@Schema(name = "validationDates")
public record ValidationDatesDto(
        @Valid @NotBlank(message = "From date may not be blank") @Pattern(regexp = ISO8601Regex.expression, message = "From " + DateErrorMessages.wrong_format_error_message) @NotNull(message = "From date may not be null") String fromDate,
        @Valid @NotBlank(message = "To date may not be blank") @Pattern(regexp = ISO8601Regex.expression, message = "To " + DateErrorMessages.wrong_format_error_message) @NotNull(message = "To date may not be null") String toDate) {

}
