package com.complyt.domain.nexus;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class NexusCalculationSummary {
    private long count;
    private BigDecimal amount;

    public final BigDecimal getAmount() {
        return amount != null ? amount : BigDecimal.ZERO;
    }
}
