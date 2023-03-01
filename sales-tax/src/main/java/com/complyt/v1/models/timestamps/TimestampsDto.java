package com.complyt.v1.models.timestamps;

import com.complyt.utils.regex.ISO8601Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;


@With
@Schema(name = "Timestamps")
public record TimestampsDto(@Valid @NotBlank(message = "Created date may not be blank") @Pattern(regexp = ISO8601Regex.expression, message = "Created date is in illegal format - For date/time fields, please provide a valid ISO8601 format. Supported formats are 'YYYY-MM-DD', 'YYYY-MM-DDTHH:mm:ssZ', and 'YYYY-MM-DDTHH:mm:ss±hh:mm' (with a valid time zone offset).") @NotNull(message = "Created date may not be null") String createdDate,
                            @Valid @NotBlank(message = "Updated date may not be blank") @Pattern(regexp = ISO8601Regex.expression, message = "Updated date is in illegal format - For date/time fields, please provide a valid ISO8601 format. Supported formats are 'YYYY-MM-DD', 'YYYY-MM-DDTHH:mm:ssZ', and 'YYYY-MM-DDTHH:mm:ss±hh:mm' (with a valid time zone offset).") @NotNull(message = "Updated date may not be null") String updatedDate) {

}
