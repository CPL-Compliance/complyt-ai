package com.complyt.v1.models.customer.exemption;

import com.complyt.utils.regex.ISO8601Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@With
@Schema(name = "validationDates")
public record ValidationDatesDto(
        @Valid @NotBlank(message = "From date may not be blank") @Pattern(regexp = ISO8601Regex.expression, message = "From date is in illegal format - For date/time fields, please provide a valid ISO8601 format. Supported formats are 'YYYY-MM-DD', 'YYYY-MM-DDTHH:mm:ssZ', and 'YYYY-MM-DDTHH:mm:ss±hh:mm' (with a valid time zone offset).") @NotNull(message = "From Date timestamps may not be null") String fromDate,
        @Valid @NotBlank(message = "To date may not be blank") @Pattern(regexp = ISO8601Regex.expression, message = "To date is in illegal format - For date/time fields, please provide a valid ISO8601 format. Supported formats are 'YYYY-MM-DD', 'YYYY-MM-DDTHH:mm:ssZ', and 'YYYY-MM-DDTHH:mm:ss±hh:mm' (with a valid time zone offset).") @NotNull(message = "To Date timestamps may not be null") String toDate) {

}
