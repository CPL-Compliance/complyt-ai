package io.complyt.authentication.config;

import io.complyt.authentication.business.authorization.Auth0AuthorizationServerWrapper;
import io.complyt.authentication.business.authorization.AuthorizationServerWrapper;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AuthorizationServerWrapperConfig {

    @Profile({"auth0", "default"})
    @Bean("authorizationServerWrapper")
    Auth0AuthorizationServerWrapper auth0AuthorizationServerWrapper(@NonNull WebClient webClient) {
        return new Auth0AuthorizationServerWrapper(webClient);
    }
}
