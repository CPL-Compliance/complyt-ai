package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@With
@Schema(name = "SalesTaxTracking")
public record SalesTaxTrackingDto(UUID complytId,
                                  @Valid @NotNull(message = "State address may not be null") StateDto state,
                                  @NotNull(message = "enforcesSalesTax address may not be null") boolean enforcesSalesTax,
                                  @Valid @NotNull(message = "PhysicalNexusTracker address may not be null") PhysicalNexusTrackerDto physicalNexusTracker,
                                  @Valid @NotNull(message = "PhysicalNexusTracker address may not be null") EconomicNexusTrackerDto economicNexusTracker,
                                  LocalDateTime appliedDate, boolean approved, LocalDateTime approvalDate) {

}
