package io.complyt.authentication.auth0_client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtConfiguration {
    private int lifetime_in_seconds;
    private boolean secret_encoded;
}
