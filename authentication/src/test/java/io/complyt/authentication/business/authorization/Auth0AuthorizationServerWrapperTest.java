package io.complyt.authentication.business.authorization;

import io.complyt.authentication.auth0_client.Auth0Client;
import io.complyt.authentication.business.exceptions.ComplytAuth0Exception;
import io.complyt.authentication.domain.TenantIdAndNameObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.unit_tests.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Auth0AuthorizationServerWrapperTest {
    Auth0AuthorizationServerWrapper auth0AuthorizationServerWrapper;

    @Mock
    WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    WebClient.RequestBodySpec requestBodySpecMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @Mock
    private  WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    AccessToken accessToken = TestUtilities.createAccessToken();
    Auth0AccessToken auth0AccessToken = TestUtilities.createAuth0AccessToken();
    String clientId = "client ID";
    String clientSecret = "Client Secret";
    String adminId = "Admin Id";
    String adminSecret = "Admin Secret";
    String audience = "Audience";
    String managementAudience = "Management Audience";
    String managementToken = "management Access Token";
    String grantType = "Grant Type";
    String contentTypeHeaderName = "Content-Type";
    String contentTypeHeaderValue = "application/x-www-form-urlencoded";
    String authorizationHeaderName = "Authorization";
    String authorizationHeaderValue = "Bearer " + managementToken;
    String clientName = "client Name";
    String tenantId = "tenant ID";
    String newClientId = "New Client ID";
    String newClientSecret = "New Client Secret";

    @BeforeEach
    void setUp() {
        auth0AuthorizationServerWrapper = new Auth0AuthorizationServerWrapper(webClient, "Management Audience", "Grant Type",
                "Admin Id", "Admin Secret");
    }

    @Test
    void getAccessToken_validCredentials_ReturnsAccessToken() {
        // When
        when(webClient.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri("/oauth/token")).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(contentTypeHeaderName, contentTypeHeaderValue)).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue("client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&audience=" + audience +
                "&grant_type=" + grantType)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<Auth0AccessToken>>notNull()))
                .thenReturn(Mono.just(auth0AccessToken));


        Mono<AccessToken> accessTokenMono = auth0AuthorizationServerWrapper.getAccessToken(clientId,
                clientSecret, audience, grantType);

        // Then
        StepVerifier.create(accessTokenMono).expectNext(accessToken).verifyComplete();
    }

    @Test
    void getAccessToken_Auth0ServiceIsUnavailable_is5RetriesExhausted() {
        // When
        when(webClient.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri("/oauth/token")).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(contentTypeHeaderName, contentTypeHeaderValue)).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue("client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&audience=" + audience +
                "&grant_type=" + grantType)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Auth0AccessToken.class))
                .thenReturn(Mono.error(new Exception("Retries Exception")));

        Mono<AccessToken> accessTokenMono = auth0AuthorizationServerWrapper.getAccessToken(clientId,
                clientSecret, audience, grantType);

        //Then
        StepVerifier.create(accessTokenMono)
                .expectErrorMatches(
                        throwable -> throwable instanceof ComplytAuth0Exception
                                && throwable.getMessage().equals("5 Retries Exhausted"))
                .verify();
    }

    @Test
    void getManagementAccessToken_validCredentials_ReturnsAccessToken() {
        // When
        when(webClient.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri("/oauth/token")).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.header(contentTypeHeaderName, contentTypeHeaderValue)).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue("client_id=" + adminId +
                "&client_secret=" + adminSecret +
                "&audience=" + managementAudience +
                "&grant_type=" + grantType)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<Auth0AccessToken>>notNull()))
                .thenReturn(Mono.just(auth0AccessToken));


        Mono<AccessToken> accessTokenMono = auth0AuthorizationServerWrapper.getManagementAccessToken();

        // Then
        StepVerifier.create(accessTokenMono).expectNext(accessToken).verifyComplete();
    }

    @Test
    void getManagementAccessToken_Auth0ServiceIsUnavailable_is5RetriesExhausted() {
        // When
        when(webClient.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri("/oauth/token")).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(contentTypeHeaderName, contentTypeHeaderValue)).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue("client_id=" + adminId +
                "&client_secret=" + adminSecret +
                "&audience=" + managementAudience +
                "&grant_type=" + grantType)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Auth0AccessToken.class))
                .thenReturn(Mono.error(new Exception("Retries Exception")));

        Mono<AccessToken> accessTokenMono = auth0AuthorizationServerWrapper.getManagementAccessToken();

        //Then
        StepVerifier.create(accessTokenMono)
                .expectErrorMatches(
                        throwable -> throwable instanceof ComplytAuth0Exception
                                && throwable.getMessage().equals("5 Retries Exhausted"))
                .verify();
    }

    @Test
    void removeApiKeyFromClient_validCredentials_ReturnsAuth0Client() {
        // Given
        String contentTypeHeaderValue = "application/json";
        Auth0Client auth0Client = TestUtilities.createAuth0Client();

        String json = "{ \"name\": \"" + clientName +
                "\", \"client_metadata\": { \"tenant_id\": \"" + tenantId +
                "\", \"clientId\": " + newClientId + ", \"clientSecret\": " + newClientSecret + " } }";

        // When
        when(webClient.patch()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri("/api/v2/clients/" + clientId)).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(contentTypeHeaderName, contentTypeHeaderValue)).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(authorizationHeaderName, authorizationHeaderValue)).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(json)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<Auth0Client>>notNull()))
                .thenReturn(Mono.just(auth0Client));

        Mono<Auth0Client> auth0ClientMono = auth0AuthorizationServerWrapper.removeApiKeyFromClient(clientName,
                clientId, tenantId, managementToken, newClientId, newClientSecret);

        // Then
        StepVerifier.create(auth0ClientMono).expectNext(auth0Client).verifyComplete();
    }

    @Test
    void removeApiKeyFromClient_Auth0ServiceIsUnavailable_is5RetriesExhausted() {
        // Given
        String contentTypeHeaderValue = "application/json";

        String json = "{ \"name\": \"" + clientName +
                "\", \"client_metadata\": { \"tenant_id\": \"" + tenantId +
                "\", \"clientId\": " + newClientId + ", \"clientSecret\": " + newClientSecret + " } }";

        // When
        when(webClient.patch()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri("/api/v2/clients/" + clientId)).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(contentTypeHeaderName, contentTypeHeaderValue)).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(authorizationHeaderName, authorizationHeaderValue)).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(json)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Auth0Client.class))
                .thenReturn(Mono.error(new Exception("Retries Exception")));

        Mono<Auth0Client> auth0ClientMono = auth0AuthorizationServerWrapper.removeApiKeyFromClient(clientName,
                clientId, tenantId, managementToken, newClientId, newClientSecret);

        // Then
        StepVerifier.create(auth0ClientMono)
                .expectErrorMatches(
                        throwable -> throwable instanceof ComplytAuth0Exception
                                && throwable.getMessage().equals("5 Retries Exhausted"))
                .verify();
    }

    @Test
    void removeApiKeyFromClient_newClientIdIsNull_ReturnsAuth0Client() {
        // Given
        String newClientId = null;
        String contentTypeHeaderValue = "application/json";

        Auth0Client auth0Client = TestUtilities.createAuth0Client();

        String json = "{ \"name\": \"" + clientName +
                "\", \"client_metadata\": { \"tenant_id\": \"" + tenantId +
                "\", \"clientId\": " + newClientId + ", \"clientSecret\": " + newClientSecret + " } }";

        // When
        when(webClient.patch()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri("/api/v2/clients/" + clientId)).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(contentTypeHeaderName, contentTypeHeaderValue)).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(authorizationHeaderName, authorizationHeaderValue)).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(json)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<Auth0Client>>notNull()))
                .thenReturn(Mono.just(auth0Client));

        Mono<Auth0Client> auth0ClientMono = auth0AuthorizationServerWrapper.removeApiKeyFromClient(clientName,
                clientId, tenantId, managementToken, newClientId, newClientSecret);

        // Then
        StepVerifier.create(auth0ClientMono).expectNext(auth0Client).verifyComplete();
    }

    @Test
    void removeApiKeyFromClient_newClientSecretIsNull_ReturnsAuth0Client() {
        // Given
        String newClientSecret = null;
        String contentTypeHeaderValue = "application/json";
        Auth0Client auth0Client = TestUtilities.createAuth0Client();

        String json = "{ \"name\": \"" + clientName +
                "\", \"client_metadata\": { \"tenant_id\": \"" + tenantId +
                "\", \"clientId\": " + newClientId + ", \"clientSecret\": " + newClientSecret + " } }";

        // When
        when(webClient.patch()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri("/api/v2/clients/" + clientId)).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(contentTypeHeaderName, contentTypeHeaderValue)).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(authorizationHeaderName, authorizationHeaderValue)).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(json)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<Auth0Client>>notNull()))
                .thenReturn(Mono.just(auth0Client));

        Mono<Auth0Client> auth0ClientMono = auth0AuthorizationServerWrapper.removeApiKeyFromClient(clientName,
                clientId, tenantId, managementToken, newClientId, newClientSecret);

        // Then
        StepVerifier.create(auth0ClientMono).expectNext(auth0Client).verifyComplete();
    }

    @Test
    void getTenantIdAndClientNameFromAuth0_validInputs_ReturnsTenantIdAndClientObject() {
        // Given
        Auth0Client auth0Client = TestUtilities.createAuth0Client();
        TenantIdAndNameObject tenantIdAndNameObject = new TenantIdAndNameObject(auth0Client.client_metadata().getTenant_id(), auth0Client.name());

        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri("/api/v2/clients/" + clientId)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(contentTypeHeaderName, contentTypeHeaderValue)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(authorizationHeaderName, authorizationHeaderValue)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<Auth0Client>>notNull())).thenReturn(Mono.just(auth0Client));

        Mono<TenantIdAndNameObject> tenantIdAndNameObjectMono = auth0AuthorizationServerWrapper.getTenantIdAndClientNameFromAuth0(clientId, managementToken);

        // Then
        StepVerifier.create(tenantIdAndNameObjectMono).expectNext(tenantIdAndNameObject).verifyComplete();
    }

    @Test
    void getTenantIdAndClientNameFromAuth0_Auth0ServiceIsUnavailable_is5RetriesExhausted() {
        // When
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri("/api/v2/clients/" + clientId)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(contentTypeHeaderName, contentTypeHeaderValue)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.header(authorizationHeaderName, authorizationHeaderValue)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Auth0Client.class))
                .thenReturn(Mono.error(new Exception("Retries Exception")));

        Mono<TenantIdAndNameObject> tenantIdAndNameObjectMono = auth0AuthorizationServerWrapper.getTenantIdAndClientNameFromAuth0(clientId, managementToken);

        // Then
        StepVerifier.create(tenantIdAndNameObjectMono)
                .expectErrorMatches(
                        throwable -> throwable instanceof ComplytAuth0Exception
                                && throwable.getMessage().equals("5 Retries Exhausted"))
                .verify();
    }

    @Test
    void getAccessToken_clientIdIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getAccessToken(null,
                    "Client Secret", "Audience", "Grant Type");
        });

        assertEquals(nullPointerException.getMessage(), "clientId is marked non-null but is null");
    }


    @Test
    void getAccessToken_clientSecretIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getAccessToken("client ID",
                    null, "Audience", "Grant Type");
        });

        assertEquals(nullPointerException.getMessage(), "clientSecret is marked non-null but is null");
    }

    @Test
    void getAccessToken_audienceIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getAccessToken("Client ID",
                    "Client Secret", null, "Grant Type");
        });

        assertEquals(nullPointerException.getMessage(), "audience is marked non-null but is null");
    }

    @Test
    void getAccessToken_grantTypeIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getAccessToken("Client ID",
                    "Client Secret", "audience", null);
        });

        assertEquals(nullPointerException.getMessage(), "grantType is marked non-null but is null");
    }

    @Test
    void removeApiKeyFromClient_clientNameIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.removeApiKeyFromClient(null,
                    "client ID", "tenant ID", "management Access Token", "New Client ID", "New Client Secret");
        });

        assertEquals(nullPointerException.getMessage(), "clientName is marked non-null but is null");
    }

    @Test
    void removeApiKeyFromClient_clientIdIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.removeApiKeyFromClient("client Name",
                    null, "tenant ID", "management Access Token", "New Client ID", "New Client Secret");
        });

        assertEquals(nullPointerException.getMessage(), "clientId is marked non-null but is null");
    }

    @Test
    void removeApiKeyFromClient_tenantIdIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.removeApiKeyFromClient("client Name",
                    "client ID", null, "management Access Token", "New Client ID", "New Client Secret");
        });

        assertEquals(nullPointerException.getMessage(), "tenantId is marked non-null but is null");
    }

    @Test
    void removeApiKeyFromClient_managementAccessTokenIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.removeApiKeyFromClient("client Name",
                    "client ID", "tenant ID", null, "New Client ID", "New Client Secret");
        });

        assertEquals(nullPointerException.getMessage(), "accessToken is marked non-null but is null");
    }

    @Test
    void getTenantIdAndClientNameFromAuth0_clientIdIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getTenantIdAndClientNameFromAuth0(null, "management Access Token");
        });

        assertEquals(nullPointerException.getMessage(), "clientId is marked non-null but is null");
    }

    @Test
    void getTenantIdAndClientNameFromAuth0_AccessTokenIsNull_throwsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getTenantIdAndClientNameFromAuth0("client ID", null);
        });

        assertEquals(nullPointerException.getMessage(), "accessToken is marked non-null but is null");
    }
}