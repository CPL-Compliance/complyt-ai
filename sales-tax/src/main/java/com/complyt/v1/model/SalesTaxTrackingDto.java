package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ToString
@Schema(name = "SalesTaxTracking")
public class SalesTaxTrackingDto {

    String id;
    StateDto state;
    boolean enforcesSalesTax;
    PhysicalNexusTrackerDto physicalNexusTracker;
    EconomicNexusTrackerDto economicNexusTracker;
    LocalDateTime appliedDate;
    boolean approved;
    LocalDateTime approvalDate;
}
