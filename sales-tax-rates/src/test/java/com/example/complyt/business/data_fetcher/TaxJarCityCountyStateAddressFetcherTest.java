package com.example.complyt.business.data_fetcher;

import com.complyt.business.data_fetcher.TaxJarCityCountyStateAddressFetcher;
import com.complyt.domain.SalesTaxData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

public class TaxJarCityCountyStateAddressFetcherTest {

    private TaxJarCityCountyStateAddressFetcher taxJarCityCountyStateAddressFetcher;

    @BeforeEach
    void setUp() {
        taxJarCityCountyStateAddressFetcher = new TaxJarCityCountyStateAddressFetcher();
    }

    @Test
    void fetch_NullSalesTaxDataPassed_ThrowsException() {
        // Given
        SalesTaxData nullSalesTaxData = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> taxJarCityCountyStateAddressFetcher.fetch(nullSalesTaxData));

        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void equals_SameTaxJarCityCountyStateAddressFetcher_ReturnsTrue() {
        // Given
        TaxJarCityCountyStateAddressFetcher givenTaxJarCityCountyStateAddressFetcher = new TaxJarCityCountyStateAddressFetcher();

        // When
        boolean isEquals = taxJarCityCountyStateAddressFetcher.equals(givenTaxJarCityCountyStateAddressFetcher);

        // Then
        assertTrue(isEquals);
    }
}
