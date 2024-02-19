package com.complyt.v1.models;

import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.checkables.TenantIdCheckable;
import com.complyt.v1.models.nexus.NexusDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
public record ClientTrackingDtoTenant(
        @Valid @NotNull NexusDto nexus,
        @NotNull @Size(max = 256, message = "ClientTracking.name " + StringErrorMessages.MAX_256_ERROR) String name,
        @Valid TimestampsDto internalTimestamps,
        @NotNull @Size(max = 35, message = "tenantId " + StringErrorMessages.MAX_30_ERROR) String tenantId)

        implements TenantIdCheckable {
}
