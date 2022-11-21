package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import com.complyt.domain.sales_tax.zip_tax.Result;
import com.complyt.domain.sales_tax.zip_tax.ZipTaxData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class FastTaxDataToSalesTaxRateMapperTest {
    private FastTaxData fastTaxData;
    private TaxInfoItem taxInfoItem;
    @BeforeEach
    void setUp(){
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
                .taxInfoItems(Arrays.asList(taxInfoItem))
                .build();
    }

    @Test
    void map_TaxInfoItem_SalesTaxRate() {
        // Given + When
        SalesTaxRate expectedSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();
        SalesTaxRate actualSalesTaxRate = FastTaxDataToSalesTaxRateMapper.INSTANCE.map(taxInfoItem);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void map_FastTaxData_SalesTaxRate() {
        // Given + When
        SalesTaxRate expectedSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();
        SalesTaxRate actualSalesTaxRate = FastTaxDataToSalesTaxRateMapper.INSTANCE.map(fastTaxData);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }
}