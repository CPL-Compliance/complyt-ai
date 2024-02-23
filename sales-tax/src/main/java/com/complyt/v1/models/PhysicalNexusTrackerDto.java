package com.complyt.v1.models;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.regex.ISO8601Regex;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.With;

@With
@Schema(name = "PhysicalNexusTracker")
public record PhysicalNexusTrackerDto(
        boolean established,
        @NotNull(message = "PhysicalNexusTracker.establishedDate " + DtoErrorMessages.NOT_NULL_ERROR)
        @Schema(ref = "timestamp")
        @Pattern(regexp = ISO8601Regex.expression, message = "establishedDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR)
        String establishedDate) {

}
