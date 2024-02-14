package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.checkables.ComplytIdCheckable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;

import java.util.UUID;

@With
@Builder
@Schema(name = "Exemption", description = FieldsDescriptions.EXEMPTION)
public record ExemptionDto(@Schema(description = FieldsDescriptions.COMPLYT_ID + "exemption") UUID complytId,
                           @Schema(description = FieldsDescriptions.CUSTOMER_ID + "exemption") @NotNull(message = "customerId " + DtoErrorMessages.NOT_NULL_ERROR) UUID customerId,
                           @Valid @NotNull(message = "state " + DtoErrorMessages.NOT_NULL_ERROR) StateDto state,
                           @Valid @NotNull(message = "classification " + DtoErrorMessages.NOT_NULL_ERROR) ClassificationDto classification,
                           @Valid @NotNull(message = "validationDates " + DtoErrorMessages.NOT_NULL_ERROR) ValidationDatesDto validationDates,
                           @Schema(ref = "internalTimestamps") @Valid TimestampsDto internalTimestamps,
                           @Valid @NotNull(message = "status " + DtoErrorMessages.NOT_NULL_ERROR) StatusDto status,
                           @Valid @NotNull(message = "certificate " + DtoErrorMessages.NOT_NULL_ERROR) CertificateDto certificate,
                           @NotNull(message = "exemptionType " + DtoErrorMessages.NOT_NULL_ERROR) ExemptionTypeDto exemptionType,
                           @NotNull(message = "exemptionStatus " + DtoErrorMessages.NOT_NULL_ERROR) ExemptionStatusDto exemptionStatus) implements ComplytIdCheckable {
}