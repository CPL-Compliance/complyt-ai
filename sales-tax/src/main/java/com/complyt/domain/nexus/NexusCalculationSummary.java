package com.complyt.domain.nexus;

import lombok.With;

import java.math.BigDecimal;

@With
public record NexusCalculationSummary(long count, BigDecimal amount) {

    @Override
    public BigDecimal amount() {
        return amount != null ? amount : BigDecimal.ZERO;
    }

}