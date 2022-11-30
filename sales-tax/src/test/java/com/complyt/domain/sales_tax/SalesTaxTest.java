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

    @BeforeEach
    void setup() {
        salesTax = new SalesTax(1000,createSalesTaxRates());
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTax(amount=1000.0, salesTaxRate=SalesTaxRate(cityDistrictRate=0.5, cityRate=0.5, countyDistrictRate=0.5, countyRate=0.5, stateRate=0.5, taxRate=0.5))";

        // When
        String actualString = salesTax.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTax_ReturnTrue() {
        // Given
        SalesTax givenSalesTax = new SalesTax(1000, createSalesTaxRates());

        // When
        boolean actualBoolean = salesTax.equals(givenSalesTax);

        // Then
        assertTrue(actualBoolean);
    }

    private SalesTaxRate createSalesTaxRates() {
        return new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f);
    }
}
