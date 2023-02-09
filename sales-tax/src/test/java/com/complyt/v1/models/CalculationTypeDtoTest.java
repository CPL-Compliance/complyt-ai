package com.complyt.v1.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculationTypeDtoTest {

    @Test
    public void CalculationTypeDto_getFixed_ReturnsFixed() {
        // Given + When
        CalculationTypeDto calculationTypeDto = CalculationTypeDto.FIXED;

        // Then
        assertEquals(CalculationTypeDto.valueOf("FIXED"), calculationTypeDto);
    }

    @Test
    public void CalculationTypeDto_getPercentage_ReturnsPercentage() {
        // Given + When
        CalculationTypeDto calculationTypeDto = CalculationTypeDto.PERCENTAGE;

        // Then
        assertEquals(CalculationTypeDto.valueOf("PERCENTAGE"), calculationTypeDto);
    }
}
