package com.complyt.domain.nexus;

import com.complyt.domain.nexus.enums.Definition;

import java.math.BigDecimal;

public record NexusCalculationSummary(long count, BigDecimal amount, Definition definition) {

    @Override
    public BigDecimal amount() {
        return amount != null ? amount : BigDecimal.ZERO;
    }

}