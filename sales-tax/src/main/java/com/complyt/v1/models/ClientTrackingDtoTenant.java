package com.complyt.v1.models;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.config.regex.ISO8601Regex;
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
        @Valid @NotNull(message = "ClientTrackingDto.nexus " + DtoErrorMessages.NOT_NULL_ERROR) NexusDto nexus,
        @NotNull(message = "ClientTrackingDto.name " + DtoErrorMessages.NOT_NULL_ERROR) @Size(max = 256, message = "ClientTracking.name " + StringErrorMessages.MAX_256_ERROR) String name,
        @Valid TimestampsDto internalTimestamps,
        @NotNull(message = "ClientTrackingDto.tenantId " + DtoErrorMessages.NOT_NULL_ERROR) @Size(max = 35, message = "tenantId " + StringErrorMessages.MAX_30_ERROR) String tenantId)
        implements TenantIdCheckable {
}
