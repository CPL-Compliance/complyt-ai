package io.complyt.authentication.business.authorization;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StubAuth0AuthorizationServerWrapperTest {

    // add test cases for getAccessToken
    @Test
    void getAccessToken_validCredentials_ReturnsAccessToken() {
        // Given
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();
        String clientId = "client ID";
        String clientSecret = "Client Secret";
        String audience = "Audience";
        String grantType = "Grant Type";
        AccessToken accessToken = TestUtilities.createStubAccessToken();

        // When
        Mono<AccessToken> accessTokenMono = auth0AuthorizationServerWrapper.getAccessToken(clientId, clientSecret, audience, grantType);

        // Then
        StepVerifier.create(accessTokenMono).expectNext(accessToken).verifyComplete();
    }

    @Test
    void getAccessToken_clientIdIsNull_ThrowNullException() {
        // Given
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();
        String clientId = null;
        String clientSecret = "Client Secret";
        String audience = "Audience";
        String grantType = "Grant Type";
        AccessToken accessToken = TestUtilities.createStubAccessToken();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getAccessToken(clientId, clientSecret, audience, grantType);
        });

        assertEquals("clientId is marked non-null but is null", exception.getMessage());
    }

    @Test
    void getAccessToken_clientSecretIsNull_ThrowNullException() {
        // Given
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();
        String clientId = "clientId";
        String clientSecret = null;
        String audience = "Audience";
        String grantType = "Grant Type";
        AccessToken accessToken = TestUtilities.createStubAccessToken();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getAccessToken(clientId, clientSecret, audience, grantType);
        });

        assertEquals("clientSecret is marked non-null but is null", exception.getMessage());
    }

    @Test
    void getAccessToken_audienceIsNull_ThrowNullException() {
        // Given
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();
        String clientId = "clientId";
        String clientSecret = "clientSecret";
        String audience = null;
        String grantType = "Grant Type";
        AccessToken accessToken = TestUtilities.createStubAccessToken();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getAccessToken(clientId, clientSecret, audience, grantType);
        });

        assertEquals("audience is marked non-null but is null", exception.getMessage());
    }

    @Test
    void getAccessToken_grantTypeIsNull_ThrowNullException() {
        // Given
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();
        String clientId = "clientId";
        String clientSecret = "clientSecret";
        String audience = "Audience";
        String grantType = null;
        AccessToken accessToken = TestUtilities.createStubAccessToken();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getAccessToken(clientId, clientSecret, audience, grantType);
        });

        assertEquals("grantType is marked non-null but is null", exception.getMessage());
    }
}