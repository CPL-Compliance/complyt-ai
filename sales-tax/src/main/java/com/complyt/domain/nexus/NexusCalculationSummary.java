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
}
