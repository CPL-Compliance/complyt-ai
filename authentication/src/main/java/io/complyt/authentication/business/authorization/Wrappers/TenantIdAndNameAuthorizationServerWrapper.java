package io.complyt.authentication.business.authorization.Wrappers;

import io.complyt.authentication.domain.TenantIdAndNameObject;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface TenantIdAndNameAuthorizationServerWrapper {
    Mono<TenantIdAndNameObject> getTenantIdAndClientNameFromAuth0(final @NonNull String clientId, @NonNull String accessToken);

}
