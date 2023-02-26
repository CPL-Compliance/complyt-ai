package com.complyt.v1.validators;

import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.models.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringBootTest()
class ValidationHandlerTest {

    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

    @MockBean
    DataConflictChecksProvider dataConflictChecksProvider;

    @Autowired
    ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
    }

    @Test
    void validate_ValidAndUnconflictedDto_ReturnsDto() {
        // Given
        TransactionDto transactionDto = objectStub.createTransactionDto(UUID.randomUUID().toString());

        // When
        when(dataConflictChecksProvider.getCheck("externalId")).thenReturn(Mono.just(TransactionDto.EXTERNAL_ID_CONFLICT_CHECK));
        when(dataConflictChecksProvider.getCheck("source")).thenReturn(Mono.just(TransactionDto.SOURCE_CONFLICT_CHECK));

        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        when(serverRequest.pathVariable("externalId")).thenReturn(transactionDto.externalId());
        when(serverRequest.pathVariable("source")).thenReturn(transactionDto.source());

        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validate(serverRequest, "source", "externalId");

        // Then
        StepVerifier.create(validationMono).expectNext(transactionDto).verifyComplete();
    }

    @Test
    void validate_ValidButHasConflictsDto_ReturnsConflictedDataApiException() {
        // Given
        String differentExternalId = UUID.randomUUID().toString();
        TransactionDto transactionDto = objectStub.createTransactionDto(UUID.randomUUID().toString());

        // When
        when(dataConflictChecksProvider.getCheck("externalId")).thenReturn(Mono.just(TransactionDto.EXTERNAL_ID_CONFLICT_CHECK));
        when(dataConflictChecksProvider.getCheck("source")).thenReturn(Mono.just(TransactionDto.SOURCE_CONFLICT_CHECK));

        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        when(serverRequest.pathVariable("externalId")).thenReturn(differentExternalId);
        when(serverRequest.pathVariable("source")).thenReturn(transactionDto.source());

        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validate(serverRequest, "source", "externalId");

        // Then
        StepVerifier.create(validationMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void validate_InvalidDtoBodyWithPathVariables_ReturnsValidationError() {
        // Given
        TransactionDto transactionDto = objectStub.createTransactionDto(UUID.randomUUID().toString()).withTransactionType(null);

        // When
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validate(serverRequest, "source", "externalId");

        // Then
        StepVerifier.create(validationMono).expectError(ObjectNotValidApiException.class).verify();
    }

    @Test
    void validate_NoPathVariablesValidTransaction_ReturnsTransactionDto() {
        // Given
        TransactionDto transactionDto = objectStub.createTransactionDto(UUID.randomUUID().toString());

        // When
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectNext(transactionDto).verifyComplete();
    }

    @Test
    void validate_NoPathVariablesInvalidTransaction_ReturnsValidationError() {
        // Given
        TransactionDto transactionDto = objectStub.createTransactionDto(UUID.randomUUID().toString()).withTransactionType(null);

        // When
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ObjectNotValidApiException.class).verify();
    }
}