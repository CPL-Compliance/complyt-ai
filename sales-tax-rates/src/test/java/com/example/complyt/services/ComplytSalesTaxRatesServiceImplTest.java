package com.example.complyt.services;

import com.complyt.business.data_fetcher.CountyFetcher;
import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.StatesMap;
import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.repositories.ComplytSalesTaxRatesRepository;
import com.complyt.services.ComplytSalesTaxRatesServiceImpl;
import testUtils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
    CountyFetcher countyFetcher;


    @Test
    void findByAddress_FindsComplytSalesTaxRates_ReturnsRates() {
        // Given
        Address califoniaAddress = TestUtilities.createAddressInCalifornia();
        String collectionName = StatesMap.statesToCollections.get(califoniaAddress.getState());

        ComplytSalesTaxRates expectedComplytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();

        // When
        when(salesTaxWebClientWrapper.findByAddress(any())).thenReturn(Mono.empty());
        when(complytSalesTaxRatesRepository.findByAddress(califoniaAddress, collectionName)).thenReturn(Mono.just(expectedComplytSalesTaxRates));
        Mono<ComplytSalesTaxRates> complytSalesTaxRatesMono = complytSalesTaxRatesService.findByAddress(califoniaAddress);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(expectedComplytSalesTaxRates).verifyComplete();
    }

    @Test
    void findByAddress_ComplytSalesTaxRatesNotFoundInDB_SavesNewComplytSalesTaxRates() {
        // Given
        Address califoniaAddress = TestUtilities.createAddressInCalifornia();
        Address californiaAddressWithCounty = califoniaAddress.withCounty("Fresno");
        String collectionName = StatesMap.statesToCollections.get(califoniaAddress.getState());
        SalesTaxRates californiaRates = TestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates expectedComplytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();

        FastTaxData fastTaxData = TestUtilities.createFastTaxData();

        // When
        when(complytSalesTaxRatesRepository.findByAddress(califoniaAddress, collectionName)).thenReturn(Mono.empty());
        when(salesTaxWebClientWrapper.findByAddress(califoniaAddress)).thenReturn(Mono.just(fastTaxData));
        when(countyFetcher.fetch(fastTaxData)).thenReturn(Mono.just(californiaAddressWithCounty.getCounty()));
        when(complytSalesTaxRatesRepository.save(any(), any())).thenReturn(Mono.just(expectedComplytSalesTaxRates));
        when(salesTaxDataToSalesTaxRate.map(fastTaxData)).thenReturn(Mono.just(californiaRates));

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

        assertEquals(nullPointerException.getMessage(), "address is marked non-null but is null");
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

        assertEquals(nullPointerException.getMessage(), "complytSalesTaxRates is marked non-null but is null");
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

        assertEquals(nullPointerException.getMessage(), "collection is marked non-null but is null");
    }

    @Test
    void findByAddress_RepositoryThrowsException_ThrowsException() {
        // Given
        Address address = TestUtilities.createAddressInCalifornia();
        String collection = StatesMap.statesToCollections.get(address.getState());
        when(complytSalesTaxRatesRepository.findByAddress(address, collection)).thenThrow(RuntimeException.class);

        // When + Then
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            complytSalesTaxRatesService.findByAddress(address);
        });

        assertEquals(RuntimeException.class, runtimeException.getClass());
    }

}
