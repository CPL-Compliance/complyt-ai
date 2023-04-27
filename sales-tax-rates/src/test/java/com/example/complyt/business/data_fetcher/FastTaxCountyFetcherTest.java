package com.example.complyt.business.data_fetcher;

import com.complyt.business.data_fetcher.FastTaxCountyFetcher;
import com.complyt.domain.Address;
import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import testUtils.TestUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FastTaxCountyFetcherTest {

    private FastTaxCountyFetcher fastTaxCountyFetcher;

    @BeforeEach
    void setUp() {
        fastTaxCountyFetcher = new FastTaxCountyFetcher();
    }

    private TaxInfoItem createTaxInfoItem() {
        return new TaxInfoItem("city", "", "", "injectedCounty", "", "", null, "", "", "", "", "", "", "", "", "");
    }

    @Test
    void inject_InjectsCounty_ReturnsTransaction() {
        // Given
        TaxInfoItem taxInfoItem = createTaxInfoItem();
        List<TaxInfoItem> taxInfoItems = new ArrayList<>() {{
            add(taxInfoItem);
        }};
        FastTaxData fastTaxData = new FastTaxData("0", taxInfoItems, "1");
        Address addressNoCounty = TestUtilities.createAddressInCalifornia();
        Address addressWithInjectedCounty = addressNoCounty.withCounty(fastTaxData.getTaxInfoItems().get(0).getCounty());

        // When
        Mono<String> countyMono = fastTaxCountyFetcher.fetch(fastTaxData);

        // Then
        StepVerifier.create(countyMono).expectNext(addressWithInjectedCounty.getCounty()).verifyComplete();
    }

    @Test
    void equals_SameTransactionFastTaxCountyFetcher_ReturnsTrue() {
        // Given
        FastTaxCountyFetcher givenFastTaxCountyFetcher = new FastTaxCountyFetcher();

        // When
        boolean isEquals = fastTaxCountyFetcher.equals(givenFastTaxCountyFetcher);

        // Then
        assertTrue(isEquals);
    }
}
