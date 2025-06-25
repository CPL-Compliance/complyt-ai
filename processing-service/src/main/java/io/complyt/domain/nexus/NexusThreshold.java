package io.complyt.domain.nexus;

import io.complyt.domain.nexus.enums.Definition;

import java.math.BigDecimal;

public record NexusThreshold(
        BigDecimal amount,
        int count,
        Definition definition
) {
}
