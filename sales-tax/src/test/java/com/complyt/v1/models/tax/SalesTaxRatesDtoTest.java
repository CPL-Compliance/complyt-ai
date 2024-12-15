package com.complyt.v1.models.tax;

import com.complyt.v1.models.tax.sales_tax.SalesTaxRatesDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxRatesDtoTest {
    private final BigDecimal rate = new BigDecimal("0.5");
    private SalesTaxRatesDto salesTaxRatesDto;

    private SalesTaxRatesDto createSalesTaxRateDto() {
        return new SalesTaxRatesDto(rate, rate, rate, rate, null, null, null, null, rate);
    }

    @BeforeEach
    void setup() {
        salesTaxRatesDto = createSalesTaxRateDto();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTaxRatesDto[stateRate=" + rate +
                ", countyRate=" + rate +
                ", cityRate=" + rate +
                ", combinedDistrictRate=" + rate +
                ", ratesMetaData=null" +
                ", mtaRate=null" +
                ", spdRate=null" +
                ", otherRate=null" +
                ", taxRate=" + rate + "]";


        // When
        String actualString = salesTaxRatesDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTaxRateDto_ReturnTrue() {
        // Given
        SalesTaxRatesDto givenSalesTaxRateDto = createSalesTaxRateDto();

        // When
        boolean isEquals = salesTaxRatesDto.equals(givenSalesTaxRateDto);

        // Then
        assertTrue(isEquals);
    }

}