package com.example.complyt.services;

import com.complyt.business.data_fetcher.CityCountyStateAddressFetcher;
import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.*;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.repositories.ComplytSalesTaxRatesRepository;
import com.complyt.services.ComplytSalesTaxRatesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComplytSalesTaxRatesServiceImplTest {

    @InjectMocks
    ComplytSalesTaxRatesServiceImpl complytSalesTaxRatesService;

    @Mock
    ComplytSalesTaxRatesRepository complytSalesTaxRatesRepository;

    @Mock
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @Mock
    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;

    @Mock
    CityCountyStateAddressFetcher cityCountyStateAddressFetcher;


    @Test
    void findByAddress_FindsComplytSalesTaxRates_ReturnsRates() {
        // Given
        Address califoniaAddress = TestUtilities.createAddressInCalifornia();
        String collectionName = StatesMap.statesToCollections.get(califoniaAddress.state());

        ComplytSalesTaxRates expectedComplytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();

        // When
        when(complytSalesTaxRatesRepository.findByAddress(califoniaAddress, collectionName)).thenReturn(Mono.just(expectedComplytSalesTaxRates));
        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesService.findByAddress(califoniaAddress);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(expectedComplytSalesTaxRates).verifyComplete();
    }

    @Test
    void findByAddress_ComplytSalesTaxRatesNotFoundInDB_SavesNewComplytSalesTaxRates() {
        // Given
        Address califoniaAddress = TestUtilities.createAddressInCalifornia();
        CityCountyState cityCountyState = TestUtilities.createCityCountyStateInCalifornia();
        String collectionName = StatesMap.statesToCollections.get(califoniaAddress.state());
        SalesTaxRates californiaRates = TestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates expectedComplytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();

        FastTaxGetBestMatchData fastTaxGetBestMatchData = TestUtilities.createFastTaxGetBestMatchData();

        // When
        when(complytSalesTaxRatesRepository.findByAddress(califoniaAddress, collectionName)).thenReturn(Mono.empty());
        when(salesTaxWebClientWrapper.findByAddress(califoniaAddress)).thenReturn(Mono.just(fastTaxGetBestMatchData));
        when(cityCountyStateAddressFetcher.fetch(fastTaxGetBestMatchData)).thenReturn(Mono.just(cityCountyState));
        when(complytSalesTaxRatesRepository.save(any(), any())).thenReturn(Mono.just(expectedComplytSalesTaxRates));
        when(salesTaxDataToSalesTaxRate.map(fastTaxGetBestMatchData)).thenReturn(Mono.just(californiaRates));

        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesService.findByAddress(califoniaAddress);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(expectedComplytSalesTaxRates).verifyComplete();
    }

    @Test
    void findByAddress_NullAddressPassed_ThrowsException() {
        // Given
        Address nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            complytSalesTaxRatesService.findByAddress(nullAddress);
        });

        assertEquals(nullPointerException.getMessage(), "address " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void save_NullComplytSalesTaxRatesPassed_ThrowsException() {
        // Given
        String collection = "collection";
        ComplytSalesTaxRates nullComplytSalesTaxRates = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            complytSalesTaxRatesService.save(nullComplytSalesTaxRates, collection);
        });

        assertEquals(nullPointerException.getMessage(), "complytSalesTaxRates " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void save_NullCollectionPassed_ThrowsException() {
        // Given
        String nullCollection = null;
        ComplytSalesTaxRates complytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            complytSalesTaxRatesService.save(complytSalesTaxRates, nullCollection);
        });

        assertEquals(nullPointerException.getMessage(), "collection " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void findByAddress_RepositoryThrowsException_ThrowsException() {
        // Given
        Address address = TestUtilities.createAddressInCalifornia();
        String collection = StatesMap.statesToCollections.get(address.state());
        when(complytSalesTaxRatesRepository.findByAddress(address, collection)).thenThrow(RuntimeException.class);

        // When + Then
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            complytSalesTaxRatesService.findByAddress(address);
        });

        assertEquals(RuntimeException.class, runtimeException.getClass());
    }

}
