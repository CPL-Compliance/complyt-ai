package com.complyt.v1.models;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.nexus.NexusDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
public record ClientTrackingDto (
    @Valid @NotNull(message = "ClientTracking.nexus " + DtoErrorMessages.NOT_NULL_ERROR) NexusDto nexus,
    @NotNull(message = "ClientTracking.name " + DtoErrorMessages.NOT_NULL_ERROR) @Size(max = 256, message = "ClientTracking.name " + StringErrorMessages.MAX_256_ERROR) String name,

    @Valid TimestampsDto internalTimestamps)
{
}


