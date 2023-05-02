package com.example.complyt.business.data_fetcher;

import com.complyt.business.data_fetcher.ZipTaxCountyFetcher;
import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.zip_tax.Result;
import com.complyt.domain.zip_tax.ZipTaxData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ZipTaxCountyFetcherTest {

    private ZipTaxCountyFetcher zipTaxCountyFetcher;

    @BeforeEach
    void setUp() {
        zipTaxCountyFetcher = new ZipTaxCountyFetcher();
    }

    @Test
    void fetch_FetchesCounty_ReturnsCounty() {
        // Given
        Result result = TestUtilities.createResult();
        List<Result> results = new ArrayList<>() {{
            add(result);
        }};
        ZipTaxData zipTaxData = new ZipTaxData("version", 0, results);
        Address addressNoCounty = TestUtilities.createAddressInCalifornia();
        Address addressWithInjectedCounty = addressNoCounty.withCounty(zipTaxData.getResults().get(0).getGeoCounty());

        Mono<String> countyMono = zipTaxCountyFetcher.fetch(zipTaxData);

        // Then
        StepVerifier.create(countyMono).expectNext(addressWithInjectedCounty.county()).verifyComplete();
    }

    @Test
    void fetch_NullSalesTaxDataPassed_ThrowsException() {
        // Given
        SalesTaxData nullSalesTaxData = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            zipTaxCountyFetcher.fetch(nullSalesTaxData);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void equals_SameTransactionZipTaxCountyFetcher_ReturnsTrue() {
        // Given
        ZipTaxCountyFetcher givenZipTaxCountyFetcher = new ZipTaxCountyFetcher();

        // When
        boolean isEquals = zipTaxCountyFetcher.equals(givenZipTaxCountyFetcher);

        // Then
        assertTrue(isEquals);
    }

}
