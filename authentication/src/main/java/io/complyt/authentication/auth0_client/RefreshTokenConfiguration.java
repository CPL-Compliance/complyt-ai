package io.complyt.authentication.auth0_client;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenConfiguration {
    private String expiration_type;
    private int leeway;
    private boolean infinite_token_lifetime;
    private boolean infinite_idle_token_lifetime;
    private int token_lifetime;
    private int idle_token_lifetime;
    private String rotation_type;
}