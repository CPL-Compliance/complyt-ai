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

    private SalesTaxData salesTaxData;

    @BeforeEach
    void setup() {

        TaxInfoItem taxInfoItem = new TaxInfoItem(
                null, "0", "0", null, "0", "0",
                null, null, null, null,
                null, null, "0", "0", null, null);

        salesTaxData = new FastTaxData("street", Collections.singletonList(taxInfoItem), "1");
    }

    @Test
    void map_nullSalesTaxData_ReturnNull() {
        // Given+ When
        SalesTaxRates actualSalesTaxRates = SalesTaxDataToSalesTaxRateMapper.INSTANCE.map(null);

        // Then
        assertNull(actualSalesTaxRates);
    }
}
