package com.complyt.domain.nexus;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.FilingFrequency;
import com.complyt.domain.State;
import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.domain.sales_tax.RegisteredType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
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
    Subsidiary subsidiary;
}
