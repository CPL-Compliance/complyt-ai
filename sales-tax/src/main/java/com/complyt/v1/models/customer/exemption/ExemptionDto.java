package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Schema(name = "Exemption")
public class ExemptionDto {
    private final UUID complytId;

    @NotNull(message = "Customer Id may not be null")
    private final UUID customerId;

    @Valid
    @NotNull(message = "State may not be null")
    private final StateDto state;

    @Valid
    @NotNull(message = "Classification may not be null")
    private final ClassificationDto classification;

    @Valid
//    @NotNull(message = "Validation Dates may not be null")
    private final ValidationDatesDto validationDates;

    @Valid
    private final TimestampsDto internalTimestamps;

    @Valid
    @NotNull(message = "Status Dates may not be null")
    private final StatusDto status;

    @Valid
    @NotNull(message = "Certificate Dates may not be null")
    private final CertificateDto certificate;

    @NotNull(message = "Exemption Type Dates may not be null")
    private final ExemptionTypeDto exemptionType;
}
