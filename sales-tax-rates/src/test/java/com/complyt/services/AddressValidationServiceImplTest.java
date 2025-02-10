package com.complyt.services;

import com.complyt.business.address_validation.AddressValidationWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.matched_address.MatchedAddressData;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressValidationServiceImplTest {
    @InjectMocks
    AddressValidationServiceImpl addressValidationService;
    @Mock
    AddressValidationWebClientWrapper<MatchedAddressData> addressValidationWebClientWrapper;
    private final Address address = TestUtilities.createAddressInCalifornia();
    private final MatchedAddressData matchedAddressData = TestUtilities.createMatchedAddressInCalifornia();

    @Test
    void validated_AddressPassed_returnsAddress() {
        // When
        when(addressValidationWebClientWrapper.validateAddress(address)).thenReturn(Mono.just(matchedAddressData));

        Mono<MatchedAddressData> addressMono = addressValidationService.validate(address);

        // Then
        StepVerifier.create(addressMono)
                .expectNext(matchedAddressData).verifyComplete();
    }

    @Test
    void validated_NullAddressPassed_returnsAddress() {
        // Given
        Address nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            addressValidationService.validate(nullAddress);
        });

        assertEquals("address " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE, nullPointerException.getMessage());
    }
}