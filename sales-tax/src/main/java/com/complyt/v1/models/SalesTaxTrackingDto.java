package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.config.regex.ISO8601Regex;
import com.complyt.v1.models.checkables.StateCheckable;
import com.complyt.v1.models.nexus.NexusCalculationSummaryDto;
import com.complyt.v1.models.nexus.NexusStateRuleDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.With;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
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
        Map<LocalDate, @Valid NexusCalculationSummaryDto> nexusCalculationSummaries,
        @Valid NexusStateRuleDto nexusStateRule,
        @Valid ClientTrackingDto clientTracking,
        @Schema(ref = "timestamp") @Pattern(regexp = ISO8601Regex.expression, message = "appliedDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR) String appliedDate,
        boolean approved,
        @Schema(ref = "timestamp") @Pattern(regexp = ISO8601Regex.expression, message = "approvalDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR) String approvalDate,
        @Schema(description = FieldsDescriptions.FILING_FREQUENCY) FilingFrequencyDto filingFrequency)
        implements StateCheckable {
}