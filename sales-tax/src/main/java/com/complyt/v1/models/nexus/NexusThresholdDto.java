package com.complyt.v1.models.nexus;

import lombok.With;

import java.math.BigDecimal;

@With
public record NexusThresholdDto(
        BigDecimal amount,
        int count,
        DefinitionDto definition
) {
}
