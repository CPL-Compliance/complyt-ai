package io.complyt.authentication.business.authorization;

import io.swagger.v3.core.util.Json;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface AuthorizationServerWrapper {
    Mono<AccessToken> getAccessToken(final @NonNull String clientId, final @NonNull String clientSecret,
                                     final @NonNull String audience, final @NonNull String grantType);

    public Mono<Auth0Client> removeApiKeyFromClient(final @NonNull String clientName, final @NonNull String clientId,
                                                    final @NonNull String tenantId);

    public Mono<AccessToken> getManagementAccessToken(final @NonNull String clientId, final @NonNull String clientSecret,
                                                      final @NonNull String audience, final @NonNull String grantType);

    public Mono<Auth0Client> getTenantIdAndClientNameFromAuth0(final @NonNull String clientId, @NonNull AccessToken accessToken);
}
