package io.complyt.authentication.facades;

import io.complyt.authentication.domain.Token;
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

    public Mono<Token> get(final @NonNull Token token) {
        return tokenService.getByEncodedApiKey(token);
    }
}
