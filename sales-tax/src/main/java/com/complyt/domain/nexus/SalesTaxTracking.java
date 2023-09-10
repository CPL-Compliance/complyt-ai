package com.complyt.domain.nexus;

import com.complyt.domain.FilingFrequency;
import com.complyt.domain.State;
import com.complyt.domain.properties.ComplytIdProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Document(collection = "sales_tax_tracking")
public class SalesTaxTracking implements ComplytIdProperty {
    UUID complytId;
    @Id
    String id;
    State state;
    String tenantId;
    String comment;
    boolean enforcesSalesTax;
    PhysicalNexusTracker physicalNexusTracker;
    EconomicNexusTracker economicNexusTracker;
    LocalDateTime appliedDate;
    boolean approved;
    LocalDateTime approvalDate;
    FilingFrequency filingFrequency;
}
