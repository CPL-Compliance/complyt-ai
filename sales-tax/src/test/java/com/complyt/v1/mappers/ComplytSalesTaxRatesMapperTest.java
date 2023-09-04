package com.complyt.v1.mappers;

import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.v1.models.sales_tax.ComplytSalesTaxRatesDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

public class ComplytSalesTaxRatesMapperTest {

    ComplytSalesTaxRatesDto expectedComplytSalesTaxRatesDto;
    ComplytSalesTaxRates expectedComplytSalesTaxRates;

    @BeforeEach
    void setUp() {
        expectedComplytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        expectedComplytSalesTaxRatesDto = UnitTestUtilities.createCaliforniaComplytSalesTaxRatesDto();
    }

    @Test
    void complytSalesTaxRatesDtoToComplytSalesTaxRates_ComplytSalesTaxRatesDto_returnComplytSalesTaxRates() {
        // Given
        ComplytSalesTaxRates actualComplytSalesTaxRates = ComplytSalesTaxRatesMapper.INSTANCE.complytSalesTaxRatesDtoToComplytSalesTaxRates(expectedComplytSalesTaxRatesDto);

        // When + Then
        Assertions.assertEquals(expectedComplytSalesTaxRates, actualComplytSalesTaxRates);
    }

    @Test
    void complytSalesTaxRatesToComplytSalesTaxRatesDto_ComplytSalesTaxRates_returnComplytSalesTaxRatesDto() {
        // Given
        ComplytSalesTaxRatesDto actualComplytSalesTaxRatesDto = ComplytSalesTaxRatesMapper.INSTANCE.complytSalesTaxRatesToComplytSalesTaxRatesDto(expectedComplytSalesTaxRates);

        // When + Then
        Assertions.assertEquals(expectedComplytSalesTaxRatesDto, actualComplytSalesTaxRatesDto);
    }
}
