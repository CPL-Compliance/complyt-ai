package com.complyt.domain.nexus;

import lombok.Getter;
import lombok.With;

import java.math.BigDecimal;

@With
public record NexusCalculationSummary(long count, BigDecimal amount) {

    @Getter
    public static class Builder {
        private long count = 0;
        private BigDecimal amount = BigDecimal.ZERO;

        public NexusCalculationSummary.Builder setCount(long count) {
            this.count = count;
            return this;
        }

        public NexusCalculationSummary.Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public NexusCalculationSummary build() {
            return new NexusCalculationSummary(count, amount);
        }

    }
}