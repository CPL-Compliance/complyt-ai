package io.complyt.authentication.business.authorization.Wrappers;

import io.complyt.authentication.business.authorization.AccessToken;
import reactor.core.publisher.Mono;

public interface ManagementAuthorizationServerWrapper {
    Mono<AccessToken> getManagementAccessToken();
}
