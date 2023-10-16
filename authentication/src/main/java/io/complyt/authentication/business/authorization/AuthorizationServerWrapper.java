package io.complyt.authentication.business.authorization;

import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface AuthorizationServerWrapper {
    Mono<AccessToken> getAccessToken(final @NonNull String clientId, final @NonNull String clientSecret,
                                     final @NonNull String audience, final @NonNull String grantType);
}
