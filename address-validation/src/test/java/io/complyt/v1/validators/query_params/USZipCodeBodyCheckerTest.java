package io.complyt.v1.validators.query_params;

import io.complyt.v1.config.error_messages.GenericErrorMessages;
import io.complyt.v1.models.AddressDto;
import org.mockito.Mock;
import org.springframework.web.reactive.function.server.ServerRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import static org.mockito.Mockito.mock;

class USZipCodeBodyCheckerTest {
    USZipCodeBodyChecker checker = new USZipCodeBodyChecker();

    AddressDto addressDto;
    @Mock
    ServerRequest serverRequest;

    @BeforeEach
    void setUp() {
        serverRequest = mock(ServerRequest.class);
        addressDto = TestUtilities.getAddressDto();
    }

    @Test
    void apply_CountryIsUSA_InvalidZip_ReturnsError() {
        // Given
        addressDto = addressDto.withZip("123ABC");

        // When
        Mono<String> result = checker.apply(addressDto, serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNext(GenericErrorMessages.ZIP_FORMAT_INVALID)
                .verifyComplete();
    }

    @Test
    void apply_CountryIsUSA_ValidZip_ReturnsEmpty() {
        // When
        Mono<String> result = checker.apply(addressDto, serverRequest);

        // Then
        StepVerifier.create(result)
                .verifyComplete(); // Expecting an empty Mono
    }

    @Test
    void apply_CountryIsNotUSA_InvalidZip_ReturnsEmpty() {
        // Given
        addressDto = addressDto.withCountry("Israel");

        // When
        Mono<String> result = checker.apply(addressDto, serverRequest);

        // Then
        StepVerifier.create(result)
                .verifyComplete(); // Expecting an empty Mono
    }
}