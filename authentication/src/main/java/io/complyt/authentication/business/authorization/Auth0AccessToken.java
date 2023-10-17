package io.complyt.authentication.business.authorization;

public record Auth0AccessToken(String access_token,
                               String scope,
                               int expires_in,
                               String token_type) {
}
