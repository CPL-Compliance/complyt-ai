package com.complyt.v1.models.timestamps;

import com.complyt.utils.regex.ISO8601Regex;
import com.complyt.v1.error_messages.DateErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.With;


@With
@Schema(name = "Timestamps")
public record TimestampsDto(@Valid @NotBlank(message = "createdDate may not be blank") @Pattern(regexp = ISO8601Regex.expression, message = "created" + DateErrorMessages.wrong_format_error_message) @NotNull(message = "createdDate may not be null") String createdDate,
                            @Valid @NotBlank(message = "updatedDate may not be blank") @Pattern(regexp = ISO8601Regex.expression, message = "updated" + DateErrorMessages.wrong_format_error_message) @NotNull(message = "updatedDate may not be null") String updatedDate) {

}
