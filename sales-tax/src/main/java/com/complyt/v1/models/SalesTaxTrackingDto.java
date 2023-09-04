package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.checkables.StateCheckable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@With
@Schema(name = "SalesTaxTracking", description = FieldsDescriptions.SALES_TAX_TRACKING)
public record SalesTaxTrackingDto(
        @Schema(description = FieldsDescriptions.COMPLYT_ID + "salesTaxTracking") UUID complytId,
        @Valid @NotNull(message = "state " + DtoErrorMessages.NOT_NULL_ERROR) StateDto state,
        @Size(max = 200, message = "comment " + StringErrorMessages.MAX_200_ERROR) String comment,
        boolean enforcesSalesTax,
        @Valid @NotNull(message = "physicalNexusTracker " + DtoErrorMessages.NOT_NULL_ERROR) PhysicalNexusTrackerDto physicalNexusTracker,
        @Valid @NotNull(message = "economicNexusTracker " + DtoErrorMessages.NOT_NULL_ERROR) EconomicNexusTrackerDto economicNexusTracker,
        LocalDateTime appliedDate, boolean approved, LocalDateTime approvalDate,
        FillingFrequencyDto fillingFrequency)
        implements StateCheckable {
}