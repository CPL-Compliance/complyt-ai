package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.checkables.ComplytIdCheckable;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

import java.util.UUID;

@With
@Schema(name = "Exemption", description = FieldsDescriptions.exemption)
public record ExemptionDto(@Schema(description = FieldsDescriptions.complyt_id + "exemption") UUID complytId,
                           @Schema(description = FieldsDescriptions.customer_id + "exemption") @NotNull(message = "customerId" + DtoErrorMessages.not_null_error) UUID customerId,
                           @Valid @NotNull(message = "state" + DtoErrorMessages.not_null_error) StateDto state,
                           @Valid @NotNull(message = "classification" + DtoErrorMessages.not_null_error) ClassificationDto classification,
                           @Valid @NotNull(message = "validationDates" + DtoErrorMessages.not_null_error) ValidationDatesDto validationDates,
                           @Schema(ref = "internalTimestamps") @Valid TimestampsDto internalTimestamps,
                           @Valid @NotNull(message = "status" + DtoErrorMessages.not_null_error) StatusDto status,
                           @Valid @NotNull(message = "certificate" + DtoErrorMessages.not_null_error) CertificateDto certificate,
                           @NotNull(message = "exemptionType" + DtoErrorMessages.not_null_error) ExemptionTypeDto exemptionType) implements ComplytIdCheckable {
}
