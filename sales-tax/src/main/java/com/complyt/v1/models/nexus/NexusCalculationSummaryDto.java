package com.complyt.v1.models.nexus;

import java.math.BigDecimal;

<<<<<<< HEAD
public record NexusCalculationSummaryDto(long count, BigDecimal amount) {
=======
public record NexusCalculationSummaryDto(long count, BigDecimal amount, DefinitionDto definition) {
>>>>>>> 91047832 (added summaryDto and mapper)
    @Override
    public BigDecimal amount() {
        return amount != null ? amount : BigDecimal.ZERO;
    }

}