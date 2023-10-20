//package com.example.complyt.business.data_fetcher;
//
//import com.complyt.business.data_fetcher.ZipTaxCityCountyStateAddressFetcher;
//import com.complyt.domain.Address;
//import com.complyt.domain.SalesTaxData;
//import com.complyt.domain.zip_tax.Result;
//import com.complyt.domain.zip_tax.ZipTaxData;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
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
//class ZipTaxCityCountyStateAddressFetcherTest {
//
//    private ZipTaxCityCountyStateAddressFetcher zipTaxCityCountyStateAddressFetcher;
//
//    @BeforeEach
//    void setUp() {
//        zipTaxCityCountyStateAddressFetcher = new ZipTaxCityCountyStateAddressFetcher();
//    }
//
//    @Test
//    void fetch_FetchesCounty_ReturnsCounty() {
//        // Given
//        Result result = TestUtilities.createResult();
//        List<Result> results = new ArrayList<>() {{
//            add(result);
//        }};
//        ZipTaxData zipTaxData = new ZipTaxData("version", 0, results);
//        Address addressNoCounty = TestUtilities.createAddressInCalifornia();
//        Address addressWithInjectedCounty = addressNoCounty.withCounty(zipTaxData.getResults().get(0).geoCounty());
//
//        Mono<Address> countyMono = zipTaxCityCountyStateAddressFetcher.fetch(zipTaxData);
//
//        // Then
//        StepVerifier.create(countyMono).expectNext(addressWithInjectedCounty.county().verifyComplete();
//    }
//
//    @Test
//    void fetch_NullSalesTaxDataPassed_ThrowsException() {
//        // Given
//        SalesTaxData nullSalesTaxData = null;
//
//        // When + Then
//        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
//            zipTaxCityCountyStateAddressFetcher.fetch(nullSalesTaxData);
//        });
//
//        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
//    }
//
//    @Test
//    void equals_SameZipTaxCityCountyStateAddressFetcher_ReturnsTrue() {
//        // Given
//        ZipTaxCityCountyStateAddressFetcher givenZipTaxCityCountyStateAddressFetcher = new ZipTaxCityCountyStateAddressFetcher();
//
//        // When
//        boolean isEquals = zipTaxCityCountyStateAddressFetcher.equals(givenZipTaxCityCountyStateAddressFetcher);
//
//        // Then
//        assertTrue(isEquals);
//    }
//
//}
