package io.complyt.authentication.config;

import io.complyt.authentication.business.authorization.Auth0AuthorizationServerWrapper;
import io.complyt.authentication.business.authorization.AuthorizationServerWrapper;
import io.complyt.authentication.business.authorization.StubAuth0AuthorizationServerWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AuthorizationServerWrapperConfigTest {
    AuthorizationServerWrapperConfig authorizationServerWrapperConfig;

    @BeforeEach
    void setUp() {
        authorizationServerWrapperConfig = new AuthorizationServerWrapperConfig();
    }

    @Test
    void authorizationServerWrapper_createAuth0AuthorizationServerWrapper_getAuth0AuthorizationServerWrapper() {
        WebClient webClient = WebClient.builder().build();

        Auth0AuthorizationServerWrapper expectedAuth0AuthorizationServerWrapper = new Auth0AuthorizationServerWrapper(webClient);
        AuthorizationServerWrapper actualAuth0AuthorizationServerWrapper = authorizationServerWrapperConfig.auth0AuthorizationServerWrapper(webClient);

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
        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            authorizationServerWrapperConfig.auth0AuthorizationServerWrapper(null);
        });

        assertEquals("webClient is marked non-null but is null", exception.getMessage());
    }
}