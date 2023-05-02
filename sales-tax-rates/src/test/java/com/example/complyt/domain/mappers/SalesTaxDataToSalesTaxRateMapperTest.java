package com.example.complyt.domain.mappers;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import com.complyt.domain.mappers.SalesTaxDataToSalesTaxRateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SalesTaxDataToSalesTaxRateMapperTest {

    private TaxInfoItem taxInfoItem;
    private SalesTaxData salesTaxData;

    @BeforeEach
    void setup() {

        taxInfoItem = TaxInfoItem.builder()
                .cityDistrictRate("0")
                .cityRate("0")
                .taxRate("0")
                .countyRate("0")
                .countyDistrictRate("0")
                .stateRate("0")
                .build();

        salesTaxData = FastTaxData.builder()
                .matchLevel("street")
                .taxInfoItems(Collections.singletonList(taxInfoItem))
                .build();
    }

    @Test
    void map_SalesTaxData_ReturnSalesTaxRate() {
        // Given
        SalesTaxRates expectedSalesTaxRates = SalesTaxRates.zeroSalesTaxRates();

        // When
        SalesTaxRates actualSalesTaxRates = SalesTaxDataToSalesTaxRateMapper.INSTANCE.map(salesTaxData);

        // Then
        assertEquals(expectedSalesTaxRates, actualSalesTaxRates);
    }

    @Test
    void map_nullSalesTaxData_ReturnNull() {
        // Given+ When
        SalesTaxRates actualSalesTaxRates = SalesTaxDataToSalesTaxRateMapper.INSTANCE.map(null);

        // Then
        assertNull(actualSalesTaxRates);
    }
}
