package io.complyt.authentication.domain.authorization;

public record AccessToken(String accessToken, String scope, String expires_in, String token_type) {
}
