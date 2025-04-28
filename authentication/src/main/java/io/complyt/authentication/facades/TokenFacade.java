package io.complyt.authentication.facades;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.security.TenantResolver;
import io.complyt.authentication.services.AuthorizationService;
import io.complyt.authentication.services.CredentialsService;
import io.complyt.authentication.services.PartnershipService;
import io.complyt.authentication.services.TokenService;
import io.complyt.authentication.v1.exceptions.types.ApiKeyNotValidException;
import io.complyt.authentication.v1.exceptions.types.FailedToCreateJWTException;
import io.complyt.authentication.v1.exceptions.types.TenantNotSupportedException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Component
public class TokenFacade {
    @NonNull
    TokenService tokenService;

    @NonNull
    CredentialsService credentialsService;

    @NonNull
    PartnershipService partnershipService;

    @NonNull
    AuthorizationService authorizationService;

    @NonNull
    TenantResolver tenantResolver;

    public Mono<Token> getToken(final @NonNull ApiKey apiKey) {
        return tokenService.findByApiKeyAndDecrypt(apiKey)
                .switchIfEmpty(Mono.defer(() -> credentialsService.getCredentialsByApiKeyAndDecrypt(apiKey)
                                .flatMap(credentials -> authorizationService.getToken(credentials)
                                        .switchIfEmpty(Mono.error(new ApiKeyNotValidException()))
                                        .flatMap(token -> tokenService.saveToken(token, credentials.getTenantId()))))
                        .switchIfEmpty(Mono.error(new ApiKeyNotValidException())));

    }

    public Mono<Token> getTokenForPartnerByTenantId(final @NonNull String requestedTenantId) {
        return tenantResolver.resolve()
                .flatMap(partnerTenantId -> partnershipService.findSupportedTenantsForPartnerByTenantId(partnerTenantId)
                        .flatMap(supportedTenantIds -> supportedTenantIds.contains(requestedTenantId) ?
                                credentialsService.getApiKeyByTenantId(requestedTenantId)
                                        .flatMap(apiKey -> tokenService.findByApiKeyAndTenantIdForPartnerAndDecrypt(apiKey, partnerTenantId)
                                                .switchIfEmpty(Mono.defer(() -> credentialsService.getCredentialsForPartnerByApiKeyAndDecrypt(apiKey)
                                                        .flatMap(credentials -> authorizationService.getTokenForPartner(credentials, partnerTenantId))
                                                        .flatMap(token -> tokenService.saveToken(token, requestedTenantId, partnerTenantId))
                                                        .switchIfEmpty(Mono.error(new FailedToCreateJWTException()))))) :
                                Mono.error(new TenantNotSupportedException())));

    }
}