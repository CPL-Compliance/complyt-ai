package io.complyt.authentication.business.authorization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Auth0AuthorizationServerWrapperTest {
    //    @InjectMocks
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
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        auth0AuthorizationServerWrapper = new Auth0AuthorizationServerWrapper(webClient);
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
}