package io.complyt.v1.validators.address_body_checks;

import io.complyt.v1.config.error_messages.DtoErrorMessages;
import io.complyt.v1.models.AddressDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

public class AddressExistingAbbreviationBodyCheckTest {
    private AddressDto addressDto;
    AddressExistingAbbreviationBodyCheck addressExistingAbbreviationBodyCheck;

    @BeforeEach
    void setup() {
        addressDto = TestUtilities.getAddressDto();
        addressExistingAbbreviationBodyCheck = new AddressExistingAbbreviationBodyCheck();
    }

    @Test
    void check_StateFieldIs3Chars_ReturnFluxEmpty() {
        // Given
        AddressDto givenAddress = addressDto.withState("Cal");

        // When
        Flux<String> errosStringFlux = addressExistingAbbreviationBodyCheck.check(givenAddress);

        // Then
        StepVerifier.create(errosStringFlux).verifyComplete();
    }

    @Test
    void check_StateFieldIs2CharsButExist_ReturnFluxEmpty() {
        // Given
        AddressDto givenAddress = addressDto.withState("Ca");

        // When
        Flux<String> errosStringFlux = addressExistingAbbreviationBodyCheck.check(givenAddress);

        // Then
        StepVerifier.create(errosStringFlux).verifyComplete();
    }

    @Test
    void check_StateFieldIs2CharsButDoesNotExist_ReturnAbbreviationError() {
        // Given
        AddressDto givenAddress = addressDto.withState("Ka");

        // When
        Flux<String> errosStringFlux = addressExistingAbbreviationBodyCheck.check(givenAddress);

        // Then
        StepVerifier.create(errosStringFlux)
                .expectNext(DtoErrorMessages.ABBREVIATION_DOES_NOT_EXIST).verifyComplete();
    }

    @Test
    void check_StateFieldIs1Char_ReturnFluxEmpty() {
        // Given
        AddressDto givenAddress = addressDto.withState("C");

        // When
        Flux<String> errosStringFlux = addressExistingAbbreviationBodyCheck.check(givenAddress);

        // Then
        StepVerifier.create(errosStringFlux).verifyComplete();
    }
}
