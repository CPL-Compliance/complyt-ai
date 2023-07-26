package io.complyt.authentication.config;

import io.complyt.authentication.domain.authorization.Auth0AuthorizationServerWrapper;
import io.complyt.authentication.domain.authorization.AuthorizationServerWrapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AuthorizationServerWrapperConfig {

    @Bean
    AuthorizationServerWrapper auth0AuthorizationServerWrapper(
            @NonNull @Value("{authorization-server-url}") String serverUrl,
            @NonNull WebClient webClient){
        return new Auth0AuthorizationServerWrapper(serverUrl, webClient);
    }
}
