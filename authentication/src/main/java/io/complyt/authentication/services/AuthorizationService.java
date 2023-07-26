package io.complyt.authentication.services;

import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.domain.authorization.AccessToken;
import io.complyt.authentication.domain.authorization.AuthorizationServerWrapper;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorizationService {
    @NonNull
    AuthorizationServerWrapper auth0AuthorizationServerWrapper;

    public Mono<Token> getToken(Credentials credentials) {
        return auth0AuthorizationServerWrapper.getAccessToken(credentials.getClientId(), credentials.getClientSecret(),
                credentials.getAudience(), credentials.getGrantType()).map(accessToken -> createToken(credentials, accessToken));
    }

    private Token createToken(Credentials credentials, AccessToken accessToken) {
        return new Token(credentials.getApiKey(), accessToken.accessToken(), accessToken.scope(),
                Integer.parseInt(accessToken.expires_in()), accessToken.token_type());
    }
}
