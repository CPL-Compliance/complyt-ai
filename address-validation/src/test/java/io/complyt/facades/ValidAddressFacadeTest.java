package io.complyt.facades;

import io.complyt.domain.Address;
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


    @BeforeEach
    void setup() {
        validAddressFacade = new ValidAddressFacade(validAddressService);
    }


    @Test
    public void validateAddress_AddressValid_ReturnsValidatedAddress() {
        // Arrange
        when(validAddressService.validateAddress(address)).thenReturn(Mono.just(address));

        // Act & Assert
        StepVerifier.create(validAddressFacade.validateAddress(address))
                .expectNext(address)
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
}