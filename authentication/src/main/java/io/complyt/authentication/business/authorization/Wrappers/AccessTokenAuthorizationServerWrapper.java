package io.complyt.authentication.business.authorization.Wrappers;

import io.complyt.authentication.business.authorization.AccessToken;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface AccessTokenAuthorizationServerWrapper {
    Mono<AccessToken> getAccessToken(final @NonNull String clientId, final @NonNull String clientSecret,
                                     final @NonNull String audience, final @NonNull String grantType);

}
