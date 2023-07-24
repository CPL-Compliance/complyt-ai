package io.complyt.authentication.services;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.repositories.TokenRepository;
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
public class TokenService {
    @NonNull
    TokenRepository tokenRepository;

    @NonNull
    PasswordEncoder encoder;

    public Mono<Token> getByEncodedApiKey(Token token) {
        return Mono.just(encoder.encode(token.getApiKey()))
                .flatMap(tokenRepository::findByApiKey);
    }
}
