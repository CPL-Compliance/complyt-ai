package io.complyt.authentication.v1.validators;

import io.complyt.authentication.v1.exceptions.types.QueryParamErrorException;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.CredentialsDto;
import io.complyt.authentication.v1.validators.query_params.QueryParamsExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.unit_tests.TestUtilities;

import java.util.Optional;

import static org.mockito.Mockito.mock;
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
    ParameterChecksProvider queryParamChecksProvider;

    @MockBean
    QueryParamsExtractor<ApiKeyDto> queryParamsExtractor;

    @MockBean
    ServerRequest serverRequest;

    @MockBean
    ShouldCallValidate shouldCallValidate;

    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

    @Test
    void handle_validCredentials_returnCredentialsDto() {
        // Given
        CredentialsDto credentialsDto = TestUtilities.createCredentialsDto();
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        // When
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));
        when(serverRequest.bodyToMono(CredentialsDto.class)).thenReturn(Mono.just(credentialsDto));

        Mono<CredentialsDto> credentialsDtoMono = credentialsDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(credentialsDtoMono).expectNext(credentialsDto).verifyComplete();
    }

    @Test
    void handle_notValidCredentialsMissingClientId_returnCredentialsDto() {
        // Given
        CredentialsDto credentialsDto = TestUtilities.createCredentialsDtoMissingClientId();
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        // When
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));
        when(serverRequest.bodyToMono(CredentialsDto.class)).thenReturn(Mono.just(credentialsDto));


        Mono<CredentialsDto> credentialsDtoMono = credentialsDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(credentialsDtoMono).expectError().verify();
    }

    @Test
    void handle_notValidCredentialsBlankClientId_returnCredentialsDto() {
        // Given
        CredentialsDto credentialsDto = TestUtilities.createCredentialsDtoBlankClientId();
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        // When
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));
        when(serverRequest.bodyToMono(CredentialsDto.class)).thenReturn(Mono.just(credentialsDto));


        Mono<CredentialsDto> credentialsDtoMono = credentialsDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(credentialsDtoMono).expectError().verify();
    }

    @Test
    void handle_notValidCredentialsMissingClientSecret_returnCredentialsDto() {
        // Given
        CredentialsDto credentialsDto = TestUtilities.createCredentialsDtoMissingClientSecret();
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        // When
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));
        when(serverRequest.bodyToMono(CredentialsDto.class)).thenReturn(Mono.just(credentialsDto));

        Mono<CredentialsDto> credentialsDtoMono = credentialsDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(credentialsDtoMono).expectError().verify();
    }

    @Test
    void handle_notValidCredentialsBlankClientSecret_returnCredentialsDto() {
        // Given
        CredentialsDto credentialsDto = TestUtilities.createCredentialsDtoBlankClientSecret();
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        // When
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));
        when(serverRequest.bodyToMono(CredentialsDto.class)).thenReturn(Mono.just(credentialsDto));

        Mono<CredentialsDto> credentialsDtoMono = credentialsDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(credentialsDtoMono).expectError().verify();
    }

    @Test
    void handle_jsonTypeValidApiKey_returnApiKeyDto() {
        // Given
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();

        // When
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);
        when(serverRequest.headers()).thenReturn(headersMock);
        when(serverRequest.headers().contentType()).thenReturn(Optional.of(MediaType.APPLICATION_JSON));
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));

        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectNext(apiKeyDto).verifyComplete();
    }
    @Test
    void handle_urlEncodedTypeValidApiKey_returnApiKeyDto() {
        // Given
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("clientId", apiKeyDto.clientId());
        formData.add("clientSecret", apiKeyDto.clientSecret());
        // When
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        when(serverRequest.formData()).thenReturn(Mono.just(formData));
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);
        when(serverRequest.headers()).thenReturn(headersMock);
        when(serverRequest.headers().contentType()).thenReturn(Optional.of(MediaType.APPLICATION_FORM_URLENCODED));
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));

        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectNext(apiKeyDto).verifyComplete();
    }
    @Test
    void handle_jsonTypeMissingApiKeyClientId_throwsError() {
        // Given
        ApiKeyDto apiKeyDto = new ApiKeyDto("", "3572db2e-486b-480a-995b-2e4d2b9104fa");

        // When
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);
        when(serverRequest.headers()).thenReturn(headersMock);
        when(serverRequest.headers().contentType()).thenReturn(Optional.of(MediaType.APPLICATION_JSON));
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));


        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectError().verify();
    }
    @Test
    void handle_urlEncodedTypeMissingApiKeyClientId_throwsError() {
        // Given
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("clientId", "");
        formData.add("clientSecret", "3572db2e-486b-480a-995b-2e4d2b9104fa");
        ApiKeyDto apiKeyDto = new ApiKeyDto("", "3572db2e-486b-480a-995b-2e4d2b9104fa");

        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        when(serverRequest.formData()).thenReturn(Mono.just(formData));
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);
        when(serverRequest.headers()).thenReturn(headersMock);
        when(serverRequest.headers().contentType()).thenReturn(Optional.of(MediaType.APPLICATION_FORM_URLENCODED));
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));


        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectError().verify();
    }
    @Test
    void handle_jsonTypeMissingApiKeyClientSecret_throwsError() {
        // Given
        ApiKeyDto apiKeyDto = new ApiKeyDto("3572db2e-486b-480a-995b-2e4d2b9104fa", "");

        // When
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);
        when(serverRequest.headers()).thenReturn(headersMock);
        when(serverRequest.headers().contentType()).thenReturn(Optional.of(MediaType.APPLICATION_JSON));
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));


        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectError().verify();
    }

    @Test
    void handle_urlEncodedTypeMissingApiKeyClientSecret_throwsError() {

        // When
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("clientId", "3572db2e-486b-480a-995b-2e4d2b9104fa");
        formData.add("clientSecret", "");
        ApiKeyDto apiKeyDto = new ApiKeyDto("3572db2e-486b-480a-995b-2e4d2b9104fa", "");

        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        when(serverRequest.formData()).thenReturn(Mono.just(formData));
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);
        when(serverRequest.headers()).thenReturn(headersMock);
        when(serverRequest.headers().contentType()).thenReturn(Optional.of(MediaType.APPLICATION_FORM_URLENCODED));
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));


        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectError().verify();
    }

    @Test
    void handle_jsonTypeInvalidFormatApiKey_throwsError() {
        // Given
        ApiKeyDto apiKeyDto = new ApiKeyDto(TestUtilities.invalidApiKeyClientIdStr, TestUtilities.invalidApiKeyClientSecretStr);

        // When
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);
        when(serverRequest.headers()).thenReturn(headersMock);
        when(serverRequest.headers().contentType()).thenReturn(Optional.of(MediaType.APPLICATION_JSON));
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));

        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectError().verify();
    }
    @Test
    void handle_urlEncodedTypeInvalidFormatApiKey_throwsError() {
        // Given
        ApiKeyDto apiKeyDto = new ApiKeyDto(TestUtilities.invalidApiKeyClientIdStr, TestUtilities.invalidApiKeyClientSecretStr);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("clientId", apiKeyDto.clientId());
        formData.add("clientSecret", apiKeyDto.clientSecret());

        // When
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);
        when(serverRequest.formData()).thenReturn(Mono.just(formData));
        when(serverRequest.headers()).thenReturn(headersMock);
        when(serverRequest.headers().contentType()).thenReturn(Optional.of(MediaType.APPLICATION_FORM_URLENCODED));
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(apiKeyDto));

        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectError().verify();
    }

    @Test
    void handle_urlEncodedTypeSentWithoutContentTypeHeader_throwsError() {
        // Given
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("clientId", apiKeyDto.clientId());
        formData.add("clientSecret", apiKeyDto.clientSecret());

        // When
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);
        when(serverRequest.formData()).thenReturn(Mono.just(formData));
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.error(new UnsupportedMediaTypeException("error")));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);

        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectError().verify();
    }
    @Test
    void handle_noContentTypeHeader_throwsError() {
        // When
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);
        when(serverRequest.headers()).thenReturn(headersMock);
        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.error(new UnsupportedMediaTypeException("error")));
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.POST);
        when(serverRequest.path()).thenReturn("/v1/api_key");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);

        Mono<ApiKeyDto> apiKeyDtoMono = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(apiKeyDtoMono).expectError().verify();
    }

    @Test
    void validateQueryParam_ErrorListNotEmpty_ThrowsException() {
        // Given
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("tenantId", "");

        when(queryParamChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.queryParams()).thenReturn(map);
        when(queryParamChecksProvider.getFunctionCheck("key")).thenReturn(Mono.just(param -> Mono.empty())); // Simulate validation failure

        // When
        Mono<ApiKeyDto> validationResult = apiKeyDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(validationResult)
                .expectError(QueryParamErrorException.class)
                .verify();
    }
}