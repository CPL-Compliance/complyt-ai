package io.complyt.domain.customer.exemption;

import io.complyt.domain.State;
import io.complyt.domain.customer.Customer;
import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.domain.properties.InternalTimestampsProperty;
import io.complyt.domain.timestamps.Timestamps;
import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Builder
public class Exemption implements ComplytIdProperty, InternalTimestampsProperty {

    private final UUID complytId;
    
    private final String id;
    private final String tenantId;
    private final UUID customerId;
    private final String country;
    private final State state;
    private final Classification classification;
    private final ValidationDates validationDates;
    private final Timestamps internalTimestamps;
    private final Status status;
    private final Certificate certificate;
    private final ExemptionType exemptionType;
    private final ExemptionStatus exemptionStatus;
    private final Customer customer;
}