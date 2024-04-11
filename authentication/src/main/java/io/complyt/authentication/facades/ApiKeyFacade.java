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

    public Mono<ApiKey> saveNewCredentials(@NonNull final Credentials credentials) {
        ApiKey apiKey = apiKeyService.generate();
        return authorizationService.getTenantIdAndClientName(credentials)
                .flatMap(tenantIdAndNameObject -> credentialsService.saveCredentials(credentials, apiKey, tenantIdAndNameObject.getTenantId(), tenantIdAndNameObject.getName()).thenReturn(apiKey));
    }

    public Mono<Credentials> markAsCancelled(@NonNull final ApiKey apiKey) {

        return credentialsService.markAsCancelled(apiKey)
                .flatMap(credentials -> authorizationService.getManagementAccessToken()
                        .flatMap(accessToken -> authorizationService.deleteClientMetadata(credentials, accessToken)
                                .then(tokenService.deleteToken(apiKey)
                                        .thenReturn(credentials))));
    }

    public Mono<ApiKey> rotateCredentials(@NonNull final ApiKey apiKey) {
        return credentialsService.rotateOldCredentials(apiKey)
                .flatMap(credentials -> generateAndSaveNewCredentialsByExisting(credentials)
                        .flatMap(newApiKey -> authorizationService.getManagementAccessToken()
                                .flatMap(accessToken -> authorizationService.rotateClientMetadata(credentials, newApiKey, accessToken)
                                .thenReturn(newApiKey))));
    }

    public Mono<ApiKey> generateAndSaveNewCredentialsByExisting(@NonNull final Credentials credentials) {
        ApiKey apiKey = apiKeyService.generate();
        return credentialsService.saveCredentialsByExistingCredentials(credentials, apiKey).thenReturn(apiKey);
    }
}
