package com.complyt.v1.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculationTypeDtoTest {

    @Test
    public void CalculationTypeDto_getFixed_gotFixed() {
        CalculationTypeDto calculationTypeDto = CalculationTypeDto.FIXED;
        assertEquals(CalculationTypeDto.FIXED, calculationTypeDto);
    }

    @Test
    public void CalculationTypeDto_getPercentage_gotPercentage() {
        CalculationTypeDto calculationTypeDto = CalculationTypeDto.PERCENTAGE;
        assertEquals(CalculationTypeDto.PERCENTAGE, calculationTypeDto);
    }
}
