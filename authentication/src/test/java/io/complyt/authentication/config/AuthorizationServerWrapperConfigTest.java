package io.complyt.authentication.config;

import io.complyt.authentication.business.authorization.Auth0AuthorizationServerWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorizationServerWrapperConfigTest {
    AuthorizationServerWrapperConfig authorizationServerWrapperConfig;

    @BeforeEach
    void setUp() {
        authorizationServerWrapperConfig = new AuthorizationServerWrapperConfig();
    }

    @Test
    void authorizationServerWrapper_createAuthorizationServerWrapper_getAuthorizationServerWrapper() {
        WebClient webClient = WebClient.builder().build();

        Auth0AuthorizationServerWrapper expectedAuth0AuthorizationServerWrapper = new Auth0AuthorizationServerWrapper(webClient);
        Auth0AuthorizationServerWrapper actualAuth0AuthorizationServerWrapper = authorizationServerWrapperConfig.auth0AuthorizationServerWrapper(webClient);

        assertEquals(expectedAuth0AuthorizationServerWrapper, actualAuth0AuthorizationServerWrapper);
    }

}