package io.complyt.authentication.facades;

import io.complyt.authentication.domain.Token;
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
public class TokenFacade {
    @NonNull
    TokenService tokenService;

    @NonNull
    CredentialsService credentialsService;

    @NonNull
    AuthorizationService authorizationService;

    public Mono<Token> post(final @NonNull Token token) {
        return tokenService.getToken(token)
                .switchIfEmpty(credentialsService.getCredentialsByApiKey(token)
                        .flatMap(credentials -> authorizationService.getToken(credentials))
                        .flatMap(tokenService::saveToken));
    }
}