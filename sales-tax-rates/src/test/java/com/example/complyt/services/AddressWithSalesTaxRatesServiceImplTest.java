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
import com.testUtils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

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
        SalesTaxRates californiaRates = TestUtilities.createCaliforniaSalesTaxRates();
        FastTaxData fastTaxData = new FastTaxData();

        AddressWithSalesTaxRates expectedAddressWithSalesTaxRates =
                new AddressWithSalesTaxRates(califoniaAddress, californiaRates, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        // When
        when(salesTaxWebClientWrapper.findByAddress(any())).thenReturn(Mono.just(fastTaxData));
        when(addressWithSalesTaxRatesRepository.findByAddress(califoniaAddress, collectionName)).thenReturn(Mono.just(expectedAddressWithSalesTaxRates));
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

}
