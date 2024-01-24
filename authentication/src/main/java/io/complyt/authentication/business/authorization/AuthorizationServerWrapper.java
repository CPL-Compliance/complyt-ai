package io.complyt.authentication.business.authorization;

import io.swagger.v3.core.util.Json;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface AuthorizationServerWrapper {
    Mono<AccessToken> getAccessToken(final @NonNull String clientId, final @NonNull String clientSecret,
                                     final @NonNull String audience, final @NonNull String grantType);

    public Mono<Auth0Client> removeApiKeyFromClient(final @NonNull String clientName, final @NonNull String clientId,
                                                    final @NonNull String tenantId, @NonNull String accessToken);

    public Mono<AccessToken> getManagementAccessToken();

    public Mono<Auth0Client> getTenantIdAndClientNameFromAuth0(final @NonNull String clientId, @NonNull String accessToken);
}
