package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTaxRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ComplytSalesTaxRatesToSalesTaxRatesMapperTest {

    private ComplytSalesTaxRates complytSalesTaxRates;

    @BeforeEach
    void setUp() {
        complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
    }

    @Test
    void map_ComplytSalesTaxRates_ReturnSalesTaxRate() {
        // Given
        SalesTaxRates expectedSalesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates();

        // When
        SalesTaxRates actualSalesTaxRates = ComplytSalesTaxRatesToSalesTaxRatesMapper.INSTANCE.map(complytSalesTaxRates);

        // Then
        assertEquals(expectedSalesTaxRates, actualSalesTaxRates);
    }

    @Test
    void map_nullComplytSalesTaxRates_ReturnNull() {
        // Given + When
        SalesTaxRates actualSalesTaxRates = ComplytSalesTaxRatesToSalesTaxRatesMapper.INSTANCE.map(null);

        // Then
        assertNull(actualSalesTaxRates);
    }
}