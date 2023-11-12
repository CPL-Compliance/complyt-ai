package com.complyt.v1.models.nexus;

import java.math.BigDecimal;

public record NexusCalculationSummaryDto(long count, BigDecimal amount) {
    @Override
    public BigDecimal amount() {
        return amount != null ? amount : BigDecimal.ZERO;
    }

}