package io.complyt.authentication.config;

import io.complyt.authentication.business.authorization.Auth0AuthorizationServerWrapper;
import io.complyt.authentication.business.authorization.AuthorizationServerWrapper;
import io.complyt.authentication.business.authorization.StubAuth0AuthorizationServerWrapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AuthorizationServerWrapperConfig {

    @Profile({"auth0"})
    @Bean("authorizationServerWrapper")
    AuthorizationServerWrapper auth0AuthorizationServerWrapper(@NonNull WebClient webClient,
                                                               @NonNull @Value("${authorization.management-audience}") String managementAudience,
                                                               @NonNull @Value("${authorization.grant-type}") String grantType,
                                                               @NonNull @Value("${authorization.admin-client-id}") String adminId,
                                                               @NonNull @Value("${authorization.admin-client-secret}") String adminSecret) {

        return new Auth0AuthorizationServerWrapper(webClient, managementAudience, grantType, adminId, adminSecret);
    }

    @Profile({"stubAuth0", "default"})
    @Bean("authorizationServerWrapper")
    AuthorizationServerWrapper stubAuth0AuthorizationServerWrapper() {
        return new StubAuth0AuthorizationServerWrapper();
    }
}
