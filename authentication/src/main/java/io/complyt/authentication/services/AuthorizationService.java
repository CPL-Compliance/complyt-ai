package io.complyt.authentication.services;

import io.complyt.authentication.business.authorization.AccessToken;
import io.complyt.authentication.business.authorization.AuthorizationServerWrapper;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.Token;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorizationService {
    @NonNull
    AuthorizationServerWrapper authorizationServerWrapper;

    public Mono<Token> getToken(@NonNull Credentials credentials) {
        return authorizationServerWrapper.getAccessToken(credentials.getClientId(), credentials.getClientSecret(),
                        credentials.getAudience(), credentials.getGrantType())
                .mapNotNull(accessToken -> createToken(credentials, accessToken));
    }

    private Token createToken(@NonNull Credentials credentials, @NonNull AccessToken accessToken) {
        return Token.builder()
                .complytClientId(credentials.getComplytClientId())
                .complytClientSecret(credentials.getComplytClientSecret())
                .accessToken(accessToken.getAccessToken())
                .scope(accessToken.getScope())
                .expiresIn(accessToken.getExpiresIn())
                .tokenType(accessToken.getTokenType())
                .build();
    }
}
