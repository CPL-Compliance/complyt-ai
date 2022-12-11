package com.complyt.domain.customer.exemption;

import com.complyt.domain.State;
import com.complyt.domain.TimeStamps;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Builder
@Document(collection = "exemption")
public class Exemption {
    @Id
    private final String id;
    private final String tenantId;
    private final ObjectId customerId;
    private final State state;
    private final Classification classification;
    private final ValidationDates validationDates;
    private final TimeStamps internalTimeStamps;
    private final Status status;
    private final Certificate certificate;
    private final ExemptionType exemptionType;
}