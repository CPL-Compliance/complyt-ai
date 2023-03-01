package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@With
@Schema(name = "PhysicalNexusTracker")
public record PhysicalNexusTrackerDto(boolean established,
                                      @NotNull(message = "Established Date may not be null") LocalDateTime establishedDate) {

}
