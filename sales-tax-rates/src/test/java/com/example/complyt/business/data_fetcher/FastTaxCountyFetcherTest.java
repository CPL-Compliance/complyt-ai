//package com.example.complyt.business.data_fetcher;
//
//import com.complyt.business.data_fetcher.FastTaxCityCountyStateAddressFetcher;
//import com.complyt.domain.Address;
//import com.complyt.domain.SalesTaxData;
//import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
//import com.complyt.domain.fast_tax.TaxInfoItem;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//import testUtils.TestUtilities;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class FastTaxCityCountyStateAddressFetcherTest {
//
//    private FastTaxCityCountyStateAddressFetcher fastTaxCityCountyStateAddressFetcher;
//
//    @BeforeEach
//    void setUp() {
//        fastTaxCityCountyStateAddressFetcher = new FastTaxCityCountyStateAddressFetcher();
//    }
//
//    private TaxInfoItem createTaxInfoItem() {
//        return new TaxInfoItem("city", "", "", "injectedCounty", "", "", null, "", "", "", "", "", "", "", "", "");
//    }
//
//    @Test
//    void fetch_FetchesCounty_ReturnsCounty() {
//        // Given
//        TaxInfoItem taxInfoItem = createTaxInfoItem();
//        List<TaxInfoItem> taxInfoItems = new ArrayList<>() {{
//            add(taxInfoItem);
//        }};
//        FastTaxGetBestMatchData fastTaxGetBestMatchData = new FastTaxGetBestMatchData("0", taxInfoItems, "1");
//        Address addressNoCounty = TestUtilities.createAddressInCalifornia();
//        Address addressWithInjectedCounty = addressNoCounty.withCounty(fastTaxGetBestMatchData.getTaxInfoItems().get(0).county());
//
//        // When
//        Mono<String> countyMono = fastTaxCityCountyStateAddressFetcher.fetch(fastTaxGetBestMatchData);
//
//        // Then
//        StepVerifier.create(countyMono).expectNext(addressWithInjectedCounty.county()).verifyComplete();
//    }
//
//    @Test
//    void fetch_NullSalesTaxDataPassed_ThrowsException() {
//        // Given
//        SalesTaxData nullSalesTaxData = null;
//
//        // When + Then
//        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
//            fastTaxCityCountyStateAddressFetcher.fetch(nullSalesTaxData);
//        });
//
//        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
//    }
//
//    @Test
//    void equals_SameFastTaxCityCountyStateAddressFetcher_ReturnsTrue() {
//        // Given
//        FastTaxCityCountyStateAddressFetcher givenFastTaxCityCountyStateAddressFetcher = new FastTaxCityCountyStateAddressFetcher();
//
//        // When
//        boolean isEquals = fastTaxCityCountyStateAddressFetcher.equals(givenFastTaxCityCountyStateAddressFetcher);
//
//        // Then
//        assertTrue(isEquals);
//    }
//}
