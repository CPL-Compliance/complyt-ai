package com.example.complyt.domain.mappers;

import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.mappers.ZipTaxDataToSalesTaxRateMapper;
import com.complyt.domain.zip_tax.Result;
import com.complyt.domain.zip_tax.ZipTaxData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

        List<Result> resultList = new ArrayList<>();
        resultList.add(result);
        zipTaxData = new ZipTaxData("1",1,resultList);
    }

    @Test
    void map_ZipTaxData_ReturnSalesTaxRate() {
        // Given
        SalesTaxRates expectedSalesTaxRates = SalesTaxRates.zeroSalesTaxRates();

        // When
        SalesTaxRates actualSalesTaxRates = ZipTaxDataToSalesTaxRateMapper.INSTANCE.map(zipTaxData);

        // Then
        assertEquals(expectedSalesTaxRates, actualSalesTaxRates);
    }

    @Test
    void map_Result_ReturnSalesTaxRate() {
        // Given
        SalesTaxRates expectedSalesTaxRate = SalesTaxRates.zeroSalesTaxRates();

        // When
        SalesTaxRates actualSalesTaxRate = ZipTaxDataToSalesTaxRateMapper.INSTANCE.map(result);

        // Then
        assertEquals(expectedSalesTaxRate, actualSalesTaxRate);
    }

    @Test
    void map_nullResult_ReturnNull() {
        // Given + When
        SalesTaxRates actualSalesTaxRate = ZipTaxDataToSalesTaxRateMapper.INSTANCE.map((Result) null);

        // Then
        assertNull(actualSalesTaxRate);
    }
}