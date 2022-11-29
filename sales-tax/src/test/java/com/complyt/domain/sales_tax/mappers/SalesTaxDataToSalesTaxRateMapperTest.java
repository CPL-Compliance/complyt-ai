package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                .taxInfoItems(Arrays.asList(taxInfoItem))
                .build();
    }
    @Test
    void map_TaxInfoItem_SalesTaxRate() {
        // Given + When
        SalesTaxRate expectedSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();
        SalesTaxRate actualSalesTaxRate = SalesTaxDataToSalesTaxRateMapper.INSTANCE.map(salesTaxData);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }
}
