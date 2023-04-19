package com.example.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.AddressWithSalesTaxRates;
import com.complyt.facade.AddressWithSalesTaxRatesFacade;
import com.complyt.services.AddressWithSalesTaxRatesService;
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
public class AddressWithSalesTaxRatesFacadeTest {

    @InjectMocks
    AddressWithSalesTaxRatesFacade addressWithSalesTaxRatesFacade;

    @Mock
    AddressWithSalesTaxRatesService addressWithSalesTaxRatesService;

    @Test
    void findByAddress_RatesReturnedFromService_ReturnsRates() {
        // Given
        AddressWithSalesTaxRates expectedAddressSalesTaxRates = TestUtilities.createCaliforniaAddressWithSalesTaxRates();
        Address address = TestUtilities.createAddressInCalifornia();

        // When
        when(addressWithSalesTaxRatesService.findByAddress(address)).thenReturn(Mono.just(expectedAddressSalesTaxRates));
        Mono<AddressWithSalesTaxRates> addressWithSalesTaxRatesMono = addressWithSalesTaxRatesFacade.findByAddress(address);

        // Then
        StepVerifier.create(addressWithSalesTaxRatesMono).expectNext(expectedAddressSalesTaxRates).verifyComplete();
    }

    @Test
    void findByAddress_NullAddressPassed_ThrowsException() {
        // Given
        Address nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> addressWithSalesTaxRatesFacade.findByAddress(nullAddress));

        assertEquals(nullPointerException.getMessage(), "address is marked non-null but is null");
    }
}
