package com.complyt.domain.sales_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SalesTaxTest {
    private SalesTax salesTax;
    private UnitTestUtilities testUtilities;

    private SalesTaxRates createSalesTaxRates() {
        return new SalesTaxRates(new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), null);
    }

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTax = testUtilities.createSalesTaxWithAllFields();
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "SalesTax[amount=" + salesTax.amount() +
                ", rate=" + salesTax.rate()  +
                ", salesTaxRates=" + salesTax.salesTaxRates() +
                ", gtRates=" + salesTax.gtRates()
                + "]";

        // When
        String actualString = salesTax.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTax_ReturnsTrue() {
        // Given
        SalesTax givenSalesTax = testUtilities.createSalesTaxWithAllFields().withAmount(BigDecimal.ZERO);

        // When
        boolean isEquals = salesTax.equals(givenSalesTax);

        // Then
        assertTrue(isEquals);
    }

}
