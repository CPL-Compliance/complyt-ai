package com.complyt.v1.models.nexus;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NexusCalculationSummaryDtoTest {

    @Test
    void amount_NullAmount_ReturnsZero() {
        // Given
        NexusCalculationSummaryDto nexusCalculationSummaryDto = new NexusCalculationSummaryDto(0, null);

        // Then
        assertEquals(BigDecimal.ZERO, nexusCalculationSummaryDto.amount());
    }

}