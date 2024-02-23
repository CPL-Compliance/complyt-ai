package com.complyt.v1.models;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.regex.ISO8601Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.With;

@Schema(name = "EconomicNexusTracker")
@With
public record EconomicNexusTrackerDto(
        boolean established,
        @NotNull(message = "EconomicNexusTracker.establishedDate " + DtoErrorMessages.NOT_NULL_ERROR)
        @Schema(ref = "timestamp")
        @Pattern(regexp = ISO8601Regex.expression, message = "establishedDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR)
        String establishedDate) {

}
