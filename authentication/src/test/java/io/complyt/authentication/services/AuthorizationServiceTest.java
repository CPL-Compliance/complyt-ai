package io.complyt.authentication.services;

import io.complyt.authentication.business.authorization.AccessToken;
import io.complyt.authentication.business.authorization.AuthorizationServerWrapper;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.security.Crypto;
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
    void getToken_credentialsIsNull_ThrowsNullException() {
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            authorizationService.getToken(null);
        });

        assertEquals(nullPointerException.getMessage(), "credentials is marked non-null but is null");
    }
}