package io.complyt.domain.sales_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalesTaxRatesTest {

    private final BigDecimal rate = new BigDecimal("0.5");
    private SalesTaxRates salesTaxRates;

    private SalesTaxRates createSalesTaxRates() {
        return new SalesTaxRates(null, rate, rate, rate, null,null, null, null,  rate);
    }

   

    @BeforeEach
    void setup() {
        salesTaxRates = createSalesTaxRates();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTaxRates[stateRate=null" +
                ", countyRate=" + rate +
                ", cityRate=" + rate +
                ", combinedDistrictRate=" + rate +
                ", ratesMetaData=null" +
                ", mtaRate=null" +
                ", spdRate=null" +
                ", otherRate=null" +
                ", taxRate=" + rate + "]";

        // When
        String actualString = salesTaxRates.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameSalesTaxRate_ReturnTrue() {
        // Given
        SalesTaxRates givenSalesTaxRate = createSalesTaxRates();

        // When
        boolean isEquals = salesTaxRates.equals(givenSalesTaxRate);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void zeroSalesTaxRate_ReturnSalesTaxRate() {
        // Given + When
        SalesTaxRates givenSalesTaxRate = SalesTaxRates.zeroSalesTaxRate();

        // Then
        assertEquals(BigDecimal.ZERO, givenSalesTaxRate.taxRate());
        assertEquals(BigDecimal.ZERO, givenSalesTaxRate.stateRate());
        assertEquals(BigDecimal.ZERO, givenSalesTaxRate.cityRate());
        assertEquals(BigDecimal.ZERO, givenSalesTaxRate.countyRate());
    }

}