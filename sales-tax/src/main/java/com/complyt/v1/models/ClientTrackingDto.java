package com.complyt.v1.models;

import com.complyt.domain.Nexus;
import com.complyt.v1.models.nexus.NexusDto;
import lombok.With;

@With
public record ClientTrackingDto(
        NexusDto nexus,
        String name
) {
}
