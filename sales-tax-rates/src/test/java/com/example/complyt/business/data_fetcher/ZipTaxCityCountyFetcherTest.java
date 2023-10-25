package com.example.complyt.business.data_fetcher;

import com.complyt.business.data_fetcher.ZipTaxCityCountyFetcher;
import com.complyt.domain.CityCountyWrapper;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.zip_tax.Result;
import com.complyt.domain.zip_tax.ZipTaxData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ZipTaxCityCountyFetcherTest {

    private ZipTaxCityCountyFetcher zipTaxCityCountyFetcher;

    @BeforeEach
    void setUp() {
        zipTaxCityCountyFetcher = new ZipTaxCityCountyFetcher();
    }

    @Test
    void fetch_FetchesCityCounty_ReturnsCounty() {
        // Given
        Result result = TestUtilities.createResult();
        List<Result> results = new ArrayList<>() {{
            add(result);
        }};
        ZipTaxData zipTaxData = new ZipTaxData("version", 0, results);

        CityCountyWrapper expectedCityCountyWrapper = new CityCountyWrapper(
                zipTaxData.getResults().get(0).geoCity(),
                zipTaxData.getResults().get(0).geoCounty());


                Mono < CityCountyWrapper > cityCountyWrapperMono = zipTaxCityCountyFetcher.fetch(zipTaxData);

        // Then
        StepVerifier.create(cityCountyWrapperMono).expectNext(expectedCityCountyWrapper).verifyComplete();
    }

    @Test
    void fetch_NullSalesTaxDataPassed_ThrowsException() {
        // Given
        SalesTaxData nullSalesTaxData = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            zipTaxCityCountyFetcher.fetch(nullSalesTaxData);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void equals_SameZipTaxCityCountyFetcher_ReturnsTrue() {
        // Given
        ZipTaxCityCountyFetcher givenZipTaxCityCountyAddressFetcher = new ZipTaxCityCountyFetcher();

        // When
        boolean isEquals = zipTaxCityCountyFetcher.equals(givenZipTaxCityCountyAddressFetcher);

        // Then
        assertTrue(isEquals);
    }

}
