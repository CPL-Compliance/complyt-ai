package com.complyt.business.address_validation;

import com.complyt.business.exceptions.ComplytAddressValidationException;
import com.complyt.domain.transaction.Address;
import com.complyt.proxies.AddressValidationServiceProxy;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComplytAddressValidationWebClientWrapperTest {

    @Mock
    AddressValidationServiceProxy addressValidationServiceProxy;
    ComplytAddressValidationWebClientWrapper complytAddressValidationWebClientWrapper;
    UnitTestUtilities testUtilities;
    Address address;
    MandatoryAddressDto mandatoryAddressDto;


    @BeforeEach
    void setUp() {
        complytAddressValidationWebClientWrapper = new ComplytAddressValidationWebClientWrapper(addressValidationServiceProxy);
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        address = testUtilities.createAddress();
        mandatoryAddressDto = testUtilities.createMandatoryAddressDto();
    }

    @Test
    void validateAddress_success() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.just(mandatoryAddressDto));

        Mono<Address> result = complytAddressValidationWebClientWrapper.validateAddress(address);

        StepVerifier.create(result)
                .expectNext(address)
                .verifyComplete();
    }

    @Test
    void validateAddress_Address_success() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.just(mandatoryAddressDto));

        Mono<Address> result = complytAddressValidationWebClientWrapper.validateAddress(address.city(), address.country(), address.county(), address.state(), address.street(), address.zip(), address.isPartial());

        StepVerifier.create(result)
                .expectNext(address)
                .verifyComplete();
    }

    @Test
    void validateAddress_retriesExhausted() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.error(new RuntimeException("Retries Exception")));

        Mono<Address> result = complytAddressValidationWebClientWrapper.validateAddress(address);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ComplytAddressValidationException
                        && throwable.getMessage().equals("5 Retries Exhausted"))
                .verify();
    }

    @Test
    void validateAddress_notFound() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.error(UnitTestUtilities.create400BadRequestFeignException()));

        Mono<Address> result = complytAddressValidationWebClientWrapper.validateAddress(address);

        StepVerifier.create(result)
                .expectError(ObjectNotValidApiException.class)
                .verify();
    }
}