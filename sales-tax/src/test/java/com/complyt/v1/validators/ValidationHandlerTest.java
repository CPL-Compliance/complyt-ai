package com.complyt.v1.validators;

import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.exceptions.types.PathVariableErrorException;
import com.complyt.v1.exceptions.types.QueryParamErrorException;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.param_checker.ParamCheckerFunctions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @MockBean
    ParameterChecksProvider paramChecksProvider;

    @MockBean
    ShouldCallValidate shouldCallValidate;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
    }


    //QueryParam
    @Test
    public void handle_NoParamsValid_ReturnsMonoEmpty() {
        // Given
        Map<String, String> pathVariables = new HashMap<>();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(false);

        Mono<TransactionDto> result = transactionDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void handle_WithValidQueryParams_ReturnsMonoEmpty() {
        // Given
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        Map<String, String> pathVariables = new HashMap<>();


        // When
        queryParams.add("page", "1");
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(paramChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(paramChecksProvider.getFunctionCheck("page")).thenReturn(Mono.just(ParamCheckerFunctions.PAGE_CHECK));
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(false);

        Mono<TransactionDto> result = transactionDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void handle_WithInvalidQueryParams_ThrowsQueryParamErrorException() {
        // Given
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", "null");
        Map<String, String> pathVariables = new HashMap<>();

        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(paramChecksProvider.getFunctionCheck("page")).thenReturn(Mono.just(ParamCheckerFunctions.PAGE_CHECK));
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(false);

        Mono<TransactionDto> result = transactionDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectError(QueryParamErrorException.class) // Expects an error of type QueryParamErrorException
                .verify();
    }

    // PathVariable
    @Test
    public void handle_WithValidPathVariable_ReturnsMonoEmpty() {
        // Given
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("complytId", "f47ac10b-58cc-4372-a567-0e02b2c3d479");
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        //When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(paramChecksProvider.getFunctionCheck("complytId")).thenReturn(Mono.just(ParamCheckerFunctions.UUID_CHECK));
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(false);

        Mono<TransactionDto> result = transactionDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void handle_WithInvalidPathVariable_ThrowsPathVariableErrorException() {
        // Given
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("complytId", "null");
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(paramChecksProvider.getFunctionCheck("complytId")).thenReturn(Mono.just(ParamCheckerFunctions.UUID_CHECK));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(false);

        Mono<TransactionDto> result = transactionDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectError(PathVariableErrorException.class) // Expects an error of type PathVariableErrorException
                .verify();
    }


    //validateBody function using handle function
    @Test
    void handle_UnValidQueryParamAndUnconflictedDto_ReturnsQueryParamException() {
        // Given
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString());
        Map<String, String> pathVariables = new HashMap<>();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("page", "null");

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.queryParam("page")).thenReturn("null".describeConstable());
        when(paramChecksProvider.getFunctionCheck("page")).thenReturn(Mono.just(ParamCheckerFunctions.PAGE_CHECK));
        when(serverRequest.method()).thenReturn(HttpMethod.PUT);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));

        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.handle(serverRequest);

        //Then
        StepVerifier.create(validationMono)
                .expectError(QueryParamErrorException.class) // Expects an error of type PathVariableErrorException
                .verify();
    }

    @Test
    void handle_UnValidPathVariableAndUnconflictedDto_ReturnsPathVariableException() {
        // Given
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString());
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("externalId", "null");
        pathVariables.put("source", transactionDto.source());
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.pathVariable("externalId")).thenReturn("null");
        when(serverRequest.pathVariable("source")).thenReturn(transactionDto.source());
        when(paramChecksProvider.getFunctionCheck("externalId")).thenReturn(Mono.just(ParamCheckerFunctions.EXTERNAL_ID_NOT_NULL_CHECK));
        when(paramChecksProvider.getFunctionCheck("source")).thenReturn(Mono.just(ParamCheckerFunctions.SOURCE_CHECK));
        when(dataConflictChecksProvider.getPathVariableCheck("externalId")).thenReturn(Mono.just(TransactionDto.EXTERNAL_ID_CONFLICT_CHECK));
        when(dataConflictChecksProvider.getPathVariableCheck("source")).thenReturn(Mono.just(TransactionDto.SOURCE_CONFLICT_CHECK));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.PUT);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));

        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.handle(serverRequest);

        //Then
        StepVerifier.create(validationMono)
                .expectError(PathVariableErrorException.class) // Expects an error of type PathVariableErrorException
                .verify();
    }

    @Test
    void handle_ValidAndUnconflictedDto_ReturnsDto() {
        // Given
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString());
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("externalId", transactionDto.externalId());
        pathVariables.put("source", transactionDto.source());
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.pathVariable("externalId")).thenReturn(transactionDto.externalId());
        when(serverRequest.pathVariable("source")).thenReturn(transactionDto.source());
        when(paramChecksProvider.getFunctionCheck("externalId")).thenReturn(Mono.just(ParamCheckerFunctions.EXTERNAL_ID_NOT_NULL_CHECK));
        when(paramChecksProvider.getFunctionCheck("source")).thenReturn(Mono.just(ParamCheckerFunctions.SOURCE_CHECK));
        when(dataConflictChecksProvider.getPathVariableCheck("externalId")).thenReturn(Mono.just(TransactionDto.EXTERNAL_ID_CONFLICT_CHECK));
        when(dataConflictChecksProvider.getPathVariableCheck("source")).thenReturn(Mono.just(TransactionDto.SOURCE_CONFLICT_CHECK));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.PUT);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));

        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.handle(serverRequest);

        //Then
        StepVerifier.create(validationMono).expectNext(transactionDto).verifyComplete();
    }

    @Test
    void handle_ValidButHasConflictsDto_ReturnsConflictedDataApiException() {
        // Given
        String differentExternalId = UUID.randomUUID().toString();
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString());
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("externalId", transactionDto.externalId());
        pathVariables.put("source", transactionDto.source());
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.pathVariable("externalId")).thenReturn(differentExternalId);
        when(serverRequest.pathVariable("source")).thenReturn(transactionDto.source());
        when(paramChecksProvider.getFunctionCheck("externalId")).thenReturn(Mono.just(ParamCheckerFunctions.EXTERNAL_ID_NOT_NULL_CHECK));
        when(paramChecksProvider.getFunctionCheck("source")).thenReturn(Mono.just(ParamCheckerFunctions.SOURCE_CHECK));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.PUT);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));
        when(dataConflictChecksProvider.getPathVariableCheck("externalId")).thenReturn(Mono.just(TransactionDto.EXTERNAL_ID_CONFLICT_CHECK));
        when(dataConflictChecksProvider.getPathVariableCheck("source")).thenReturn(Mono.just(TransactionDto.SOURCE_CONFLICT_CHECK));

        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void handle_InvalidDtoBodyWithPathVariables_ReturnsValidationError() {
        // Given
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString()).withTransactionType(null);
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("externalId", transactionDto.externalId());
        pathVariables.put("source", transactionDto.source());
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.pathVariable("externalId")).thenReturn(transactionDto.externalId());
        when(serverRequest.pathVariable("source")).thenReturn(transactionDto.source());
        when(paramChecksProvider.getFunctionCheck("externalId")).thenReturn(Mono.just(ParamCheckerFunctions.EXTERNAL_ID_NOT_NULL_CHECK));
        when(paramChecksProvider.getFunctionCheck("source")).thenReturn(Mono.just(ParamCheckerFunctions.SOURCE_CHECK));
        when(serverRequest.method()).thenReturn(HttpMethod.PUT);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));

        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ObjectNotValidApiException.class).verify();
    }

    @Test
    void handle_NoPathVariablesButValidTransaction_ReturnsTransactionDto() {
        // Given
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString());
        Map<String, String> pathVariables = new HashMap<>();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.method()).thenReturn(HttpMethod.PUT);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));

        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectNext(transactionDto).verifyComplete();
    }

    @Test
    void handle_NoPathVariablesInvalidTransaction_ReturnsValidationError() {
        // Given
        TransactionDto transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString()).withTransactionType(null);
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("externalId", transactionDto.externalId());
        pathVariables.put("source", transactionDto.source());
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.method()).thenReturn(HttpMethod.PUT);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.bodyToMono(TransactionDto.class)).thenReturn(Mono.just(transactionDto));

        Mono<TransactionDto> validationMono = transactionDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ObjectNotValidApiException.class).verify();
    }

    @Test
    void validateParam_ParamIsValid_ReturnsMonoEmpty() {
        // Given
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("complytId", "f47ac10b-58cc-4372-a567-0e02b2c3d479");

        // When
        Mono<String> stringMono = transactionDtoValidationHandler.validateParam("complytId", pathVariables.get("complytId"));

        // Then
        StepVerifier.create(stringMono).verifyComplete();
    }

    @Test
    void validateParam_ParamIsInValid_ReturnsValidationError() {
        // Given
        Map<String, String> pathVariablesWithInvalidComplytId = new HashMap<>();
        pathVariablesWithInvalidComplytId.put("complytId", "f47ac10b-58cc-4372-a567-0");

        // When
        Mono<String> stringMono = transactionDtoValidationHandler.validateParam("complytId", pathVariablesWithInvalidComplytId.get("complytId"));

        // Then
        StepVerifier.create(stringMono).expectError(PathVariableErrorException.class).verify();
    }

    @Test
    void validateParam_NullKeySent_ThrowsNullPointerException() {
        String nullKey = null;

        // Given + When
        Exception nullPointerException = assertThrows(NullPointerException.class, () ->
                transactionDtoValidationHandler.validateParam(nullKey, "value"));

        // Then

        assertEquals("key is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void validateParam_NullValueSent_ThrowsNullPointerException() {
        // Given
        String nullValue = null;

        // Given + When
        Exception nullPointerException = assertThrows(NullPointerException.class, () ->
                transactionDtoValidationHandler.validateParam("key", nullValue));

        // Then
        assertEquals("value is marked non-null but is null", nullPointerException.getMessage());
    }

}