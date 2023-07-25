package io.complyt.authentication.services;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.repositories.TokenRepository;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class TokenService {
    @NonNull
    TokenRepository tokenRepository;

    @NonNull
    private PasswordEncoder passwordEncoder;

    public Mono<Token> getByEncodedApiKey(Token token) {
        return Mono.just(passwordEncoder.encode(token.getApiKey()))
                .flatMap(tokenRepository::findByApiKey);
    }
}
