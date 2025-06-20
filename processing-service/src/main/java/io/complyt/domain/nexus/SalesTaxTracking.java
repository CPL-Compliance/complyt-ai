package io.complyt.domain.nexus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.complyt.domain.ClientTracking;
import io.complyt.domain.FilingFrequency;
import io.complyt.domain.State;
import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.domain.sales_tax.RegisteredType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@With
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonCreator
    public SalesTaxTracking(
            @JsonProperty("complytId") UUID complytId,
            @JsonProperty("id") String id,
            @JsonProperty("country") String country,
            @JsonProperty("state") State state,
            @JsonProperty("tenantId") String tenantId,
            @JsonProperty("comment") String comment,
            @JsonProperty("enforcesSalesTax") boolean enforcesSalesTax,
            @JsonProperty("physicalNexusTracker") PhysicalNexusTracker physicalNexusTracker,
            @JsonProperty("economicNexusTracker") EconomicNexusTracker economicNexusTracker,
            @JsonProperty("nexusStateRule") NexusStateRule nexusStateRule,
            @JsonProperty("clientTracking") ClientTracking clientTracking,
            @JsonProperty("nexusCalculationSummaries") Map<LocalDate, NexusCalculationSummary> nexusCalculationSummaries,
            @JsonProperty("transactionNexusSummaries") Map<UUID, TransactionNexusSummary> transactionNexusSummaries,
            @JsonProperty("appliedDate") LocalDateTime appliedDate,
            @JsonProperty("approved") boolean approved,
            @JsonProperty("approvalDate") LocalDateTime approvalDate,
            @JsonProperty("filingFrequency") FilingFrequency filingFrequency,
            @JsonProperty("registered") RegisteredType registered,
            @JsonProperty("registrationDate") LocalDateTime registrationDate,
            @JsonProperty("subsidiary") String subsidiary,
            @JsonProperty("establishedBy") String establishedBy
    ) {
        this.complytId = complytId;
        this.id = id;
        this.country = country;
        this.state = state;
        this.tenantId = tenantId;
        this.comment = comment;
        this.enforcesSalesTax = enforcesSalesTax;
        this.physicalNexusTracker = physicalNexusTracker;
        this.economicNexusTracker = economicNexusTracker;
        this.nexusStateRule = nexusStateRule;
        this.clientTracking = clientTracking;
        this.nexusCalculationSummaries = nexusCalculationSummaries;
        this.transactionNexusSummaries = transactionNexusSummaries;
        this.appliedDate = appliedDate;
        this.approved = approved;
        this.approvalDate = approvalDate;
        this.filingFrequency = filingFrequency;
        this.registered = registered;
        this.registrationDate = registrationDate;
        this.subsidiary = subsidiary;
        this.establishedBy = establishedBy;
    }
}
