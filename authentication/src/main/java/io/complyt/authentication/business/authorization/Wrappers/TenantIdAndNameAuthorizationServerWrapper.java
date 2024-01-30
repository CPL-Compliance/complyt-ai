package io.complyt.authentication.business.authorization.Wrappers;

import io.complyt.authentication.business.authorization.Auth0Client;
import io.complyt.authentication.business.authorization.TenentIdAndNameObject;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface TenantIdAndNameAuthorizationServerWrapper {
    Mono<TenentIdAndNameObject> getTenantIdAndClientNameFromAuth0(final @NonNull String clientId, @NonNull String accessToken);

}
