package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import com.complyt.domain.sales_tax.zip_tax.Result;
import com.complyt.domain.sales_tax.zip_tax.ZipTaxData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ZipTaxDataToSalesTaxRateMapperTest {
    private ZipTaxData zipTaxData;

    private Result result;


    @BeforeEach
    void setUp() {
        result = Result.builder()
                .districtSalesTax(0L)
                .citySalesTax(0L)
                .taxSales(0L)
                .countySalesTax(0L)
                .district5SalesTax(0L)
                .stateSalesTax(0L)
                .build();

        zipTaxData = new ZipTaxData();
        List<Result> resultList = new ArrayList<>();
        resultList.add(result);
        zipTaxData = zipTaxData.withVersion("1").withRCode(1).withResults(resultList);
    }

    @Test
    void map_ZipTaxData_ReturnSalesTaxRate() {
        // Given
        SalesTaxRate expectedSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();

        // When
        SalesTaxRate actualSalesTaxRate = ZipTaxDataToSalesTaxRateMapper.INSTANCE.map(zipTaxData);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void map_Result_ReturnSalesTaxRate() {
        // Given
        SalesTaxRate expectedSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();

        // When
        SalesTaxRate actualSalesTaxRate = ZipTaxDataToSalesTaxRateMapper.INSTANCE.map(result);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void map_nullResult_ReturnNull() {
        // Given + When
        SalesTaxRate actualSalesTaxRate = ZipTaxDataToSalesTaxRateMapper.INSTANCE.map((Result) null);

        // Then
        assertNull(actualSalesTaxRate);
    }
}