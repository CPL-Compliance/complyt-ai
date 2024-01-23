package io.complyt.authentication.services;

import io.complyt.authentication.business.authorization.AccessToken;
import io.complyt.authentication.business.authorization.Auth0Client;
import io.complyt.authentication.business.authorization.AuthorizationServerWrapper;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.Token;
import io.swagger.v3.core.util.Json;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AuthorizationService {
    @NonNull
    AuthorizationServerWrapper authorizationServerWrapper;

    @NonNull
    String audience;

    @NonNull
    String grantType;

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

    public Mono<Token> deleteApiKey(@NonNull Credentials credentials) {
//        return authorizationServerWrapper.removeApiKeyFromClient(credentials.getClientName(), credentials.getClientId(), credentials.getTenantId());
        return null;
    }


    public Mono<Auth0Client> getTenantIdAndClientName(@NonNull Credentials credentials) {
        return authorizationServerWrapper
                .getManagementAccessToken(credentials.getClientId(), credentials.getClientSecret(), audience, grantType)
                .flatMap(accessToken -> authorizationServerWrapper.getTenantIdAndClientNameFromAuth0(credentials.getClientId(), accessToken));
    }


}
