package io.complyt.authentication.business.authorization;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StubAuth0AuthorizationServerWrapper implements AuthorizationServerWrapper {
    String accessToken = "accessToken";
    String scope = "scope";
    int expiresIn = 86400;
    String tokenType = "Bearer";

    @Override
    public Mono<AccessToken> getAccessToken(@NonNull String clientId, @NonNull String clientSecret,
                                            @NonNull String audience, @NonNull String grantType) {
        return Mono.just(new AccessToken(accessToken, scope, expiresIn, tokenType));
    }
}
