package com.example.complyt.business.data_fetcher;

import com.complyt.business.data_fetcher.FastTaxGetBestMatchCityCountyStateAddressFetcher;
import com.complyt.domain.CityCountyStateWrapper;
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
class FastTaxCityCountyStateAddressFetcherTest {

    private FastTaxGetBestMatchCityCountyStateAddressFetcher fastTaxCityCountyStateAddressFetcher;

    @BeforeEach
    void setUp() {
        fastTaxCityCountyStateAddressFetcher = new FastTaxGetBestMatchCityCountyStateAddressFetcher();
    }

    private TaxInfoItem createTaxInfoItem() {
        return new TaxInfoItem("city", "", "", "injectedCounty", "", "", null, "", "", "", "", "", "", "", "", "");
    }

    @Test
    void fetch_FetchesCityCountyState_ReturnsCounty() {
        // Given
        TaxInfoItem taxInfoItem = createTaxInfoItem();
        List<TaxInfoItem> taxInfoItems = new ArrayList<>() {{
            add(taxInfoItem);
        }};
        FastTaxGetBestMatchData fastTaxGetBestMatchData = new FastTaxGetBestMatchData("0", taxInfoItems, "1");
        CityCountyStateWrapper cityCountyStateWrapper = new CityCountyStateWrapper(taxInfoItem.city(), taxInfoItem.county(), taxInfoItem.stateAbbreviation());

        // When
        Mono<CityCountyStateWrapper> cityCountyStateWrapperMono = fastTaxCityCountyStateAddressFetcher.fetch(fastTaxGetBestMatchData);

        // Then
        StepVerifier.create(cityCountyStateWrapperMono).expectNext(cityCountyStateWrapper).verifyComplete();
    }

    @Test
    void fetch_NullSalesTaxDataPassed_ThrowsException() {
        // Given
        SalesTaxData nullSalesTaxData = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            fastTaxCityCountyStateAddressFetcher.fetch(nullSalesTaxData);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void equals_SameFastTaxCityCountyStateAddressFetcher_ReturnsTrue() {
        // Given
        FastTaxGetBestMatchCityCountyStateAddressFetcher givenFastTaxCityCountyStateAddressFetcher = new FastTaxGetBestMatchCityCountyStateAddressFetcher();

        // When
        boolean isEquals = fastTaxCityCountyStateAddressFetcher.equals(givenFastTaxCityCountyStateAddressFetcher);

        // Then
        assertTrue(isEquals);
    }
}
