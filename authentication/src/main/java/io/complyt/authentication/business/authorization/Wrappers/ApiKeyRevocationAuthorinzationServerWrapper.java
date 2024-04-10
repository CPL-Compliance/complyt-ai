package io.complyt.authentication.business.authorization.Wrappers;

import io.complyt.authentication.auth0_client.Auth0Client;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface ApiKeyRevocationAuthorinzationServerWrapper {
    Mono<Auth0Client> updateApiKeyFromClient(final @NonNull String clientName, final @NonNull String clientId,
                                             final @NonNull String tenantId, @NonNull String accessToken,
                                             final String newClientId, final String newClientSecret);
}
