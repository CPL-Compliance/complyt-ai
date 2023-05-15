package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class FastTaxDataToSalesTaxRateMapperTest {
    private FastTaxData fastTaxData;
    private TaxInfoItem taxInfoItem;

    @BeforeEach
    void setUp() {
        taxInfoItem = TaxInfoItem.builder()
                .cityDistrictRate("0")
                .cityRate("0")
                .taxRate("0")
                .countyRate("0")
                .countyDistrictRate("0")
                .stateRate("0")
                .build();

        fastTaxData = FastTaxData.builder()
                .matchLevel("street")
                .taxInfoItems(Collections.singletonList(taxInfoItem))
                .build();
    }

    @Test
    void map_TaxInfoItem_ReturnSalesTaxRate() {
        // Given
        SalesTaxRates expectedSalesTaxRate = SalesTaxRates.zeroSalesTaxRate();

        // When
        SalesTaxRates actualSalesTaxRate = FastTaxDataToSalesTaxRateMapper.INSTANCE.map(taxInfoItem);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void map_FastTaxData_ReturnSalesTaxRate() {
        // Given + When
        SalesTaxRates expectedSalesTaxRate = SalesTaxRates.zeroSalesTaxRate();

        // When
        SalesTaxRates actualSalesTaxRate = FastTaxDataToSalesTaxRateMapper.INSTANCE.map(fastTaxData);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void map_nullTaxInfoItem_ReturnNull() {
        // Given + When
        SalesTaxRates actualSalesTaxRate = FastTaxDataToSalesTaxRateMapper.INSTANCE.map((TaxInfoItem) null);

        // Then
        assertNull(actualSalesTaxRate);
    }
}