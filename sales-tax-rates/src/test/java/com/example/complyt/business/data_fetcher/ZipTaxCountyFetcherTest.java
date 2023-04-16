package com.example.complyt.business.data_fetcher;

import com.complyt.business.data_fetcher.ZipTaxCountyFetcher;
import com.complyt.domain.Address;
import com.complyt.domain.zip_tax.Result;
import com.complyt.domain.zip_tax.ZipTaxData;
import com.testUtils.TestUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ZipTaxCountyFetcherTest {

    private ZipTaxCountyFetcher zipTaxCountyFetcher;

    @BeforeEach
    void setUp() {
        zipTaxCountyFetcher = new ZipTaxCountyFetcher();
    }

    @Test
    void inject_InjectsCounty_ReturnsTransaction() {
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
        StepVerifier.create(countyMono).expectNext(addressWithInjectedCounty.getCounty()).verifyComplete();
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
