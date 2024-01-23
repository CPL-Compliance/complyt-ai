package io.complyt.authentication.facades;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.services.ApiKeyService;
import io.complyt.authentication.services.AuthorizationService;
import io.complyt.authentication.services.CredentialsService;
import io.complyt.authentication.services.TokenService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Service
public class ApiKeyFacade {
    @NonNull
    CredentialsService credentialsService;

    @NonNull
    ApiKeyService apiKeyService;

    @NonNull
    TokenService tokenService;

    @NonNull
    AuthorizationService authorizationService;

    public Mono<ApiKey> saveCredentials(@NonNull final Credentials credentials) {
        ApiKey apiKey = apiKeyService.generate();
        return authorizationService.getTenantIdAndClientName(credentials)
                .flatMap(auth0Client -> credentialsService.saveCredentials(credentials, apiKey, auth0Client.getClient_metadata().getTenant_id(), auth0Client.getName()).thenReturn(apiKey));
    }

    public Mono<Credentials> markAsCancelled(@NonNull final ApiKey apiKey) {

        return credentialsService.markAsCancelled(apiKey)
                .flatMap(credentials -> authorizationService.deleteApiKey(credentials)
                .then(tokenService.deleteToken(apiKey)
                        .thenReturn(credentials)));
    }

//    public Mono<Credentials> markAsCancelled(@NonNull final ApiKey apiKey) {
//
//        return credentialsService.markAsCancelled(apiKey)
//                .flatMap(credentials -> tokenService.deleteToken(apiKey).thenReturn(credentials));
//    }
}
