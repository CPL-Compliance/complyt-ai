package com.complyt.domain.nexus;

import com.complyt.domain.nexus.enums.Definition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NexusCalculationSummaryTest {

    private NexusCalculationSummary nexusCalculationSummary;

    @BeforeEach
    void setup() {
        nexusCalculationSummary = createNexusCalculationSummary();
    }

    private NexusCalculationSummary createNexusCalculationSummary() {
        return new NexusCalculationSummary(0L, BigDecimal.ZERO);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "NexusCalculationSummary[count=" + nexusCalculationSummary.count() +
                ", amount=" + nexusCalculationSummary.amount() + "]";

        // When
        String actualString = nexusCalculationSummary.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameNexusCalculationSummary_ReturnsTrue() {
        // Given
        NexusCalculationSummary givenNexusCalculationSummary = createNexusCalculationSummary();

        // When
        boolean isEquals = nexusCalculationSummary.equals(givenNexusCalculationSummary);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void getAmount_AmountIsNull_ReturnsZero() {
        // Given
        NexusCalculationSummary nexusCalculationSummaryWithNullManualTaxRate = nexusCalculationSummary.withAmount(null);

        // When
        BigDecimal actualTotalPrice = nexusCalculationSummaryWithNullManualTaxRate.amount();

        // Then
        assertEquals(BigDecimal.ZERO, actualTotalPrice);
    }

    @Test
    void getAmount_AmountIs10_ReturnsBigDecimalOf10() {
        // Given
        NexusCalculationSummary nexusCalculationSummaryWithManualTaxRateOf10 = nexusCalculationSummary.withAmount(new BigDecimal("10"));

        // When
        BigDecimal actualTotalPrice = nexusCalculationSummaryWithManualTaxRateOf10.amount();

        // Then
        assertEquals(new BigDecimal("10"), actualTotalPrice);
    }

}