package io.complyt.authentication.services;

import io.complyt.authentication.business.authorization.AccessToken;
import io.complyt.authentication.business.authorization.AuthorizationServerWrapper;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.Token;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AuthorizationService {
    @NonNull
    AuthorizationServerWrapper authorizationServerWrapper;

    public Mono<Token> getToken(@NonNull Credentials credentials) {
        return authorizationServerWrapper.getAccessToken(credentials.getClientId(), credentials.getClientSecret(),
                        credentials.getAudience(), credentials.getGrantType())
                .mapNotNull(accessToken -> createToken(credentials, accessToken));
    }

    private Token createToken(Credentials credentials, AccessToken accessToken) {
        return Token.builder()
                .complytClientId(credentials.getComplytClientId())
                .complytClientSecret(credentials.getComplytClientSecret())
                .accessToken(accessToken.accessToken())
                .scope(accessToken.scope())
                .expiresIn(accessToken.expiresIn())
                .tokenType(accessToken.tokenType())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
