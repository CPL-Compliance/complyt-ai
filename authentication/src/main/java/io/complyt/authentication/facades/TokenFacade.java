package io.complyt.authentication.facades;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.services.AuthorizationService;
import io.complyt.authentication.services.CredentialsService;
import io.complyt.authentication.services.TokenService;
import io.complyt.authentication.v1.exceptions.types.ApiKeyNotValidException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Service
public class TokenFacade {
    @NonNull
    TokenService tokenService;

    @NonNull
    CredentialsService credentialsService;

    @NonNull
    AuthorizationService authorizationService;

    public Mono<Token> getToken(final @NonNull ApiKey apiKey) {
        return tokenService.findByApiKeyAndDecrypt(apiKey)
                .switchIfEmpty(Mono.defer(() -> credentialsService.getCredentialsByApiKeyAndDecrypt(apiKey)
                        .flatMap(credentials -> authorizationService.getToken(credentials))
                        .switchIfEmpty(Mono.error(new ApiKeyNotValidException())))
                        .flatMap(tokenService::saveToken));

    }
}