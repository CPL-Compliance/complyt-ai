package com.complyt.domain.customer.exemption;

import com.complyt.domain.State;
import com.complyt.domain.fields.ComplytIdFieldDomain;
import com.complyt.domain.timestamps.Timestamps;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Builder
@Document(collection = "exemption")
public class Exemption implements ComplytIdFieldDomain {

    private final UUID complytId;
    @Id
    private final String id;
    private final String tenantId;
    private final UUID customerId;
    private final State state;
    private final Classification classification;
    private final ValidationDates validationDates;
    private final Timestamps internalTimestamps;
    private final Status status;
    private final Certificate certificate;
    private final ExemptionType exemptionType;
}