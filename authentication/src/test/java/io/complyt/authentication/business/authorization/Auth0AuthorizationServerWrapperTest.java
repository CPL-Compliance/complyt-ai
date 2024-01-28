package io.complyt.authentication.business.authorization;

import io.complyt.authentication.business.exceptions.ComplytAuth0Exception;
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

    @BeforeEach
    void setUp() {
        auth0AuthorizationServerWrapper = new Auth0AuthorizationServerWrapper(webClient, "Management Audience", "Grant Type",
                "Admin Id", "Admin Secret");
    }

    @Test
    void getAccessToken_validCredentials_ReturnsAccessToken() {
        // Given
        AccessToken accessToken = TestUtilities.createAccessToken();
        Auth0AccessToken auth0AccessToken = TestUtilities.createAuth0AccessToken();
        String clientId = "client ID";
        String clientSecret = "Client Secret";
        String audience = "Audience";
        String grantType = "Grant Type";
        String headerName = "Content-Type";
        String headerValue = "application/x-www-form-urlencoded";

        // When
        when(webClient.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri("/oauth/token")).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(headerName, headerValue)).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue("client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&audience=" + audience +
                "&grant_type=" + grantType)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<Auth0AccessToken>>notNull()))
                .thenReturn(Mono.just(auth0AccessToken));


        Mono<AccessToken> accessTokenMono = auth0AuthorizationServerWrapper.getAccessToken("client ID",
                "Client Secret", "Audience", "Grant Type");

        // Then
        StepVerifier.create(accessTokenMono).expectNext(accessToken).verifyComplete();
    }

    @Test
    void getAccessToken_Auth0ServiceIsUnavailable_is5RetriesExhausted() {
        // Given
        AccessToken accessToken = TestUtilities.createAccessToken();
        Auth0AccessToken auth0AccessToken = TestUtilities.createAuth0AccessToken();
        String clientId = "client ID";
        String clientSecret = "Client Secret";
        String audience = "Audience";
        String grantType = "Grant Type";
        String headerName = "Content-Type";
        String headerValue = "application/x-www-form-urlencoded";

        // When
        when(webClient.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri("/oauth/token")).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.header(headerName, headerValue)).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue("client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&audience=" + audience +
                "&grant_type=" + grantType)).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(Auth0AccessToken.class))
                .thenReturn(Mono.error(new Exception("Retries Exception")));

        Mono<AccessToken> accessTokenMono = auth0AuthorizationServerWrapper.getAccessToken("client ID",
                "Client Secret", "Audience", "Grant Type");

        //Then
        StepVerifier.create(accessTokenMono)
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
}