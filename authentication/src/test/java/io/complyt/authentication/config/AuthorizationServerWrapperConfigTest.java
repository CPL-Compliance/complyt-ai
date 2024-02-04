package io.complyt.authentication.config;

import io.complyt.authentication.business.authorization.Auth0AuthorizationServerWrapper;
import io.complyt.authentication.business.authorization.AuthorizationServerWrapper;
import io.complyt.authentication.business.authorization.StubAuth0AuthorizationServerWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;
import test_utils.unit_tests.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AuthorizationServerWrapperConfigTest {
    AuthorizationServerWrapperConfig authorizationServerWrapperConfig;

    @Mock
    WebClient webClient;

    @BeforeEach
    void setUp() {
        authorizationServerWrapperConfig = new AuthorizationServerWrapperConfig();
    }

    @Test
    void authorizationServerWrapper_createAuth0AuthorizationServerWrapper_getAuth0AuthorizationServerWrapper() {
        WebClient webClient = WebClient.builder().build();

        Auth0AuthorizationServerWrapper expectedAuth0AuthorizationServerWrapper = new Auth0AuthorizationServerWrapper(webClient,
                "Management Audience", "Grant Type", "Admin Id", "Admin Secret");
        AuthorizationServerWrapper actualAuth0AuthorizationServerWrapper = authorizationServerWrapperConfig.auth0AuthorizationServerWrapper(webClient,
                "Management Audience", "Grant Type", "Admin Id", "Admin Secret");

        assertEquals(expectedAuth0AuthorizationServerWrapper, actualAuth0AuthorizationServerWrapper);
    }

    @Test
    void authorizationServerWrapper_createStubAuth0AuthorizationServerWrapper_getStubAuth0AuthorizationServerWrapper() {
        StubAuth0AuthorizationServerWrapper expectedStubAuth0AuthorizationServerWrapper = new StubAuth0AuthorizationServerWrapper();
        AuthorizationServerWrapper actualAuth0AuthorizationServerWrapper = authorizationServerWrapperConfig.stubAuth0AuthorizationServerWrapper();

        assertEquals(expectedStubAuth0AuthorizationServerWrapper, actualAuth0AuthorizationServerWrapper);
    }

    @Test
    void authorizationServerWrapper_webClientIsNull_throwNullException() {
        // Given
        String managementAudience = "Management Audience";
        String grantType = "Grant Type";
        String adminId = "Admin Id";
        String adminSecret = "Admin Secret";

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            authorizationServerWrapperConfig.auth0AuthorizationServerWrapper(null, managementAudience, grantType, adminId, adminSecret);
        });

        assertEquals("webClient is marked non-null but is null", exception.getMessage());
    }

    @Test
    void authorizationServerWrapper_managementAudienceIsNull_throwNullException() {
        // Given
        String managementAudience = "Management Audience";
        String grantType = "Grant Type";
        String adminId = "Admin Id";
        String adminSecret = "Admin Secret";

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            authorizationServerWrapperConfig.auth0AuthorizationServerWrapper(webClient, null, grantType, adminId, adminSecret);
        });

        assertEquals("managementAudience is marked non-null but is null", exception.getMessage());
    }

    @Test
    void authorizationServerWrapper_grantTypeIsNull_throwNullException() {
        // Given
        String managementAudience = "Management Audience";
        String grantType = "Grant Type";
        String adminId = "Admin Id";
        String adminSecret = "Admin Secret";

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            authorizationServerWrapperConfig.auth0AuthorizationServerWrapper(webClient, managementAudience, null, adminId, adminSecret);
        });

        assertEquals("grantType is marked non-null but is null", exception.getMessage());
    }

    @Test
    void authorizationServerWrapper_adminIdIsNull_throwNullException() {
        // Given
        String managementAudience = "Management Audience";
        String grantType = "Grant Type";
        String adminId = "Admin Id";
        String adminSecret = "Admin Secret";

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            authorizationServerWrapperConfig.auth0AuthorizationServerWrapper(webClient, managementAudience, grantType, null, adminSecret);
        });

        assertEquals("adminId is marked non-null but is null", exception.getMessage());
    }

    @Test
    void authorizationServerWrapper_adminSecretIsNull_throwNullException() {
        // Given
        String managementAudience = "Management Audience";
        String grantType = "Grant Type";
        String adminId = "Admin Id";
        String adminSecret = "Admin Secret";

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            authorizationServerWrapperConfig.auth0AuthorizationServerWrapper(webClient, managementAudience, grantType, adminId, null);
        });

        assertEquals("adminSecret is marked non-null but is null", exception.getMessage());
    }
}