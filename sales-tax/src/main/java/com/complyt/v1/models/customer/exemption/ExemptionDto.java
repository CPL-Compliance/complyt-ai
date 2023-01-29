package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Schema(name = "Exemption")
public class ExemptionDto {
    private final UUID complytId;
    private final UUID customerId;
    private final StateDto state;
    private final ClassificationDto classification;
    private final ValidationDatesDto validationDates;
    private final TimestampsDto internalTimestamps;
    private final StatusDto status;
    private final CertificateDto certificate;
    private final ExemptionTypeDto exemptionType;
}
