package io.complyt.authentication.business.authorization;

import io.complyt.authentication.auth0_client.Auth0Client;
import io.complyt.authentication.auth0_client.ClientMetadata;
import io.complyt.authentication.domain.TenantIdAndNameObject;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StubAuth0AuthorizationServerWrapper implements AuthorizationServerWrapper {
    String accessToken = "accessToken";
    String managementToken = "managementToken";
    String scope = "scope";
    String managementScope = "managementScope";
    int expiresIn = 86400;
    String tokenType = "Bearer";

    @Override
    public Mono<AccessToken> getAccessToken(@NonNull String clientId, @NonNull String clientSecret,
                                            @NonNull String audience, @NonNull String grantType) {
        return Mono.just(new AccessToken(accessToken, scope, expiresIn, tokenType));
    }

    @Override
    public Mono<Auth0Client> updateApiKeyFromClient(@NonNull String clientName, @NonNull String clientId, @NonNull String tenantId, @NonNull String accessToken,
                                                    final String newClientId, final String newClientSecret) {
        return Mono.just(new Auth0Client("tenant", false, false, "name", new ClientMetadata("tenantId", "ClientId", "clientSecret"),
                true, true, false, false,null, null, "clientId", true, "clientSecret",
                null, "appType", null, true ));
    }

    @Override
    public Mono<AccessToken> getManagementAccessToken() {
        return Mono.just(new AccessToken(managementToken, managementScope, expiresIn, tokenType));
    }

    @Override
    public Mono<TenantIdAndNameObject> getTenantIdAndClientNameFromAuth0(@NonNull String clientId, @NonNull String accessToken) {
        return Mono.just(new TenantIdAndNameObject("test_tenant", "test_name"));
    }
}
