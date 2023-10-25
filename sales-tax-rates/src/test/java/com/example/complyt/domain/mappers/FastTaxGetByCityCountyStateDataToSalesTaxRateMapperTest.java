package com.example.complyt.domain.mappers;

import com.complyt.domain.RatesMetaData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.fast_tax.FastTaxGetByCityCountyStateData;
import com.complyt.domain.mappers.FastTaxGetByCityCountyStateDataToSalesTaxRateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class FastTaxGetByCityCountyStateDataToSalesTaxRateMapperTest {
    private FastTaxGetByCityCountyStateData fastTaxGetByCityCountyStateData;

    @BeforeEach
    void setUp() {
        fastTaxGetByCityCountyStateData = new FastTaxGetByCityCountyStateData("city", "county", "countyFips", "state", "", "0", "", "0", "0", "0", "0", "0");
    }

    @Test
    void map_FastTaxGetByCityCountyStateData_ReturnSalesTaxRate() {
        // Given
        RatesMetaData ratesMetaData = new RatesMetaData(new BigDecimal(fastTaxGetByCityCountyStateData.getCityDistrictRate()), new BigDecimal(fastTaxGetByCityCountyStateData.getCountyDistrictRate()));
        SalesTaxRates expectedSalesTaxRates = SalesTaxRates.zeroSalesTaxRates()
                .withRatesMetaData(ratesMetaData);

        // When
        SalesTaxRates actualSalesTaxRates = FastTaxGetByCityCountyStateDataToSalesTaxRateMapper.INSTANCE.map(fastTaxGetByCityCountyStateData);

        // Then
        assertEquals(expectedSalesTaxRates, actualSalesTaxRates);
    }


    @Test
    void map_NullFastTaxGetByCityCountyStateData_ReturnNull() {
        // Given + When
        SalesTaxRates actualSalesTaxRate = FastTaxGetByCityCountyStateDataToSalesTaxRateMapper.INSTANCE.map((FastTaxGetByCityCountyStateData) null);

        // Then
        assertNull(actualSalesTaxRate);
    }
}
