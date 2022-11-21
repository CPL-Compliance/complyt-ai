package com.complyt.domain.sales_tax.mappers;

import com.complyt.domain.sales_tax.SalesTaxRate;
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

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ZipTaxDataToSalesTaxRateMapperTest {
    private ZipTaxData zipTaxData;

    private Result result;


    @BeforeEach
    void setUp() {
        result = Result.builder()
                .districtSalesTax(0l)
                .citySalesTax(0l)
                .taxSales(0l)
                .countySalesTax(0l)
                .district5SalesTax(0l)
                .stateSalesTax(0l)
                .build();

        zipTaxData = new ZipTaxData();
        List<Result> resultList = new ArrayList<>();
        resultList.add(result);
        zipTaxData = zipTaxData.withVersion("1").withRCode(1).withResults(resultList);
    }

    @Test
    void map_ZipTaxData_SalesTaxRate() {
        // Given + When
        SalesTaxRate actulalSalesTaxRate = ZipTaxDataToSalesTaxRateMapper.INSTANCE.map(zipTaxData);
        SalesTaxRate expectedSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();

        // Then
        assertEquals(expectedSalesTaxRate, actulalSalesTaxRate);
    }

    @Test
    void map_Result_SalesTaxRate() {
        // Given + When
        SalesTaxRate actulalSalesTaxRate = ZipTaxDataToSalesTaxRateMapper.INSTANCE.map(result);
        SalesTaxRate expectedSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();

        // Then
        assertEquals(expectedSalesTaxRate, actulalSalesTaxRate);
    }
}