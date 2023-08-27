package com.complyt.domain.sales_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SalesTaxTest {
    private SalesTax salesTax;

    private SalesTaxRates createSalesTaxRates() {
        return new SalesTaxRates(new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), null);
    }

    @BeforeEach
    void setup() {
        salesTax = new SalesTax(new BigDecimal(1000), createSalesTaxRates());
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "SalesTax[amount=" + salesTax.amount() +
                ", salesTaxRates=" + salesTax.salesTaxRates() + "]";

        // When
        String actualString = salesTax.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTax_ReturnsTrue() {
        // Given
        SalesTaxRates salesTaxRates = createSalesTaxRates();
        SalesTax givenSalesTax = new SalesTax(new BigDecimal(1000), salesTaxRates);

        // When
        boolean isEquals = salesTax.equals(givenSalesTax);

        // Then
        assertTrue(isEquals);
    }

}
