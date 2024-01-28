package io.complyt.authentication.business.authorization.Wrappers;

import io.complyt.authentication.business.authorization.Auth0Client;
import lombok.NonNull;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

public interface ApiKeyRevocationAuthorinzationServerWrapper {
    Mono<Auth0Client> removeApiKeyFromClient(final @NonNull String clientName, final @NonNull String clientId,
                                             final @NonNull String tenantId, @NonNull String accessToken,
                                             @RequestParam(value = "newClientId", required = false) String newClientId,
                                             @RequestParam(value = "newClientSecret", required = false) String newClientSecret);
}
