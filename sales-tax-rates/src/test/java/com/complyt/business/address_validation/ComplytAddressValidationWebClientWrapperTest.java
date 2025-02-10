package com.complyt.business.address_validation;

import com.complyt.business.exceptions.ComplytAddressValidationException;
import com.complyt.domain.Address;
import com.complyt.domain.matched_address.MatchedAddressData;
import com.complyt.proxies.AddressValidationServiceProxy;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComplytAddressValidationWebClientWrapperTest {
    @Mock
    AddressValidationServiceProxy addressValidationServiceProxy;
    ComplytAddressValidationWebClientWrapper complytAddressValidationWebClientWrapper;
    Address address;
    MatchedAddressData validateAddress;
    MatchedAddressData matchedAddressData;

    @BeforeEach
    void setUp() {
        complytAddressValidationWebClientWrapper = new ComplytAddressValidationWebClientWrapper(addressValidationServiceProxy);
        address = TestUtilities.createAddressInCalifornia();
        validateAddress = TestUtilities.createMatchedAddressInCalifornia();
        matchedAddressData = TestUtilities.createMatchedAddressInCalifornia();
    }

    @Test
    void validateAddress_success() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.just(validateAddress));

        Mono<MatchedAddressData> result = complytAddressValidationWebClientWrapper.validateAddress(address.city(), address.country(), address.county(), address.state(), address.street(), address.zip(), address.isPartial());

        StepVerifier.create(result)
                .expectNext(matchedAddressData)
                .verifyComplete();
    }

    @Test
    void validateAddress_Address_success() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.just(validateAddress));

        Mono<MatchedAddressData> result = complytAddressValidationWebClientWrapper.validateAddress(address);

        StepVerifier.create(result)
                .expectNext(matchedAddressData)
                .verifyComplete();
    }

    @Test
    void validateAddress_retriesExhausted() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.error(new RuntimeException("Retries Exception")));

        Mono<MatchedAddressData> result = complytAddressValidationWebClientWrapper.validateAddress(address);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ComplytAddressValidationException
                        && throwable.getMessage().equals("5 Retries Exhausted"))
                .verify();
    }

    @Test
    void validateAddress_notFound() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.error(TestUtilities.create400BadRequestFeignException()));

        Mono<MatchedAddressData> result = complytAddressValidationWebClientWrapper.validateAddress(address);

        StepVerifier.create(result)
                .expectError(ObjectNotValidApiException.class)
                .verify();
    }
}