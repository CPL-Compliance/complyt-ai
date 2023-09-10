package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.models.transaction.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
    }

    @Test
    void validate_ValidAndUnconflictedDto_ReturnsDto() {
        // Given
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString());
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("externalId", transactionDto.externalId());
        pathVariables.put("source", transactionDto.source());

        // When

        when(dataConflictChecksProvider.getPathVariableCheck("externalId")).thenReturn(Mono.just(TransactionDto.EXTERNAL_ID_CONFLICT_CHECK));
        when(dataConflictChecksProvider.getPathVariableCheck("source")).thenReturn(Mono.just(TransactionDto.SOURCE_CONFLICT_CHECK));

        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        when(serverRequest.pathVariable("externalId")).thenReturn(transactionDto.externalId());
        when(serverRequest.pathVariable("source")).thenReturn(transactionDto.source());

        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectNext(transactionDto).verifyComplete();
    }

    @Test
    void validate_ValidButHasConflictsDto_ReturnsConflictedDataApiException() {
        // Given
        String differentExternalId = UUID.randomUUID().toString();
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString());
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("externalId", transactionDto.externalId());
        pathVariables.put("source", transactionDto.source());

        // When
        when(dataConflictChecksProvider.getPathVariableCheck("externalId")).thenReturn(Mono.just(TransactionDto.EXTERNAL_ID_CONFLICT_CHECK));
        when(dataConflictChecksProvider.getPathVariableCheck("source")).thenReturn(Mono.just(TransactionDto.SOURCE_CONFLICT_CHECK));

        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        when(serverRequest.pathVariable("externalId")).thenReturn(differentExternalId);
        when(serverRequest.pathVariable("source")).thenReturn(transactionDto.source());

        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void validate_InvalidDtoBodyWithPathVariables_ReturnsValidationError() {
        // Given
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString()).withTransactionType(null);
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("externalId", transactionDto.externalId());
        pathVariables.put("source", transactionDto.source());

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ObjectNotValidApiException.class).verify();
    }

    @Test
    void validate_NoPathVariablesButValidTransaction_ReturnsTransactionDto() {
        // Given
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString());
        Map<String, String> pathVariables = new HashMap<>();

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectNext(transactionDto).verifyComplete();
    }

    @Test
    void validate_NoPathVariablesInvalidTransaction_ReturnsValidationError() {
        // Given
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString()).withTransactionType(null);
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("externalId", transactionDto.externalId());
        pathVariables.put("source", transactionDto.source());

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ObjectNotValidApiException.class).verify();
    }
}