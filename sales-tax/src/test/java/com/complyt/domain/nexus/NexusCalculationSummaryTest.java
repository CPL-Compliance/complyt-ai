package com.complyt.domain.nexus;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class NexusCalculationSummaryTest {

    private NexusCalculationSummary nexusCalculationSummary;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

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
        String expectedString = "NexusCalculationSummary(count=" + nexusCalculationSummary.getCount() +
                ", amount=" + nexusCalculationSummary.amount() + ")";

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
        NexusCalculationSummary nexusCalculationSummaryWithNullManualTaxRate = nexusCalculationSummary.setAmount(null);

        // When
        BigDecimal actualTotalPrice = nexusCalculationSummaryWithNullManualTaxRate.amount();

        // Then
        assertEquals(BigDecimal.ZERO, actualTotalPrice);
    }

    @Test
    void getAmount_AmountIs10_ReturnsBigDecimalOf10() {
        // Given
        NexusCalculationSummary nexusCalculationSummaryWithManualTaxRateOf10 = nexusCalculationSummary.setAmount(new BigDecimal("10"));

        // When
        BigDecimal actualTotalPrice = nexusCalculationSummaryWithManualTaxRateOf10.amount();

        // Then
        assertEquals(new BigDecimal("10"), actualTotalPrice);
    }

}