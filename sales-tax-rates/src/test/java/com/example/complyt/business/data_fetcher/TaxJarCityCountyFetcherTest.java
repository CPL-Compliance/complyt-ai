package com.example.complyt.business.data_fetcher;

import com.complyt.business.data_fetcher.TaxJarCityCountyFetcher;
import com.complyt.domain.SalesTaxData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

public class TaxJarCityCountyFetcherTest {

    private TaxJarCityCountyFetcher taxJarCityCountyFetcher;

    @BeforeEach
    void setUp() {
        taxJarCityCountyFetcher = new TaxJarCityCountyFetcher();
    }

    @Test
    void fetch_NullSalesTaxDataPassed_ThrowsException() {
        // Given
        SalesTaxData nullSalesTaxData = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> taxJarCityCountyFetcher.fetch(nullSalesTaxData));

        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void equals_SameTaxJarCityCountyFetcher_ReturnsTrue() {
        // Given
        TaxJarCityCountyFetcher givenTaxJarCityCountyAddressFetcher = new TaxJarCityCountyFetcher();

        // When
        boolean isEquals = taxJarCityCountyFetcher.equals(givenTaxJarCityCountyAddressFetcher);

        // Then
        assertTrue(isEquals);
    }
}
