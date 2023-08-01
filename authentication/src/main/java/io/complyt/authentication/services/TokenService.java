package io.complyt.authentication.services;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.repositories.TokenRepository;
import io.complyt.authentication.v1.models.ApiKey;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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

    public Mono<Token> findByApiKey(final @NonNull ApiKey apiKey) {
        return tokenRepository.findByComplytClientId(apiKey.getClientId())
                .filter(token -> passwordEncoder.matches(apiKey.getClientSecret(), token.getComplytClientSecret()));
    }

    public Mono<Token> saveToken(Token token) {
        return tokenRepository.save(token);
    }
}
