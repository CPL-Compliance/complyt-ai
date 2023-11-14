package io.complyt.authentication.v1.validators;

import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.CredentialsDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.unit_tests.TestUtilities;

import static org.mockito.Mockito.when;

@SpringBootTest
class ValidationHandlerTest {

    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

    @Autowired
    ValidationHandler<ApiKeyDto, SpringValidatorAdapter> apiKeyDtoValidationHandler;

    @Autowired
    ValidationHandler<CredentialsDto, SpringValidatorAdapter> credentialsDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;

    @Test
    void handle_validCredentials_returnCredentialsDto() {
        // Given
        CredentialsDto credentialsDto = TestUtilities.createCredentialsDto();

        // When
        when(serverRequest.bodyToMono(CredentialsDto.class)).thenReturn(Mono.just(credentialsDto));
        Mono<CredentialsDto> credentialsDtoMono = credentialsDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(credentialsDtoMono).expectNext(credentialsDto).verifyComplete();
    }

    @Test
    void handle_notValidCredentialsMissingClientId_returnCredentialsDto() {
        // Given
        CredentialsDto credentialsDto = TestUtilities.createCredentialsDtoMissingClientId();

        // When
        when(serverRequest.bodyToMono(CredentialsDto.class)).thenReturn(Mono.just(credentialsDto));
        Mono<CredentialsDto> credentialsDtoMono = credentialsDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(credentialsDtoMono).expectError().verify();
    }

    @Test
    void handle_notValidCredentialsBlankClientId_returnCredentialsDto() {
        // Given
        CredentialsDto credentialsDto = TestUtilities.createCredentialsDtoBlankClientId();

        // When
        when(serverRequest.bodyToMono(CredentialsDto.class)).thenReturn(Mono.just(credentialsDto));
        Mono<CredentialsDto> credentialsDtoMono = credentialsDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(credentialsDtoMono).expectError().verify();
    }

    @Test
    void handle_notValidCredentialsMissingClientSecret_returnCredentialsDto() {
        // Given
        CredentialsDto credentialsDto = TestUtilities.createCredentialsDtoMissingClientSecret();

        // When
        when(serverRequest.bodyToMono(CredentialsDto.class)).thenReturn(Mono.just(credentialsDto));
        Mono<CredentialsDto> credentialsDtoMono = credentialsDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(credentialsDtoMono).expectError().verify();
    }

    @Test
    void handle_notValidCredentialsBlankClientSecret_returnCredentialsDto() {
        // Given
        CredentialsDto credentialsDto = TestUtilities.createCredentialsDtoBlankClientSecret();

        // When
        when(serverRequest.bodyToMono(CredentialsDto.class)).thenReturn(Mono.just(credentialsDto));
        Mono<CredentialsDto> credentialsDtoMono = credentialsDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(credentialsDtoMono).expectError().verify();
    }

    @Test
    void handle_validApiKey_returnApiKeyDto() {
        // Given
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        // When
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectNext(apiKeyDto).verifyComplete();
    }

    @Test
    void handle_missingApiKeyClientId_throwsError() {
        // Given
        ApiKeyDto apiKeyDto = new ApiKeyDto("", "3572db2e-486b-480a-995b-2e4d2b9104fa");

        // When
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectError().verify();
    }

    @Test
    void handle_missingApiKeyClientSecret_throwsError() {
        // Given
        ApiKeyDto apiKeyDto = new ApiKeyDto("3572db2e-486b-480a-995b-2e4d2b9104fa", "");

        // When
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectError().verify();
    }

    @Test
    void handle_invalidFormatApiKey_throwsError() {
        // Given
        ApiKeyDto apiKeyDto = new ApiKeyDto(TestUtilities.invalidApiKeyClientIdStr, TestUtilities.invalidApiKeyClientSecretStr);

        // When
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectError().verify();
    }
}