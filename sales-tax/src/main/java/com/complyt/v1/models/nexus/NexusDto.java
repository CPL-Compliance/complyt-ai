package com.complyt.v1.models.nexus;

import lombok.With;

import java.time.LocalDateTime;

@With
public record NexusDto(
        LocalDateTime taxableDate
) {
}

