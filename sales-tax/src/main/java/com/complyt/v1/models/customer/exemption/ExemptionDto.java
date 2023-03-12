package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.checkables.ComplytIdCheckable;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.With;

import java.util.UUID;

@With
@Schema(name = "Exemption")
public record ExemptionDto(UUID complytId, @NotNull(message = "Customer Id may not be null") UUID customerId,
                           @Valid @NotNull(message = "State may not be null") StateDto state,
                           @Valid @NotNull(message = "Classification may not be null") ClassificationDto classification,
                           @Valid @NotNull(message = "Validation Dates may not be null") ValidationDatesDto validationDates,
                           @Valid TimestampsDto internalTimestamps,
                           @Valid @NotNull(message = "Status may not be null") StatusDto status,
                           @Valid @NotNull(message = "Certificate may not be null") CertificateDto certificate,
                           @NotNull(message = "Exemption Type may not be null") ExemptionTypeDto exemptionType) implements ComplytIdCheckable {
}
