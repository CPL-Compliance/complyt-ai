package com.complyt.business.transaction;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


class ZipCodeProcessorTest {

    @Test
    void getBaseZipCode_validZip_returnZip() {
        // Given
        String zip = "10038";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getBaseZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(zip).verifyComplete();
    }

    @Test
    void getBaseZipCode_validZipWithSpaces_returnZip() {
        // Given
        String zip = "  10038  ";
        String expectedZip = "10038";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getBaseZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(expectedZip).verifyComplete();
    }

    @Test
    void getBaseZipCode_valid4DigitZip_returnPaddedZip() {
        // Given
        String zip = "3031";
        String paddedZip = "03031";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getBaseZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(paddedZip).verifyComplete();
    }

    @Test
    void getBaseZipCode_validFullZip_returnZip() {
        // Given
        String zip = "23031-8783";
        String expectedZip = "23031";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getBaseZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(expectedZip).verifyComplete();
    }

    @Test
    void getBaseZipCode_validPartialZip_returnZip() {
        // Given
        String zip = "23031-83";
        String expectedZip = "23031";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getBaseZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(expectedZip).verifyComplete();
    }

    @Test
    void getBaseZipCode_InvalidLongZip_returnError() {
        // Given
        String zip = "100234312232";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getBaseZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }

    @Test
    void getBaseZipCode_InvalidZipWithLetters_returnError() {
        // Given
        String zip = "10ab2";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getBaseZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }

    @Test
    void getBaseZipCode_InvalidZipInvalidFirstCharacter_returnError() {
        // Given
        String zip = "-10038";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getBaseZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }

    @Test
    void getBaseZipCode_Invalid3digitZip_returnError() {
        // Given
        String zip = "123-1008";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getBaseZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }


    @Test
    void getPaddedZipCode_validZip_returnZip() {
        // Given
        String zip = "10038";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getPaddedZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(zip).verifyComplete();
    }

    @Test
    void getPaddedZipCode_validZipWithSpaces_returnZip() {
        // Given
        String zip = "  10038  ";
        String expectedZip = "10038";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getPaddedZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(expectedZip).verifyComplete();
    }

    @Test
    void getPaddedZipCode_valid4DigitZip_returnPaddedZip() {
        // Given
        String zip = "3031";
        String paddedZip = "03031";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getPaddedZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(paddedZip).verifyComplete();
    }

    @Test
    void getPaddedZipCode_validFullZip_returnZip() {
        // Given
        String zip = "23031-8783";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getPaddedZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(zip).verifyComplete();
    }

    @Test
    void getPaddedZipCode_validPartialZip_returnZip() {
        // Given
        String zip = "23031-83";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getPaddedZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(zip).verifyComplete();
    }

    @Test
    void getPaddedZipCode_validFullZipWithSpaces_returnZip() {
        // Given
        String zip = "  10038-2222  ";
        String expectedZip = "10038-2222";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getPaddedZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(expectedZip).verifyComplete();
    }

    @Test
    void getPaddedZipCode_validBase4DigitZipWithSpaces_returnZip() {
        // Given
        String zip = "  1138-2222  ";
        String expectedZip = "01138-2222";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getPaddedZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectNext(expectedZip).verifyComplete();
    }

    @Test
    void getPaddedZipCode_InvalidLongZip_returnError() {
        // Given
        String zip = "100234312232";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getPaddedZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }

    @Test
    void getPaddedZipCode_InvalidZipWithLetters_returnError() {
        // Given
        String zip = "10ab2";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getPaddedZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }

    @Test
    void getPaddedZipCode_InvalidZipInvalidFirstCharacter_returnError() {
        // Given
        String zip = "-10038";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getPaddedZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }

    @Test
    void getPaddedZipCode_Invalid3digitZip_returnError() {
        // Given
        String zip = "123-1008";

        // When
        Mono<String> actualZip = ZipCodeProcessor.getPaddedZipCode(zip);

        // Then
        StepVerifier.create(actualZip).expectError();
    }
}