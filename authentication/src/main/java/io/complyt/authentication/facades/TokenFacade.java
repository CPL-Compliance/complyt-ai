package io.complyt.authentication.facades;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.services.TokenService;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Service
public class TokenFacade {
    @NonNull
    TokenService tokenService;

    @NonNull
    PasswordEncoder encoder;

    public Mono<Token> get(Token token) {
        String encodedApiKey = encoder.encode(token.getApiKey());
        tokenService.getByEncodedApiKey(encodedApiKey);
    }
}
