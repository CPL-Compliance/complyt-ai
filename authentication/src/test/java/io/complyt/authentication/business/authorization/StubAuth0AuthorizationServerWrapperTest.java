package io.complyt.authentication.business.authorization;

import io.complyt.authentication.auth0_client.Auth0Client;
import io.complyt.authentication.domain.TenantIdAndNameObject;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.unit_tests.TestUtilities;

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


    @Test
    void getManagementAccessToken_validCredentials_ReturnsManagementAccessToken() {
        // Given
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();
        AccessToken stubManagementAccessTokenToken = TestUtilities.createStubManagementAccessToken();

        // When
        Mono<AccessToken> accessTokenMono = auth0AuthorizationServerWrapper.getManagementAccessToken();

        // Then
        StepVerifier.create(accessTokenMono).expectNext(stubManagementAccessTokenToken).verifyComplete();
    }

    @Test
    void getTenantIdAndClientNameFromAuth0_validCredentials_ReturnsTenantIdAndNameObject() {
        // Given
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();
        String clientId = "Client ID";
        AccessToken managementToken = TestUtilities.createManagementAccessToken();
        TenantIdAndNameObject expectedTenantIdAndClientObject = new TenantIdAndNameObject("test_tenant", "test_name");

        // When
        Mono<TenantIdAndNameObject> tenantIdAndNameObjectMono = auth0AuthorizationServerWrapper.getTenantIdAndClientNameFromAuth0(clientId, managementToken.accessToken());

        // Then
        StepVerifier.create(tenantIdAndNameObjectMono).expectNext(expectedTenantIdAndClientObject).verifyComplete();
    }

    @Test
    void getTenantIdAndClientNameFromAuth0_clientIdIsNull_ThrowNullException() {
        // Given
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();
        String clientId = null;
        AccessToken managementToken = TestUtilities.createManagementAccessToken();


        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getTenantIdAndClientNameFromAuth0(clientId, managementToken.accessToken());
        });

        assertEquals("clientId is marked non-null but is null", exception.getMessage());
    }

    @Test
    void getTenantIdAndClientNameFromAuth0_managementAccessTokenIsNull_ThrowNullException() {
        // Given
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();
        String clientId = "Client ID";


        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.getTenantIdAndClientNameFromAuth0(clientId, null);
        });

        assertEquals("accessToken is marked non-null but is null", exception.getMessage());
    }

    @Test
    void removeApiKeyFromClient_validCredentials_ReturnsAuth0Client() {
        // Given
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();
        String clientName = "client Name";
        String clientId = "client ID";
        String tenantId = "tenant ID";
        String managementToken = "management Access Token";
        String newClientId = "New Client ID";
        String newClientSecret = "New Client Secret";
        Auth0Client expectedAuth0client = TestUtilities.createAuth0Client();

        // When
        Mono<Auth0Client> auth0ClientMono = auth0AuthorizationServerWrapper.updateApiKeyFromClient(clientName, clientId, tenantId, managementToken, newClientId, newClientSecret);

        // Then
        StepVerifier.create(auth0ClientMono).expectNext(expectedAuth0client).verifyComplete();
    }

    @Test
    void removeApiKeyFromClient_clientNameIsNull_throwsNullException() {
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.updateApiKeyFromClient(null,
                    "client ID", "tenant ID", "management Access Token", "New Client ID", "New Client Secret");
        });

        assertEquals(nullPointerException.getMessage(), "clientName is marked non-null but is null");
    }

    @Test
    void removeApiKeyFromClient_clientIdIsNull_throwsNullException() {
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.updateApiKeyFromClient("client Name",
                    null, "tenant ID", "management Access Token", "New Client ID", "New Client Secret");
        });

        assertEquals(nullPointerException.getMessage(), "clientId is marked non-null but is null");
    }

    @Test
    void removeApiKeyFromClient_tenantIdIsNull_throwsNullException() {
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.updateApiKeyFromClient("client Name",
                    "client ID", null, "management Access Token", "New Client ID", "New Client Secret");
        });

        assertEquals(nullPointerException.getMessage(), "tenantId is marked non-null but is null");
    }

    @Test
    void removeApiKeyFromClient_managementAccessTokenIsNull_throwsNullException() {
        StubAuth0AuthorizationServerWrapper auth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            auth0AuthorizationServerWrapper.updateApiKeyFromClient("client Name",
                    "client ID", "tenant ID", null, "New Client ID", "New Client Secret");
        });

        assertEquals(nullPointerException.getMessage(), "accessToken is marked non-null but is null");
    }
}