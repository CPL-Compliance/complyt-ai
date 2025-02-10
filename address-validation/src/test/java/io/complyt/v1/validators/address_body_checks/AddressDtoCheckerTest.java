package io.complyt.v1.validators.address_body_checks;

import io.complyt.domain.Address;
import io.complyt.v1.config.error_messages.DtoErrorMessages;
import io.complyt.v1.models.AddressDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_utils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AddressDtoCheckerTest {
    private AddressDtoChecker addressDtoChecker;
    private AddressDto addressDto;

    @BeforeEach
    void setUp() {
        addressDtoChecker = new AddressDtoChecker();
        addressDto = TestUtilities.getAddressDto();
    }

    @Test
    void check_PartialAddressWithMissingZipCode_ShouldReturnErrors() {
        // Given
        AddressDto addressWithoutZip = new AddressDto(null, "USA", null, "CO", null, null, true);

        Flux<String> errorMessages = addressDtoChecker.check(addressWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext("Zip may not be blank in a partial address")
                .expectNext(DtoErrorMessages.ZIP_NOT_IN_FORMAT)
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithInvalidState_ShouldReturnErrors() {
        // Given
        AddressDto addressWithoutZip = new AddressDto(null, "USA", null, "Invalid State", null, "80001", true);

        Flux<String> errorMessages = addressDtoChecker.check(addressWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext(DtoErrorMessages.STATE_NOT_RECOGNIZED_USA)
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithStateNull_ShouldReturnNoError() {
        // Given
        AddressDto addressWithoutZip = new AddressDto(null, "USA", null, "CA", null, "80001", true);

        Flux<String> errorMessages = addressDtoChecker.check(addressWithoutZip);

        StepVerifier.create(errorMessages)
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithStateBlank_ShouldReturnsError() {
        // Given
        AddressDto addressWithoutZip = new AddressDto(null, "USA", null, "", null, "80001", true);

        Flux<String> errorMessages = addressDtoChecker.check(addressWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext("The state in the provided USA address is not recognized. Please verify and provide a valid state")
                .verifyComplete();
    }

    @Test
    void check_FullValidAddress_ShouldReturnEmpty() {
        // Given
        AddressDto addressWithoutZip = new AddressDto("City", "USA", "County", "CO", "Street", "80001", false);

        Flux<String> errorMessages = addressDtoChecker.check(addressWithoutZip);

        StepVerifier.create(errorMessages).verifyComplete();
    }

    @Test
    void check_FullAddressWithInvalidStateAndNullZip_ShouldReturnErrors() {
        // Given
        AddressDto addressWithoutZip = new AddressDto("City", "USA", "County", "Invalid State", "Street", "Region", false);

        Flux<String> errorMessages = addressDtoChecker.check(addressWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext(DtoErrorMessages.ZIP_NOT_IN_FORMAT)
                .expectNext(DtoErrorMessages.STATE_NOT_RECOGNIZED_USA)
                .verifyComplete();
    }

    @Test
    void check_FullAddressWithMissingFields_ShouldReturnErrors() {
        // Given
        AddressDto addressWithoutZip = new AddressDto(null, "USA", null, null, null, "80001", false);

        Flux<String> errorMessages = addressDtoChecker.check(addressWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext("State may not be blank in a non-partial address")
                .expectNext("Street may not be blank in a non-partial address")
                .expectNext("City may not be blank in a non-partial address")
                .expectNext(DtoErrorMessages.STATE_NOT_RECOGNIZED_USA)
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithMissingZipAndBlankState_ShouldReturnErrors() {
        // Given
        AddressDto address = new AddressDto(null, "USA", null, "", null, null, true);

        Flux<String> errorMessages = addressDtoChecker.check(address);

        StepVerifier.create(errorMessages)
                .expectNext("Zip may not be blank in a partial address")
                .expectNext(DtoErrorMessages.ZIP_NOT_IN_FORMAT)
                .expectNext(DtoErrorMessages.STATE_NOT_RECOGNIZED_USA)
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithUnsupportedCountry_ShouldReturnError() {
        // Given
        AddressDto address = new AddressDto(null, "UnsupportedCountry", null, null, null, "80001", true);

        Flux<String> errorMessages = addressDtoChecker.check(address);

        StepVerifier.create(errorMessages)
                .expectNext(DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR)
                .verifyComplete();
    }

    @Test
    void check_FullAddressWithBlankCity_ShouldReturnError() {
        // Given
        AddressDto address = new AddressDto("", "USA", null, "CO", "Street", "80001", false);

        Flux<String> errorMessages = addressDtoChecker.check(address);

        StepVerifier.create(errorMessages)
                .expectNext("City may not be blank in a non-partial address")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithEmptyFields_ShouldReturnError() {
        // Given
        AddressDto address = new AddressDto("", "USA", "", "", "", "", true);

        Flux<String> errorMessages = addressDtoChecker.check(address);

        StepVerifier.create(errorMessages)
                .expectNext("Zip may not be blank in a partial address")
                .expectNext(DtoErrorMessages.ZIP_NOT_IN_FORMAT)
                .expectNext(DtoErrorMessages.STATE_NOT_RECOGNIZED_USA)
                .verifyComplete();
    }

    @Test
    void check_FullAddressWithAllFieldsMissing_ShouldReturnErrors() {
        // Given
        AddressDto address = new AddressDto(null, "USA", null, null, null, null, false);

        Flux<String> errorMessages = addressDtoChecker.check(address);

        StepVerifier.create(errorMessages)
                .expectNext("State may not be blank in a non-partial address")
                .expectNext("Street may not be blank in a non-partial address")
                .expectNext("City may not be blank in a non-partial address")
                .expectNext("Zip may not be blank in a non-partial address")
                .expectNext(DtoErrorMessages.ZIP_NOT_IN_FORMAT)
                .expectNext(DtoErrorMessages.STATE_NOT_RECOGNIZED_USA)
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithNullZipAndValidState_ShouldReturnErrors() {
        // Given
        AddressDto address = new AddressDto(null, "USA", null, "CO", null, null, true);

        Flux<String> errorMessages = addressDtoChecker.check(address);

        StepVerifier.create(errorMessages)
                .expectNext("Zip may not be blank in a partial address")
                .expectNext(DtoErrorMessages.ZIP_NOT_IN_FORMAT)
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithLongZip_ShouldReturnErrors() {
        // Given
        AddressDto address = new AddressDto(null, "USA", null, "CO", null, "1232434324324-3424123", true);

        Flux<String> errorMessages = addressDtoChecker.check(address);

        StepVerifier.create(errorMessages)
                .expectNext(DtoErrorMessages.ZIP_NOT_IN_FORMAT)
                .verifyComplete();
    }

    @Test
    void check_AddressIsNull_ReturnsError() {
        Exception nullPointerException = assertThrows(NullPointerException.class, () -> addressDtoChecker.check(null));

        // Then
        assertEquals("addressDto is marked non-null but is null", nullPointerException.getMessage());
    }
}