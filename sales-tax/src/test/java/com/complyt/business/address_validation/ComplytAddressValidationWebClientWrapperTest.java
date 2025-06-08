package com.complyt.business.address_validation;

import com.complyt.business.exceptions.ComplytAddressValidationException;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.proxies.AddressValidationServiceProxy;
import com.complyt.security.TenantResolver;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.models.matched_address.MatchedAddressDataDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComplytAddressValidationWebClientWrapperTest {

    @Mock
    AddressValidationServiceProxy addressValidationServiceProxy;
    ComplytAddressValidationWebClientWrapper complytAddressValidationWebClientWrapper;
    UnitTestUtilities testUtilities;
    Address address;
    MatchedAddressDataDto mandatoryAddressDto;
    ShippingAddress shippingAddress;




    @BeforeEach
    void setUp() {
        complytAddressValidationWebClientWrapper = new ComplytAddressValidationWebClientWrapper(addressValidationServiceProxy);
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        address = testUtilities.createAddress();
        mandatoryAddressDto = testUtilities.createMatchedAddressDto();
        shippingAddress = testUtilities.createShippingAddress();
    }

    @Test
    void validateAddress_success() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.just(mandatoryAddressDto));
        Mono<MatchedAddressDataDto> result = complytAddressValidationWebClientWrapper.validateAddress(shippingAddress);

        StepVerifier.create(result)
                .expectNext(mandatoryAddressDto)
                .verifyComplete();
    }

    @Test
    void validateAddress_Address_success() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.just(mandatoryAddressDto));

        Mono<MatchedAddressDataDto> result = complytAddressValidationWebClientWrapper.validateAddress(address.city(), address.country(), address.county(), address.state(), address.street(), address.zip(), address.isPartial());

        StepVerifier.create(result)
                .expectNext(mandatoryAddressDto)
                .verifyComplete();
    }

    @Test
    void validateAddress_retriesExhausted() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.error(new RuntimeException("Retries Exception")));

        Mono<MatchedAddressDataDto> result = complytAddressValidationWebClientWrapper.validateAddress(shippingAddress);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof ComplytAddressValidationException
                        && throwable.getMessage().equals("5 Retries Exhausted"))
                .verify();
    }

    @Test
    void validateAddress_notFound() {
        when(addressValidationServiceProxy.validateAddress(address.state(), address.country(), address.county(), address.city(), address.street(), address.zip(), address.isPartial()))
                .thenReturn(Mono.error(UnitTestUtilities.create400BadRequestFeignException()));

        Mono<MatchedAddressDataDto> result = complytAddressValidationWebClientWrapper.validateAddress(shippingAddress);

        StepVerifier.create(result)
                .expectError(ObjectNotValidApiException.class)
                .verify();
    }
}