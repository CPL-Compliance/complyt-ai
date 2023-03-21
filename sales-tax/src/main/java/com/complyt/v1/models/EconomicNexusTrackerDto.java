package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(name = "EconomicNexusTracker")
public record EconomicNexusTrackerDto(
        @NotNull(message = "EconomicNexusTracker.established" + DtoErrorMessages.not_null_error) boolean established,
        @NotNull(message = "EconomicNexusTracker.establishedDate" + DtoErrorMessages.not_null_error) LocalDateTime establishedDate) {

}
