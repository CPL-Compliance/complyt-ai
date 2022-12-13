package com.complyt.domain.nexus;

import com.complyt.domain.State;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Document(collection = "sales_tax_tracking")
public class SalesTaxTracking {
    private final String tenantId;
    @Id
    private String id;
    private State state;
    private boolean enforcesSalesTax;
    private PhysicalNexusTracker physicalNexusTracker;
    private EconomicNexusTracker economicNexusTracker;
    private LocalDateTime appliedDate;
    private boolean isApproved;
    private LocalDateTime approvalDate;
}
