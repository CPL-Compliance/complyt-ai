package com.complyt.domain.nexus;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class NexusCalculationSummary {
    private long count;
    private double amount;
}
