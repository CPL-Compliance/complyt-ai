package io.complyt.authentication.auth0_client;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class JwtConfiguration {
    private int lifetime_in_seconds;
    private boolean secret_encoded;
}
