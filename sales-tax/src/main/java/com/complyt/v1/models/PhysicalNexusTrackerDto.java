package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.With;

import java.time.LocalDateTime;

@With
@Schema(name = "PhysicalNexusTracker")
public record PhysicalNexusTrackerDto(boolean established,
                                      @NotNull(message = "PhysicalNexusTracker.establishedDate" + DtoErrorMessages.not_null_error) LocalDateTime establishedDate) {

}
