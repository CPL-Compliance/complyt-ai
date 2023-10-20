//package com.example.complyt.domain.mappers;
//
//import com.complyt.domain.RatesMetaData;
//import com.complyt.domain.SalesTaxRates;
//import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
//import com.complyt.domain.fast_tax.TaxInfoItem;
//import com.complyt.domain.mappers.FastTaxGetBestMatchDataToSalesTaxRateMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.Collections;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//
//@ExtendWith(MockitoExtension.class)
//class fastTaxGetBestMatchDataGetBestMatchToSalesTaxRateMapperTest {
//    private FastTaxGetBestMatchData fastTaxGetBestMatchData;
//    private TaxInfoItem taxInfoItem;
//
//    @BeforeEach
//    void setUp() {
//        taxInfoItem = new TaxInfoItem(null, "0", "0", null, "0", "0",
//                null, null, null, null, null, null, "0",
//                "0", null, null);
//
//        fastTaxGetBestMatchData = new FastTaxGetBestMatchData("street", Collections.singletonList(taxInfoItem), "1");
//    }
//
//    @Test
//    void map_TaxInfoItem_ReturnSalesTaxRate() {
//        // Given
//        RatesMetaData ratesMetaData = new RatesMetaData(new BigDecimal(taxInfoItem.cityDistrictRate()), new BigDecimal(taxInfoItem.cityDistrictRate()));
//        SalesTaxRates expectedSalesTaxRates = SalesTaxRates.zeroSalesTaxRates()
//                .withRatesMetaData(ratesMetaData);
//
//        // When
//        SalesTaxRates actualSalesTaxRates = FastTaxGetBestMatchDataToSalesTaxRateMapper.INSTANCE.map(taxInfoItem);
//
//        // Then
//        assertEquals(expectedSalesTaxRates, actualSalesTaxRates);
//    }
//
//    @Test
//    void map_FastTaxGetBestMatchData_ReturnSalesTaxRate() {
//        // Given + When
//        RatesMetaData ratesMetaData = new RatesMetaData(new BigDecimal(taxInfoItem.cityDistrictRate()), new BigDecimal(taxInfoItem.cityDistrictRate()));
//        SalesTaxRates expectedSalesTaxRates = SalesTaxRates.zeroSalesTaxRates()
//                .withRatesMetaData(ratesMetaData);
//
//        // When
//        SalesTaxRates actualSalesTaxRates = FastTaxGetBestMatchDataToSalesTaxRateMapper.INSTANCE.map(fastTaxGetBestMatchData);
//
//        // Then
//        assertEquals(expectedSalesTaxRates, actualSalesTaxRates);
//    }
//
//    @Test
//    void map_nullTaxInfoItem_ReturnNull() {
//        // Given + When
//        SalesTaxRates actualSalesTaxRate = FastTaxGetBestMatchDataToSalesTaxRateMapper.INSTANCE.map((TaxInfoItem) null);
//
//        // Then
//        assertNull(actualSalesTaxRate);
//    }
//}