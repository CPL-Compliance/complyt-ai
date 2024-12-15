//package com.complyt.domain.mappers;
//
//import com.complyt.domain.SalesTaxRates;
//import com.complyt.domain.enums.SalesTaxSources;
//import com.complyt.domain.taxjar.TaxJarData;
//import com.taxjar.model.rates.Rate;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class TaxJarDataToSalesTaxRateMapperTest {
//
//    private TaxJarData taxJarData;
//    private Rate rate;
//
//    @BeforeEach
//    void setUp() {
//        rate = new Rate();
//        taxJarData = new TaxJarData(rate);
//    }
//
//    @Test
//    void map_TaxInfoItem_ReturnSalesTaxRate() {
//        // Given
//        SalesTaxRates expectedSalesTaxRates = SalesTaxRates.zeroSalesTaxRates(SalesTaxSources.TAX_JAR).withRatesMetaData(null);
//
//        // When
//        SalesTaxRates actualSalesTaxRates = TaxJarDataToSalesTaxRateMapper.INSTANCE.map(rate);
//
//        // Then
//        assertEquals(expectedSalesTaxRates, actualSalesTaxRates);
//    }
//
//    @Test
//    void map_TaxJarData_ReturnSalesTaxRate() {
//        // Given + When
//        SalesTaxRates expectedSalesTaxRates = SalesTaxRates.zeroSalesTaxRates(SalesTaxSources.TAX_JAR).withRatesMetaData(null);
//
//        // When
//        SalesTaxRates actualSalesTaxRates = TaxJarDataToSalesTaxRateMapper.INSTANCE.map(taxJarData);
//
//        // Then
//        assertEquals(expectedSalesTaxRates, actualSalesTaxRates);
//    }
//
//    @Test
//    void map_nullTaxInfoItem_ReturnNull() {
//        // Given + When
//        SalesTaxRates actualSalesTaxRate = TaxJarDataToSalesTaxRateMapper.INSTANCE.map((Rate) null);
//
//        // Then
//        Assertions.assertNull(actualSalesTaxRate);
//    }
//}
