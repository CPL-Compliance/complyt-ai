package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTaxRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SalesTaxDataToSalesTaxRateMapperTest {

    private ComplytSalesTaxRates complytSalesTaxRates;

    @BeforeEach
    void setup() {
        complytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
    }

    @Test
    void map_SalesTaxData_ReturnSalesTaxRate() {
        // Given
        SalesTaxRates expectedSalesTaxRates = UnitTestUtilities.createCaliforniaSalesTaxRates();

        // When
        SalesTaxRates actualSalesTaxRates = ComplytSalesTaxRatesToSalesTaxRatesMapper.INSTANCE.map(complytSalesTaxRates);

        // Then
        assertEquals(expectedSalesTaxRates, actualSalesTaxRates);
    }

    @Test
    void map_nullSalesTaxData_ReturnNull() {
        // Given
        SalesTaxRates expectedSalesTaxRate = SalesTaxRates.zeroSalesTaxRate();

        // When
        SalesTaxRates actualSalesTaxRates = ComplytSalesTaxRatesToSalesTaxRatesMapper.INSTANCE.map(null);

        // Then
        assertNull(actualSalesTaxRates);
    }
}
