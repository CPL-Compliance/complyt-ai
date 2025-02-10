package io.complyt.facades;

import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.ValidatedAddress;
import io.complyt.services.ValidAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidAddressFacadeTest {

    ValidAddressFacade validAddressFacade;

    @Mock
    ValidAddressService validAddressService;


    Address address = TestUtilities.getAddress();
    ValidatedAddress validatedAddress = TestUtilities.getValidatedAddress();
    CachedAddressData cachedAddressData = TestUtilities.getCachedAddressData();


    @BeforeEach
    void setup() {
        validAddressFacade = new ValidAddressFacade(validAddressService);
    }


    @Test
    public void validateAddress_AddressValid_ReturnsValidatedAddress() {
        // Arrange
        when(validAddressService.validateAddress(address)).thenReturn(Mono.just(validatedAddress));

        // Act & Assert
        StepVerifier.create(validAddressFacade.validateAddress(address))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    public void validateAddress_NullAddress_ThrowsNullPointerException() {
        // Arrange
        Address nullAddress = null;

        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            validAddressFacade.validateAddress(nullAddress).block();
        });

        // Assert
        assertEquals("address is marked non-null but is null", exception.getMessage());
    }


    @Test
    public void resolveAddress_AddressValid_ReturnsValidatedAddress() {
        // Arrange
        when(validAddressService.resolveAddress(address)).thenReturn(Mono.just(cachedAddressData));

        // Act & Assert
        StepVerifier.create(validAddressFacade.resolveAddress(address))
                .expectNext(cachedAddressData)
                .verifyComplete();
    }

    @Test
    public void resolveAddress_NullAddress_ThrowsNullPointerException() {
        // Arrange
        Address nullAddress = null;

        // Act & Assert
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            validAddressFacade.resolveAddress(nullAddress).block();
        });

        // Assert
        assertEquals("address is marked non-null but is null", exception.getMessage());
    }
}