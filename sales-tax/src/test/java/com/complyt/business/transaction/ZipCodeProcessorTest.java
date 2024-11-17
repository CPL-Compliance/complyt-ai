package com.complyt.business.transaction;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ZipCodeProcessorTest {

    @Test
    void getZipCode_validZip_returnZip() {
        // Given
        String zip = "10038";

        // When
        Mono<String> actualZip = ZipCodeProcessor.get5DigitZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(zip).verifyComplete();
    }

    @Test
    void getZipCode_valid4DigitZip_returnZip() {
        // Given
        String zip = "3031";

        // When
        Mono<String> actualZip = ZipCodeProcessor.get5DigitZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }

    @Test
    void getZipCode_validFullZip_returnZip() {
        // Given
        String zip = "23031-8783";
        String expectedZip = "23031";

        // When
        Mono<String> actualZip = ZipCodeProcessor.get5DigitZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(expectedZip).verifyComplete();
    }

    @Test
    void getZipCode_validPartialZip_returnZip() {
        // Given
        String zip = "23031-83";
        String expectedZip = "23031";

        // When
        Mono<String> actualZip = ZipCodeProcessor.get5DigitZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(expectedZip).verifyComplete();
    }

    @Test
    void getZipCode_InvalidLongZip_returnZip() {
        // Given
        String zip = "100234312232";

        // When
        Mono<String> actualZip = ZipCodeProcessor.get5DigitZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }

    @Test
    void getZipCode_InvalidZipWithLetters_returnZip() {
        // Given
        String zip = "10ab2";

        // When
        Mono<String> actualZip = ZipCodeProcessor.get5DigitZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }

    @Test
    void getZipCode_InvalidZipInvalidFirstCharacter_returnZip() {
        // Given
        String zip = "-10038";

        // When
        Mono<String> actualZip = ZipCodeProcessor.get5DigitZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }

    @Test
    void getZipCode_Invalid3digitZip_returnZip() {
        // Given
        String zip = "123-1008";

        // When
        Mono<String> actualZip = ZipCodeProcessor.get5DigitZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }
}