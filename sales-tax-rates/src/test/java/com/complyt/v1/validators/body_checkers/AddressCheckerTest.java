package com.complyt.v1.validators.body_checkers;

import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.AddressWithDateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AddressCheckerTest {

    private AddressChecker addressChecker;
    private AddressWithDateDto addressWithDateDto;

    @BeforeEach
    void setup() {
        addressChecker = new AddressChecker();
        addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia(LocalDateTime.now().toString());
    }

    @Test
    public void check_PartialAddress_ReturnsEmptyFlux() {
        // When
        AddressDto addressDto = new AddressDto(null, "US", null, "11111", null,
                "11111", true);
        Flux<String> result = addressChecker.check(addressWithDateDto.withAddress(addressDto));

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void check_FullAddressWithBlankFields_ReturnsErrors() {
        // Given
        AddressDto addressDto = new AddressDto(null, "US", null, "11111", "street",
                "11111", false);
        addressWithDateDto = addressWithDateDto.withAddress(addressDto);

        // When
        Flux<String> result = addressChecker.check(addressWithDateDto);

        // Then
        StepVerifier.create(result)
                .expectNext("Address.city may not be blank in a non partial address")
                .verifyComplete();
    }
}