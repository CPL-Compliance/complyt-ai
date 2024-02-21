package io.complyt.authentication.facades;

import io.complyt.authentication.auth0_client.Auth0Client;
import io.complyt.authentication.business.exceptions.ComplytAuth0Exception;
import io.complyt.authentication.domain.TenantIdAndNameObject;
import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.services.ApiKeyService;
import io.complyt.authentication.services.AuthorizationService;
import io.complyt.authentication.services.CredentialsService;
import io.complyt.authentication.services.TokenService;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.unit_tests.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class ApiKeyFacadeTest {
    @InjectMocks
    ApiKeyFacade apiKeyFacade;

    @Mock
    ApiKeyService apiKeyService;

    @Mock
    CredentialsService credentialsService;

    @Mock
    TokenService tokenService;

    @Mock
    AuthorizationService authorizationService;

    Credentials credentials;

    final String tenantId = TestUtilities.tenantId;
    final String name = TestUtilities.name;

    @BeforeEach
    void setUp() {
        credentials = TestUtilities.createCredentials();
    }

    @Test
    void saveCredentials_validCredentials_returnApiKey() {
        String expectedApiKeyClientIdStr = "9a62acdf-cc85-4009-a57b-cf77c3eba1ec";
        String expectedApiKeyClientSecretStr = "3572db2e-486b-480a-995b-2e4d2b9104fa";
        ApiKey expectedApiKey = new ApiKey(expectedApiKeyClientIdStr, expectedApiKeyClientSecretStr);
        TenantIdAndNameObject tenantIdAndNameObject = new TenantIdAndNameObject(tenantId, name);

        // When
        when(apiKeyService.generate()).thenReturn(expectedApiKey);
        when(authorizationService.getTenantIdAndClientName(credentials)).thenReturn(Mono.just(tenantIdAndNameObject));
        when(credentialsService.saveCredentials(credentials, expectedApiKey, tenantId, name)).thenReturn(Mono.just(credentials));

        // Then
        Mono<ApiKey> actualApiKey = apiKeyFacade.saveCredentials(credentials);

        StepVerifier.create(actualApiKey).expectNext(expectedApiKey).verifyComplete();
    }

    @Test
    void saveCredentials_getTenantIdAndNameFailed_throwsError() {
        String expectedApiKeyClientIdStr = "9a62acdf-cc85-4009-a57b-cf77c3eba1ec";
        String expectedApiKeyClientSecretStr = "3572db2e-486b-480a-995b-2e4d2b9104fa";
        ApiKey expectedApiKey = new ApiKey(expectedApiKeyClientIdStr, expectedApiKeyClientSecretStr);

        // When
        when(apiKeyService.generate()).thenReturn(expectedApiKey);
        when(authorizationService.getTenantIdAndClientName(credentials)).thenReturn(Mono.error(new ComplytAuth0Exception()));

        // Then
        Mono<ApiKey> actualApiKey = apiKeyFacade.saveCredentials(credentials);

        StepVerifier.create(actualApiKey).expectError(ComplytAuth0Exception.class).verify();
    }

    @Test
    void markAsCancelled_validApiKey_returnCredentials()  {
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        String managementToken = TestUtilities.createManagementAccessToken().accessToken();
        Auth0Client auth0Client = TestUtilities.createAuth0Client();
        Token token = TestUtilities.createToken();

        // When
        when(credentialsService.markAsCancelled(apiKey)).thenReturn(Mono.just(credentials));
        when(authorizationService.getManagementAccessToken()).thenReturn(Mono.just(managementToken));
        when(authorizationService.deleteApiKey(credentials, managementToken)).thenReturn(Mono.just(auth0Client));
        when(tokenService.deleteToken(apiKey)).thenReturn(Mono.just(token));

        // Then
        Mono<Credentials> credentialsMono = apiKeyFacade.markAsCancelled(apiKey);

        StepVerifier.create(credentialsMono).expectNext(credentials).verifyComplete();
    }

    @Test
    void markAsCancelled_ApiKeyNotExists_returnMonoEmpty() {
        String notExistsApiKeyClientIdStr = "9a62acdf-cc85-4009-a57b-cf77c3ebnot";
        String notExistsApiKeyClientSecretStr = "3572db2e-486b-480a-995b-2e4d2b910not";
        ApiKey notExistedApiKey = new ApiKey(notExistsApiKeyClientIdStr, notExistsApiKeyClientSecretStr);

        // When
        when(credentialsService.markAsCancelled(notExistedApiKey)).thenReturn(Mono.empty());

        // Then
        Mono<Credentials> credentialsMono = apiKeyFacade.markAsCancelled(notExistedApiKey);

        StepVerifier.create(credentialsMono).verifyComplete();
    }

    @Test
    void markAsCancelled_failedRetrievingManagementAccessToken_throwsError() {
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(credentialsService.markAsCancelled(apiKey)).thenReturn(Mono.just(credentials));
        when(authorizationService.getManagementAccessToken()).thenReturn(Mono.error(new ComplytAuth0Exception()));

        // Then
        Mono<Credentials> credentialsMono = apiKeyFacade.markAsCancelled(apiKey);

        StepVerifier.create(credentialsMono).expectError(ComplytAuth0Exception.class).verify();
    }

    @Test
    void markAsCancelled_failedDeletingApiKeyFromAuth0_throwsError() {
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        String managementToken = TestUtilities.createManagementAccessToken().accessToken();

        // When
        when(credentialsService.markAsCancelled(apiKey)).thenReturn(Mono.just(credentials));
        when(authorizationService.getManagementAccessToken()).thenReturn(Mono.just(managementToken));
        when(authorizationService.deleteApiKey(credentials, managementToken)).thenReturn(Mono.error(new ComplytAuth0Exception()));
        when(tokenService.deleteToken(apiKey)).thenReturn(Mono.empty());

        // Then
        Mono<Credentials> credentialsMono = apiKeyFacade.markAsCancelled(apiKey);

        StepVerifier.create(credentialsMono).expectError(ComplytAuth0Exception.class).verify();
    }

    @Test
    void markAsCancelled_tokenNotExistInDB_returnCredentials() {
        ApiKey apiKey = TestUtilities.createApiKey();
        Credentials credentials = TestUtilities.createCredentials();
        String managementToken = TestUtilities.createManagementAccessToken().accessToken();
        Auth0Client auth0Client = TestUtilities.createAuth0Client();

        // When
        when(credentialsService.markAsCancelled(apiKey)).thenReturn(Mono.just(credentials));
        when(authorizationService.getManagementAccessToken()).thenReturn(Mono.just(managementToken));
        when(authorizationService.deleteApiKey(credentials, managementToken)).thenReturn(Mono.just(auth0Client));
        when(tokenService.deleteToken(apiKey)).thenReturn(Mono.empty());

        // Then
        Mono<Credentials> credentialsMono = apiKeyFacade.markAsCancelled(apiKey);

        StepVerifier.create(credentialsMono).expectNext(credentials).verifyComplete();
    }

    @Test
    void saveCredentials_credentialsIsNull_throwNullException() {
        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            apiKeyFacade.saveCredentials(null);
        });

        assertEquals(nullPointerException.getMessage(), "credentials is marked non-null but is null");
    }

    @Test
    void markAsCancelled_apiKeyIsNull_throwNullException() {
        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            apiKeyFacade.markAsCancelled(null);
        });

        assertEquals(nullPointerException.getMessage(), "apiKey is marked non-null but is null");
    }
}