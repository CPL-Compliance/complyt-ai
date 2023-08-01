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

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
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

//    @Test
//    void getAccessToken_validCredentials_ReturnsAccessToken() {
//        // Given
//        AccessToken accessToken = TestUtilities.createAccessToken();
//
//        // When
//        when(webClient.post()).thenReturn(requestBodyUriSpecMock);
//
//        when(requestBodyUriSpecMock.uri("server-url")).thenReturn(requestBodyUriSpecMock);
//        when(requestBodySpecMock.header(any(),any())).thenReturn(requestBodySpecMock);
//        when(requestHeadersUriSpecMock.header(any(),any())).thenReturn(requestHeadersUriSpecMock);
//
//        when(requestBodySpecMock.accept(any())).thenReturn(requestBodySpecMock);
//        when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<AccessToken>>notNull()))
//                .thenReturn(Mono.just(accessToken));
//
//        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
//
//        Mono<AccessToken> accessTokenMono = auth0AuthorizationServerWrapper.getAccessToken("client ID",
//                "Client Secret", "Audience", "Grant Type");
//
//        // Then
//        StepVerifier.create(accessTokenMono).expectNext(accessToken).verifyComplete();
//    }
}