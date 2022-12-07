package com.complyt.domain.sales_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxTest {
    private SalesTax salesTax;

    private SalesTaxRate createSalesTaxRates() {
        return new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
    }

    @BeforeEach
    void setup() {
        salesTax = new SalesTax(1000, createSalesTaxRates());
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "SalesTax(amount=" + salesTax.getAmount() +
                ", salesTaxRate=" + salesTax.getSalesTaxRate() + ")";

        // When
        String actualString = salesTax.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTax_ReturnsTrue() {
        // Given
        SalesTaxRate salesTaxRate = createSalesTaxRates();
        SalesTax givenSalesTax = new SalesTax(1000, salesTaxRate);

        // When
        boolean isEquals = salesTax.equals(givenSalesTax);

        // Then
        assertTrue(isEquals);
    }

}
