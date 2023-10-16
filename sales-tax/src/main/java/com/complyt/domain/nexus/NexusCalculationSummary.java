package com.complyt.domain.nexus;

<<<<<<< HEAD
import lombok.Getter;
import lombok.With;

import java.math.BigDecimal;

@With
public record NexusCalculationSummary(long count, BigDecimal amount) {
=======
import com.complyt.domain.nexus.enums.Definition;

import java.math.BigDecimal;

public record NexusCalculationSummary(long count, BigDecimal amount, Definition definition) {

>>>>>>> 91047832 (added summaryDto and mapper)
    @Override
    public BigDecimal amount() {
        return amount != null ? amount : BigDecimal.ZERO;
    }

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