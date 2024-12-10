package com.complyt.domain.mappers;

import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FastTaxGetBestMatchDataToSalesTaxRateMapperTest {
    private FastTaxGetBestMatchDataToSalesTaxRateMapper mapper;

    private TaxInfoItem taxInfoItem;
    private FastTaxGetBestMatchData fastTaxGetBestMatchData;

    @BeforeEach
    void setUp() {
        mapper = FastTaxGetBestMatchDataToSalesTaxRateMapper.INSTANCE;

        // Create a TaxInfoItem instance
        taxInfoItem  = new TaxInfoItem(
                "Fresno",                // city
                "0.05000",                 // cityDistrictRate
                "0.07000",                 // cityRate
                "Fresno",                // county
                "0.02000",                 // countyDistrictRate
                "0.03000",                 // countyRate
                Collections.emptyList(), // informationComponents
                "",                     // notesCodes
                "",                     // notesDesc
                "0.01000",                 // specialDistrictRate
                "CA",                   // stateAbbreviation
                "California",           // stateName
                "0.04000",                 // stateRate
                "0.09000",                 // taxRate
                "LABOR/FREIGHT/SERVICES", // totalTaxExempt
                "93711-5508"            // zip
        );
        fastTaxGetBestMatchData = TestUtilities.createFastTaxGetBestMatchData().withTaxInfoItems(List.of(taxInfoItem));
    }

    @Test
    void map_TaxInfoItem_ReturnsSalesTaxRates() {
        // When
        SalesTaxRates salesTaxRates = mapper.map(taxInfoItem);

        // Then
        assertNotNull(salesTaxRates);
        assertEquals(new BigDecimal("0.05000").stripTrailingZeros(), salesTaxRates.ratesMetaData().cityDistrictRate());
        assertEquals(new BigDecimal("0.02000").stripTrailingZeros(), salesTaxRates.ratesMetaData().countyDistrictRate());
        assertEquals(new BigDecimal("0.01000").stripTrailingZeros(), salesTaxRates.ratesMetaData().specialDistrictRate());
        assertEquals(new BigDecimal("0.07000").stripTrailingZeros(), salesTaxRates.cityRate());
        assertEquals(new BigDecimal("0.09000").stripTrailingZeros(), salesTaxRates.taxRate());
        assertEquals(new BigDecimal("0.03000").stripTrailingZeros(), salesTaxRates.countyRate());
        assertEquals(new BigDecimal("0.04000").stripTrailingZeros(), salesTaxRates.stateRate());
        assertEquals(new BigDecimal("0.08000").stripTrailingZeros(), salesTaxRates.combinedDistrictRate()); // Sum of city, county, and special district rates
    }

    @Test
    void map_SalesTaxData_ReturnsSalesTaxRates() {
        // When
        SalesTaxRates salesTaxRates = mapper.map(fastTaxGetBestMatchData);

        // Then
        assertNotNull(salesTaxRates);
        assertEquals(new BigDecimal("0.07000").stripTrailingZeros(), salesTaxRates.cityRate());
        assertEquals(new BigDecimal("0.02000").stripTrailingZeros(), salesTaxRates.ratesMetaData().countyDistrictRate());
        assertEquals(new BigDecimal("0.01000").stripTrailingZeros(), salesTaxRates.ratesMetaData().specialDistrictRate());
        assertEquals(new BigDecimal("0.07000").stripTrailingZeros(), salesTaxRates.cityRate());
        assertEquals(new BigDecimal("0.09000").stripTrailingZeros(), salesTaxRates.taxRate());
        assertEquals(new BigDecimal("0.03000").stripTrailingZeros(), salesTaxRates.countyRate());
        assertEquals(new BigDecimal("0.04000").stripTrailingZeros(), salesTaxRates.stateRate());
        assertEquals(new BigDecimal("0.08000").stripTrailingZeros(), salesTaxRates.combinedDistrictRate()); // Sum of city, county, and special district rates
    }

    @Test
    void toCombinedDistrictRate_ValidTaxInfoItem_ReturnsCorrectSum() {
        // When
        BigDecimal combinedDistrictRate = mapper.toCombinedDistrictRate(taxInfoItem);

        // Then
        assertNotNull(combinedDistrictRate);
        assertEquals(new BigDecimal("0.08000").stripTrailingZeros(), combinedDistrictRate); // 0.05000 + 0.02000 + 0.01000
    }

    @Test
    void stripTrailingZeros_ValidTaxInfoItem_ReturnsTax() {
        // When
        BigDecimal combinedDistrictRate = mapper.stripTrailingZeros(new BigDecimal("0.03000"));

        // Then
        assertEquals(new BigDecimal("0.03000").stripTrailingZeros(), combinedDistrictRate);
    }

    @Test
    void stripTrailingZeros_ValidTaxNull_ReturnsNull() {
        // When
        BigDecimal combinedDistrictRate = mapper.stripTrailingZeros(null);

        // Then
        assertNull(combinedDistrictRate);
    }
}