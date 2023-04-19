package com.example.complyt.services;

import com.complyt.business.data_fetcher.CountyFetcher;
import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.AddressWithSalesTaxRates;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.StatesMap;
import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.repositories.AddressWithSalesTaxRatesRepository;
import com.complyt.services.AddressWithSalesTaxRatesServiceImpl;
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
public class AddressWithSalesTaxRatesServiceImplTest {

    @InjectMocks
    AddressWithSalesTaxRatesServiceImpl addressWithSalesTaxRatesService;

    @Mock
    AddressWithSalesTaxRatesRepository addressWithSalesTaxRatesRepository;

    @Mock
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @Mock
    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;

    @Mock
    CountyFetcher countyFetcher;


    @Test
    void findByAddress_FindsAddressWithRates_ReturnsRates() {
        // Given
        Address califoniaAddress = TestUtilities.createAddressInCalifornia();
        String collectionName = StatesMap.statesToCollections.get(califoniaAddress.getState());
//        SalesTaxRates californiaRates = TestUtilities.createCaliforniaSalesTaxRates();

        AddressWithSalesTaxRates expectedAddressWithSalesTaxRates = TestUtilities.createCaliforniaAddressWithSalesTaxRates();

        // When
        when(salesTaxWebClientWrapper.findByAddress(any())).thenReturn(Mono.empty());
        when(addressWithSalesTaxRatesRepository.findByAddress(califoniaAddress, collectionName)).thenReturn(Mono.just(expectedAddressWithSalesTaxRates));
        Mono<AddressWithSalesTaxRates> addressWithSalesTaxRatesMono = addressWithSalesTaxRatesService.findByAddress(califoniaAddress);

        // Then
        StepVerifier.create(addressWithSalesTaxRatesMono).expectNext(expectedAddressWithSalesTaxRates).verifyComplete();
    }

    @Test
    void findByAddress_AddressWithSalesTaxRatesNotFoundInDB_SavesNewAddressWithSalesTaxRates() {
        // Given
        Address califoniaAddress = TestUtilities.createAddressInCalifornia();
        Address californiaAddressWithCounty = califoniaAddress.withCounty("Fresno");
        String collectionName = StatesMap.statesToCollections.get(califoniaAddress.getState());
        SalesTaxRates californiaRates = TestUtilities.createCaliforniaSalesTaxRates();
        AddressWithSalesTaxRates expectedAddressWithSalesTaxRates = TestUtilities.createCaliforniaAddressWithSalesTaxRates();

        FastTaxData fastTaxData = TestUtilities.createFastTaxData();

        // When
        when(addressWithSalesTaxRatesRepository.findByAddress(califoniaAddress, collectionName)).thenReturn(Mono.empty());
        when(salesTaxWebClientWrapper.findByAddress(califoniaAddress)).thenReturn(Mono.just(fastTaxData));
        when(countyFetcher.fetch(fastTaxData)).thenReturn(Mono.just(californiaAddressWithCounty.getCounty()));
        when(addressWithSalesTaxRatesRepository.save(any(), any())).thenReturn(Mono.just(expectedAddressWithSalesTaxRates));
        when(salesTaxDataToSalesTaxRate.map(fastTaxData)).thenReturn(Mono.just(californiaRates));

        Mono<AddressWithSalesTaxRates> addressWithSalesTaxRatesMono = addressWithSalesTaxRatesService.findByAddress(califoniaAddress);

        // Then
        StepVerifier.create(addressWithSalesTaxRatesMono).expectNext(expectedAddressWithSalesTaxRates).verifyComplete();
    }

    @Test
    void findByAddress_NullAddressPassed_ThrowsException() {
        // Given
        Address nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            addressWithSalesTaxRatesService.findByAddress(nullAddress);
        });

        assertEquals(nullPointerException.getMessage(), "address is marked non-null but is null");
    }

    @Test
    void save_NullAddressWithSalesTaxRatesPassed_ThrowsException() {
        // Given
        String collection = "collection";
        AddressWithSalesTaxRates nullAddressWithSalesTaxRates = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            addressWithSalesTaxRatesService.save(nullAddressWithSalesTaxRates, collection);
        });

        assertEquals(nullPointerException.getMessage(), "addressWithSalesTaxRates is marked non-null but is null");
    }

    @Test
    void save_NullCollectionPassed_ThrowsException() {
        // Given
        String nullCollection = null;
        AddressWithSalesTaxRates addressWithSalesTaxRates = TestUtilities.createCaliforniaAddressWithSalesTaxRates();

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            addressWithSalesTaxRatesService.save(addressWithSalesTaxRates, nullCollection);
        });

        assertEquals(nullPointerException.getMessage(), "collection is marked non-null but is null");
    }

}
