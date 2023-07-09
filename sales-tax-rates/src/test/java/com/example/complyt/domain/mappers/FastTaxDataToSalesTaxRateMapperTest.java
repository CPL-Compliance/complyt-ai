package com.example.complyt.domain.mappers;

import com.complyt.domain.RatesMetaData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import com.complyt.domain.mappers.FastTaxDataToSalesTaxRateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class FastTaxDataToSalesTaxRateMapperTest {
    private FastTaxData fastTaxData;
    private TaxInfoItem taxInfoItem;

    @BeforeEach
    void setUp() {
        taxInfoItem = new TaxInfoItem(null, "0", "0", null, "0", "0",
                null, null, null, null, null, null, "0",
                "0", null, null);

        fastTaxData = new FastTaxData("street", Collections.singletonList(taxInfoItem), "1");
    }

    @Test
    void map_TaxInfoItem_ReturnSalesTaxRate() {
        // Given
        RatesMetaData ratesMetaData = new RatesMetaData(Float.parseFloat(taxInfoItem.cityDistrictRate()), Float.parseFloat(taxInfoItem.cityDistrictRate()));
        SalesTaxRates expectedSalesTaxRates = SalesTaxRates.zeroSalesTaxRates()
                .withRatesMetaData(ratesMetaData);

        // When
        SalesTaxRates actualSalesTaxRates = FastTaxDataToSalesTaxRateMapper.INSTANCE.map(taxInfoItem);

        // Then
        assertEquals(expectedSalesTaxRates, actualSalesTaxRates);
    }

    @Test
    void map_FastTaxData_ReturnSalesTaxRate() {
        // Given + When
        RatesMetaData ratesMetaData = new RatesMetaData(Float.parseFloat(taxInfoItem.cityDistrictRate()), Float.parseFloat(taxInfoItem.cityDistrictRate()));
        SalesTaxRates expectedSalesTaxRates = SalesTaxRates.zeroSalesTaxRates()
                .withRatesMetaData(ratesMetaData);

        // When
        SalesTaxRates actualSalesTaxRates = FastTaxDataToSalesTaxRateMapper.INSTANCE.map(fastTaxData);

        // Then
        assertEquals(expectedSalesTaxRates, actualSalesTaxRates);
    }

    @Test
    void map_nullTaxInfoItem_ReturnNull() {
        // Given + When
        SalesTaxRates actualSalesTaxRate = FastTaxDataToSalesTaxRateMapper.INSTANCE.map((TaxInfoItem) null);

        // Then
        assertNull(actualSalesTaxRate);
    }
}