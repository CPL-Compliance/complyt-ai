package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.config.regex.ISO8601Regex;
import com.complyt.v1.config.regex.TenantIdRegex;
import com.complyt.v1.models.checkables.TenantIdCheckable;
import com.complyt.v1.models.nexus.NexusDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
public record ClientTrackingDtoTenant(
        @Valid @NotNull NexusDto nexus,
        @NotNull @Size(max = 256, message = "ClientTracking.name " + StringErrorMessages.MAX_256_ERROR) String name,
        @Valid TimestampsDto internalTimestamps,
        @NotNull @Pattern(regexp = TenantIdRegex.expression, message = DtoErrorMessages.TENANT_ID_FORMAT) @Size(max = 50, message = "tenantId " + StringErrorMessages.MAX_50_ERROR) String tenantId)

        implements TenantIdCheckable {
}
