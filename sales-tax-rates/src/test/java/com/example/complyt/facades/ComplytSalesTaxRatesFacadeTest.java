package com.example.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.facade.ComplytSalesTaxRatesFacade;
import com.complyt.services.ComplytSalesTaxRatesService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComplytSalesTaxRatesFacadeTest {

    @InjectMocks
    ComplytSalesTaxRatesFacade complytSalesTaxRatesFacade;

    @Mock
    ComplytSalesTaxRatesService complytSalesTaxRatesService;

    @Test
    void findByAddress_RatesReturnedFromService_ReturnsRates() {
        // Given
        ComplytSalesTaxRates expectedComplytSalesTaxRatesFacade = TestUtilities.createCaliforniaComplytSalesTaxRates();
        Address address = TestUtilities.createAddressInCalifornia();

        // When
        when(complytSalesTaxRatesService.findByAddress(address)).thenReturn(Mono.just(expectedComplytSalesTaxRatesFacade));
        Mono<ComplytSalesTaxRates> addressWithSalesTaxRatesMono = complytSalesTaxRatesFacade.findByAddress(address);

        // Then
        StepVerifier.create(addressWithSalesTaxRatesMono).expectNext(expectedComplytSalesTaxRatesFacade).verifyComplete();
    }

    @Test
    void findByAddress_NullAddressPassed_ThrowsException() {
        // Given
        Address nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> complytSalesTaxRatesFacade.findByAddress(nullAddress));

        assertEquals(nullPointerException.getMessage(), "address is marked non-null but is null");
    }
}
