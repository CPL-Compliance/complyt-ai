package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.checkables.StateCheckable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@With
@Schema(name = "SalesTaxTracking", description = FieldsDescriptions.salesTaxTracking)
public record SalesTaxTrackingDto(@Schema(description = FieldsDescriptions.complyt_id + "salesTaxTracking") UUID complytId,
                                  @Valid @NotNull(message = "state" + DtoErrorMessages.not_null_error) StateDto state,
                                  boolean enforcesSalesTax,
                                  @Valid @NotNull(message = "physicalNexusTracker" + DtoErrorMessages.not_null_error) PhysicalNexusTrackerDto physicalNexusTracker,
                                  @Valid @NotNull(message = "economicNexusTracker" + DtoErrorMessages.not_null_error) EconomicNexusTrackerDto economicNexusTracker,
                                  LocalDateTime appliedDate, boolean approved, LocalDateTime approvalDate)
        implements StateCheckable {
}
