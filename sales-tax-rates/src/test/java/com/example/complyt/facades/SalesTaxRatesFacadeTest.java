package com.example.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxRates;
import com.complyt.facade.SalesTaxRatesFacade;
import com.complyt.services.SalesTaxRatesService;
import com.testUtils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalesTaxRatesFacadeTest {

    @InjectMocks
    SalesTaxRatesFacade salesTaxRatesFacade;

    @Mock
    SalesTaxRatesService salesTaxRatesService;

    @Test
    void findByAddress_RatesReturnedFromService_ReturnsRates() {
        // Given
        SalesTaxRates expectedSalesTaxRates = TestUtilities.createCaliforniaSalesTaxRates();
        Address address = TestUtilities.createAddressInCalifornia();

        // When
        when(salesTaxRatesService.findByAddress(address)).thenReturn(Mono.just(expectedSalesTaxRates));
        Mono<SalesTaxRates> salesTaxRatesMono = salesTaxRatesFacade.findByAddress(address);

        // Then
        StepVerifier.create(salesTaxRatesMono).expectNext(expectedSalesTaxRates);
    }

    @Test
    void findByAddress_NullAddressPassed_ThrowsException() {
        // Given
        Address nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxRatesFacade.findByAddress(nullAddress));

        assertEquals(nullPointerException.getMessage(), "address is marked non-null but is null");
    }
}
