package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
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
        SalesTaxRates expectedSalesTaxRate = SalesTaxRates.zeroSalesTaxRate();

        // When
        SalesTaxRates actualSalesTaxRate = SalesTaxDataToSalesTaxRateMapper.INSTANCE.map(salesTaxData);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void map_nullSalesTaxData_ReturnNull() {
        // Given
        SalesTaxRates expectedSalesTaxRate = SalesTaxRates.zeroSalesTaxRate();

        // When
        SalesTaxRates actualSalesTaxRate = SalesTaxDataToSalesTaxRateMapper.INSTANCE.map(null);

        // Then
        assertNull(actualSalesTaxRate);
    }
}
