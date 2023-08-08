package io.complyt.authentication.business.authorization;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

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
}