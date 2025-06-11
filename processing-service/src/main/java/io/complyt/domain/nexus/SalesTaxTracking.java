package io.complyt.domain.nexus;

import io.complyt.domain.ClientTracking;
import io.complyt.domain.FilingFrequency;
import io.complyt.domain.State;
import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.domain.sales_tax.RegisteredType;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Accessors(chain = true)
public class SalesTaxTracking implements ComplytIdProperty {
    UUID complytId;
    
    String id;
    String country;
    State state;
    String tenantId;
    String comment;
    boolean enforcesSalesTax;
    PhysicalNexusTracker physicalNexusTracker;
    EconomicNexusTracker economicNexusTracker;
    NexusStateRule nexusStateRule;
    ClientTracking clientTracking;
    Map<LocalDate, NexusCalculationSummary> nexusCalculationSummaries;
    Map<UUID, TransactionNexusSummary> transactionNexusSummaries;
    LocalDateTime appliedDate;
    boolean approved;
    LocalDateTime approvalDate;
    FilingFrequency filingFrequency;
    RegisteredType registered;
    LocalDateTime registrationDate;
    String subsidiary;
    String establishedBy;

}