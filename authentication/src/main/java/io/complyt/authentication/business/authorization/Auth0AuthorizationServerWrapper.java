package io.complyt.authentication.business.authorization;

import io.complyt.authentication.domain.mappers.Auth0AccessTokenToAccessToken;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Auth0AuthorizationServerWrapper implements AuthorizationServerWrapper {


    @NonNull
    WebClient webClient;

    public Mono<AccessToken> getAccessToken(final @NonNull String clientId, final @NonNull String clientSecret,
                                            final @NonNull String audience, final @NonNull String grantType) {
        return webClient.post()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("client_id=" + clientId +
                        "&client_secret=" + clientSecret +
                        "&audience=" + audience +
                        "&grant_type=" + grantType)
                .retrieve()
                .bodyToMono(Auth0AccessToken.class)
                .map(Auth0AccessTokenToAccessToken.INSTANCE::map);
    }
}