package com.example.complyt.business.data_fetcher;

import com.complyt.business.data_fetcher.FastTaxCountyFetcher;
import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
    void fetch_FetchesCounty_ReturnsCounty() {
        // Given
        TaxInfoItem taxInfoItem = createTaxInfoItem();
        List<TaxInfoItem> taxInfoItems = new ArrayList<>() {{
            add(taxInfoItem);
        }};
        FastTaxData fastTaxData = new FastTaxData("0", taxInfoItems, "1");
        Address addressNoCounty = TestUtilities.createAddressInCalifornia();
        Address addressWithInjectedCounty = addressNoCounty.withCounty(fastTaxData.getTaxInfoItems().get(0).county());

        // When
        Mono<String> countyMono = fastTaxCountyFetcher.fetch(fastTaxData);

        // Then
        StepVerifier.create(countyMono).expectNext(addressWithInjectedCounty.county()).verifyComplete();
    }

    @Test
    void fetch_NullSalesTaxDataPassed_ThrowsException() {
        // Given
        SalesTaxData nullSalesTaxData = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            fastTaxCountyFetcher.fetch(nullSalesTaxData);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void equals_SameFetcherFastTaxCountyFetcher_ReturnsTrue() {
        // Given
        FastTaxCountyFetcher givenFastTaxCountyFetcher = new FastTaxCountyFetcher();

        // When
        boolean isEquals = fastTaxCountyFetcher.equals(givenFastTaxCountyFetcher);

        // Then
        assertTrue(isEquals);
    }
}
