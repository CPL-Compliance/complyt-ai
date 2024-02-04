package io.complyt.authentication.services;

import io.complyt.authentication.business.authorization.AccessToken;
import io.complyt.authentication.auth0_client.Auth0Client;
import io.complyt.authentication.business.authorization.AuthorizationServerWrapper;
import io.complyt.authentication.business.exceptions.ComplytAuth0Exception;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.TenantIdAndNameObject;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.security.Crypto;
import io.complyt.authentication.security.EncryptedData;
import lombok.experimental.FieldDefaults;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class AuthorizationServiceTest {
    @InjectMocks
    AuthorizationService authorizationService;

    @Mock
    AuthorizationServerWrapper authorizationServerWrapper;

    @Mock
    Crypto cryptoAesGcmNoPadding;

    @Test
    void getToken_validCredentials_returnToken() {
        // Given
        Credentials credentials = TestUtilities.createCredentials();
        AccessToken accessToken = TestUtilities.createAccessToken();
        Token expectedToken = TestUtilities.createToken(credentials, accessToken);
        final Token expectedTokenWithAccessToken = expectedToken.withAccessToken("Access Token");

        // When
        when(authorizationServerWrapper.getAccessToken(credentials.getClientId(), credentials.getClientSecret(),
                credentials.getAudience(), credentials.getGrantType())).thenReturn(Mono.just(accessToken));

        Mono<Token> actualToken = authorizationService.getToken(credentials);

        // Then
        StepVerifier
                .create(actualToken)
                .expectNextMatches(
                        token -> token.getAccessToken().equals(expectedTokenWithAccessToken.getAccessToken()) &&
                                token.getComplytClientId().equals(expectedTokenWithAccessToken.getComplytClientId()) &&
                                token.getCreatedAt().isAfter(expectedTokenWithAccessToken.getCreatedAt()) &&
                                token.getExpiresIn() == expectedTokenWithAccessToken.getExpiresIn() &&
                                token.getComplytClientSecret().equals(expectedTokenWithAccessToken.getComplytClientSecret()))
                .verifyComplete();
    }

    @Test
    void getTenantIdAndClientName_validCredentials_returnTenantIdAndNameObject() {
        // Given
        AccessToken managementToken = TestUtilities.createManagementAccessToken();
        Credentials credentials = TestUtilities.createCredentials();
        TenantIdAndNameObject tenantIdAndNameObject = TestUtilities.createTenantIdAndNameObject(credentials);

        // When
        when(authorizationServerWrapper.getManagementAccessToken()).thenReturn(Mono.just(managementToken));
        when(authorizationServerWrapper.getTenantIdAndClientNameFromAuth0(credentials.getClientId(), managementToken.accessToken())).thenReturn(Mono.just(tenantIdAndNameObject));

        // Then
        Mono<TenantIdAndNameObject> tenantIdAndNameObjectMono = authorizationService.getTenantIdAndClientName(credentials);
        StepVerifier.create(tenantIdAndNameObjectMono).expectNext(tenantIdAndNameObject).verifyComplete();
    }

    @Test
    void getTenantIdAndClientName_failedToGetManagementAccessToken_throwError() {
        // Given
        Credentials credentials = TestUtilities.createCredentials();

        // When
        when(authorizationServerWrapper.getManagementAccessToken()).thenReturn(Mono.error(new ComplytAuth0Exception()));

        // Then
        Mono<TenantIdAndNameObject> managementTokenMono = authorizationService.getTenantIdAndClientName(credentials);
        StepVerifier.create(managementTokenMono).expectError(ComplytAuth0Exception.class).verify();
    }

    @Test
    void getManagementAccessToken_validFunction_returnStringManagementAccessToken() {
        // Given
        AccessToken managementToken = TestUtilities.createManagementAccessToken();
        String accessToken = managementToken.accessToken();

        // When
        when(authorizationServerWrapper.getManagementAccessToken()).thenReturn(Mono.just(managementToken));

        // Then
        Mono<String> managementTokenMono = authorizationService.getManagementAccessToken();
        StepVerifier.create(managementTokenMono).expectNext(accessToken).verifyComplete();
    }


    @Test
    void getManagementAccessToken_failedToGetManagementAccessToken_throwError() {
        // When
        when(authorizationServerWrapper.getManagementAccessToken()).thenReturn(Mono.error(new ComplytAuth0Exception()));

        // Then
        Mono<String> managementTokenMono = authorizationService.getManagementAccessToken();
        StepVerifier.create(managementTokenMono).expectError(ComplytAuth0Exception.class).verify();
    }

    @Test
    void deleteApiKey_SuccessfulDeletion_ReturnsAuth0ClientMono() throws Exception {
        // Given
        Credentials credentials = TestUtilities.createCredentials();
        String managementToken = TestUtilities.createManagementAccessToken().accessToken();
        EncryptedData encryptedClientId  = TestUtilities.createEncryptedClientId(credentials);
        Auth0Client expectedAuth0Client = TestUtilities.createAuth0Client();

        // When
        when(cryptoAesGcmNoPadding.decrypt(encryptedClientId)).thenReturn("decryptedClientId");
        when(authorizationServerWrapper.removeApiKeyFromClient(
                credentials.getName(), "decryptedClientId", credentials.getTenantId(), managementToken, null, null))
                .thenReturn(Mono.just(expectedAuth0Client));

        // Then
        Mono<Auth0Client> resultMono = authorizationService.deleteApiKey(credentials, managementToken);
        StepVerifier.create(resultMono).expectNext(expectedAuth0Client).verifyComplete();
    }

    @Test
    void getToken_credentialsIsNull_ThrowsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            authorizationService.getToken(null);
        });

        assertEquals(nullPointerException.getMessage(), "credentials is marked non-null but is null");
    }

    @Test
    void getTenantIdAndClientName_credentialsIsNull_ThrowsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            authorizationService.getTenantIdAndClientName(null);
        });

        assertEquals(nullPointerException.getMessage(), "credentials is marked non-null but is null");
    }

    @Test
    void deleteApiKey_credentialsIsNull_ThrowsNullException() {
        String managementToken = TestUtilities.createManagementAccessToken().accessToken();

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            authorizationService.deleteApiKey(null, managementToken);
        });

        assertEquals(nullPointerException.getMessage(), "credentials is marked non-null but is null");
    }

    @Test
    void deleteApiKey_managementAccessTokenIsNull_ThrowsNullException() {
        Credentials credentials = TestUtilities.createCredentials();

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            authorizationService.deleteApiKey(credentials, null);
        });

        assertEquals(nullPointerException.getMessage(), "accessToken is marked non-null but is null");
    }
}