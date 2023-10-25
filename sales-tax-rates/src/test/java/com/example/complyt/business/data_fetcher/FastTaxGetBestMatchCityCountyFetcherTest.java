package com.example.complyt.business.data_fetcher;

import com.complyt.business.data_fetcher.FastTaxGetBestMatchCityCountyFetcher;
import com.complyt.domain.CityCountyWrapper;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FastTaxGetBestMatchCityCountyFetcherTest {

    private FastTaxGetBestMatchCityCountyFetcher fastTaxGetBestMatchCityCountyFetcher;

    @BeforeEach
    void setUp() {
        fastTaxGetBestMatchCityCountyFetcher = new FastTaxGetBestMatchCityCountyFetcher();
    }

    private TaxInfoItem createTaxInfoItem() {
        return new TaxInfoItem("city", "", "", "injectedCounty", "", "", null, "", "", "", "", "", "", "", "", "");
    }

    @Test
    void fetch_FetchesCityCounty_ReturnsCounty() {
        // Given
        TaxInfoItem taxInfoItem = createTaxInfoItem();
        List<TaxInfoItem> taxInfoItems = new ArrayList<>() {{
            add(taxInfoItem);
        }};
        FastTaxGetBestMatchData fastTaxGetBestMatchData = new FastTaxGetBestMatchData("0", taxInfoItems, "1");
        CityCountyWrapper cityCountyWrapper = new CityCountyWrapper(taxInfoItem.city(), taxInfoItem.county());

        // When
        Mono<CityCountyWrapper> cityCountyWrapperMono = fastTaxGetBestMatchCityCountyFetcher.fetch(fastTaxGetBestMatchData);

        // Then
        StepVerifier.create(cityCountyWrapperMono).expectNext(cityCountyWrapper).verifyComplete();
    }

    @Test
    void fetch_NullSalesTaxDataPassed_ThrowsException() {
        // Given
        SalesTaxData nullSalesTaxData = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            fastTaxGetBestMatchCityCountyFetcher.fetch(nullSalesTaxData);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void equals_SameFastTaxCityCountyFetcher_ReturnsTrue() {
        // Given
        FastTaxGetBestMatchCityCountyFetcher givenFastTaxCityCountyAddressFetcher = new FastTaxGetBestMatchCityCountyFetcher();

        // When
        boolean isEquals = fastTaxGetBestMatchCityCountyFetcher.equals(givenFastTaxCityCountyAddressFetcher);

        // Then
        assertTrue(isEquals);
    }
}
