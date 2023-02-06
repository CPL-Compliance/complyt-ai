package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@With
@Schema(name = "EconomicNexusTracker")
public record EconomicNexusTrackerDto(@NotNull(message = "Established may not be null") boolean established,
                                      @NotNull(message = "EstablishedDate may not be null") LocalDateTime establishedDate) {

}
