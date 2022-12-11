package com.complyt.domain.sales_tax.product_classification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculationTypeTest {

    @Test
    public void CalculationType_getFixed_ReturnsFixed() {
        // Given + When
        CalculationType calculationType = CalculationType.FIXED;

        // Then
        assertEquals(CalculationType.valueOf("FIXED"), calculationType);
    }

    @Test
    public void CalculationType_getPercentage_ReturnsPercentage() {
        // Given + When
        CalculationType calculationType = CalculationType.PERCENTAGE;

        // Then
        assertEquals(CalculationType.valueOf("PERCENTAGE"), calculationType);
    }

}