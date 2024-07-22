package com.complyt.domain.nexus;

import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class NexusCalculationSummary {
    private long count;
    private BigDecimal amount;

    public BigDecimal amount() {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    @Data
    @Accessors(chain = true)
    public static class Builder {
        private long count = 0;
        private BigDecimal amount = BigDecimal.ZERO;

        public NexusCalculationSummary build() {
            return new NexusCalculationSummary(count, amount);
        }
    }
}