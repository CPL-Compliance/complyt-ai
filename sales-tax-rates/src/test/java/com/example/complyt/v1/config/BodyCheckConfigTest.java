package com.example.complyt.v1.config;

import com.complyt.v1.config.BodyCheckConfig;
import com.complyt.v1.model.AddressDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

public class BodyCheckConfigTest {

    AddressDto addressDto;

    @BeforeEach
    void setUp() {
        addressDto = TestUtilities.createAddressDtoInCalifornia();
    }

    @Test
    void transactionBodyCheck_PartialAddress_ReturnsTrue() {
        // Given
        AddressDto addressToCheck = addressDto.withPartial(true);

        // When + Then
        Flux<String> isValid = BodyCheckConfig.ADDRESS_BODY_CHECK.apply(addressToCheck);

        StepVerifier.create(isValid).expectNextCount(0).verifyComplete();
    }

    @Test
    void transactionBodyCheck_PartialFalseAndNullStreet_ReturnsFalse() {
        // Given
        AddressDto addressToCheck = addressDto.withStreet(null);

        // When + Then
        Flux<String> isValid = BodyCheckConfig.ADDRESS_BODY_CHECK.apply(addressToCheck);

        StepVerifier.create(isValid).expectNextCount(1).verifyComplete();
    }

    @Test
    void transactionBodyCheck_PartialFalseAndNullCity_ReturnsFalse() {
        // Given
        AddressDto addressToCheck = addressDto.withCity(null);

        // When + Then
        Flux<String> isValid = BodyCheckConfig.ADDRESS_BODY_CHECK.apply(addressToCheck);

        StepVerifier.create(isValid).expectNextCount(1).verifyComplete();
    }

    @Test
    void transactionBodyCheck_PartialFalseAndNullCountry_ReturnsFalse() {
        // Given
        AddressDto addressToCheck = addressDto.withCountry(null);

        // When + Then
        Flux<String> isValid = BodyCheckConfig.ADDRESS_BODY_CHECK.apply(addressToCheck);

        StepVerifier.create(isValid).expectNextCount(1).verifyComplete();
    }

}