package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Schema(name = "SalesTaxTracking")
public class SalesTaxTrackingDto {

    private StateDto state;
    private final String tenantId;
    private boolean enforcesSalesTax;
    private PhysicalNexusTrackerDto physicalNexusTracker;
    private EconomicNexusTrackerDto economicNexusTracker;
    private LocalDateTime appliedDate;
    private boolean approved;
    private LocalDateTime approvalDate;

}
