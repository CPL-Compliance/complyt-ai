package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ToString
@With
@Schema(name = "SalesTaxTracking")
public class SalesTaxTrackingDto {

    UUID complytId;
    StateDto state;
    boolean enforcesSalesTax;
    PhysicalNexusTrackerDto physicalNexusTracker;
    EconomicNexusTrackerDto economicNexusTracker;
    LocalDateTime appliedDate;
    boolean approved;
    LocalDateTime approvalDate;
}
