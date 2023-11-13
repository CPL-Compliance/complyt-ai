package io.complyt.authentication.facades;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.services.ApiKeyService;
import io.complyt.authentication.services.CredentialsService;
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

    public Mono<ApiKey> saveCredentials(@NonNull final Credentials credentials) {
        ApiKey apiKey = apiKeyService.generate();
        return credentialsService.saveCredentials(credentials, apiKey).thenReturn(apiKey);
    }
}
