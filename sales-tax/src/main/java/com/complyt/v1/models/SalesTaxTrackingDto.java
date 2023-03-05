package com.complyt.v1.models;

import com.complyt.v1.models.checkables.StateCheckable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@With
@Schema(name = "SalesTaxTracking")
public record SalesTaxTrackingDto(UUID complytId,
                                  @Valid @NotNull(message = "State may not be null") StateDto state,
                                  boolean enforcesSalesTax,
                                  @Valid @NotNull(message = "Physical Nexus Tracker may not be null") PhysicalNexusTrackerDto physicalNexusTracker,
                                  @Valid @NotNull(message = "Economic Nexus Tracker may not be null") EconomicNexusTrackerDto economicNexusTracker,
                                  LocalDateTime appliedDate, boolean approved, LocalDateTime approvalDate)
        implements StateCheckable {
}
