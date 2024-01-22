package com.complyt.v1.validators.param_checker;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ParamCheckerFunctionsTest {

    @Test
    public void uuidCheck_ValidUuid_ReturnsMonoEmpty() {
        // Given & When
        String validUuid = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
        Mono<String> result = ParamCheckerFunctions.UUID_CHECK.apply(validUuid);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    public void uuidCheck_InvalidUuid_ReturnsErrorMessage() {
        // Given & When
        String invalidUuid = "invalidUuid";
        Mono<String> result = ParamCheckerFunctions.UUID_CHECK.apply(invalidUuid);

        // Then
        StepVerifier.create(result)
                .expectNext(DtoErrorMessages.COMPLYT_ID_FORMAT_ERROR)
                .verifyComplete();
    }

    @Test
    public void sourceCheck_ValidSource_ReturnsMonoEmpty() {
        // Given & When
        String validSource = "1";
        Mono<String> result = ParamCheckerFunctions.SOURCE_CHECK.apply(validSource);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    public void sourceCheck_InvalidSource_ReturnsErrorMessage() {
        // Given & When
        String invalidSource = "invalidSource";
        Mono<String> result = ParamCheckerFunctions.SOURCE_CHECK.apply(invalidSource);

        // Then
        StepVerifier.create(result)
                .expectNext(DtoErrorMessages.SOURCE_FORMAT_ERROR)
                .verifyComplete();
    }

    @Test
    public void pageCheck_ValidNumeric_ReturnsMonoEmpty() {
        // Given & When
        String validNumeric = "123";
        Mono<String> result = ParamCheckerFunctions.PAGE_CHECK.apply(validNumeric);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    public void pageCheck_InvalidNumeric_ReturnsErrorMessage() {
        // Given & When
        String invalidNumeric = "abc";
        Mono<String> result = ParamCheckerFunctions.PAGE_CHECK.apply(invalidNumeric);

        // Then
        StepVerifier.create(result)
                .expectNext(DtoErrorMessages.PAGE_FORMAT_ERROR)
                .verifyComplete();
    }

    @Test
    public void externalIdNotNullCheck_NotNullUndefined_ReturnsMonoEmpty() {
        // Given & When
        String notNullUndefined = "validParam";
        Mono<String> result = ParamCheckerFunctions.EXTERNAL_ID_NOT_NULL_CHECK.apply(notNullUndefined);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    public void externalIdNotNullCheck_Null_ReturnsErrorMessage() {
        // Given & When
        String notNullUndefined = "null";
        Mono<String> result = ParamCheckerFunctions.EXTERNAL_ID_NOT_NULL_CHECK.apply(notNullUndefined);

        // Then
        StepVerifier.create(result)
                .expectNext(DtoErrorMessages.EXTERNAL_ID_NOT_NULL_ERROR)
                .verifyComplete();
    }

    @Test
    public void externalIdNotNullCheck_Undefined_ReturnsErrorMessage() {
        // Given & When
        String notNullUndefined = "undefined";
        Mono<String> result = ParamCheckerFunctions.EXTERNAL_ID_NOT_NULL_CHECK.apply(notNullUndefined);

        // Then
        StepVerifier.create(result)
                .expectNext(DtoErrorMessages.EXTERNAL_ID_NOT_NULL_ERROR)
                .verifyComplete();
    }

    @Test
    public void stateCheck_ValidState_ReturnsMonoEmpty() {
        // Given & When
        String validState = "CA";
        Mono<String> result = ParamCheckerFunctions.STATE_CHECK.apply(validState);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    public void stateCheck_InvalidState_ReturnsErrorMessage() {
        // Given & When
        String invalidState = "invalidState";
        Mono<String> result = ParamCheckerFunctions.STATE_CHECK.apply(invalidState);

        // Then
        StepVerifier.create(result)
                .expectNext(DtoErrorMessages.STATE_FORMAT_ERROR)
                .verifyComplete();
    }

    @Test
    public void dateCheck_InvalidFormat_ReturnsErrorMessage() {
        // Given
        String invalidDate = "invalidDate";
        String errorMessage = "date must be in the format yyyy-mm-dd";
        Mono<String> result = ParamCheckerFunctions.DATE_CHECK.apply(invalidDate);

        // Then
        StepVerifier.create(result)
                .expectNext(errorMessage)
                .verifyComplete();
    }

    @Test
    public void dateCheck_ValidFormat_ReturnsEmptyMono() {
        // Given
        String validDate = "2022-01-21";
        Mono<String> result = ParamCheckerFunctions.DATE_CHECK.apply(validDate);

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }
}