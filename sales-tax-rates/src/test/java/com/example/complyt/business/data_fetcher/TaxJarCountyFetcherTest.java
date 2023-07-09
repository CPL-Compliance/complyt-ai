package com.example.complyt.business.data_fetcher;

import com.complyt.business.data_fetcher.TaxJarCountyFetcher;
import com.complyt.domain.SalesTaxData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

public class TaxJarCountyFetcherTest {

    private TaxJarCountyFetcher taxJarCountyFetcher;

    @BeforeEach
    void setUp() {
        taxJarCountyFetcher = new TaxJarCountyFetcher();
    }

    @Test
    void fetch_NullSalesTaxDataPassed_ThrowsException() {
        // Given
        SalesTaxData nullSalesTaxData = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> taxJarCountyFetcher.fetch(nullSalesTaxData));

        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void equals_SameTaxJarCountyFetcher_ReturnsTrue() {
        // Given
        TaxJarCountyFetcher givenTaxJarCountyFetcher = new TaxJarCountyFetcher();

        // When
        boolean isEquals = taxJarCountyFetcher.equals(givenTaxJarCountyFetcher);

        // Then
        assertTrue(isEquals);
    }
}
