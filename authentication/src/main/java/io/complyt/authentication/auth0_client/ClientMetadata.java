package io.complyt.authentication.auth0_client;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.With;

@Getter
@With
@AllArgsConstructor
@EqualsAndHashCode
public class ClientMetadata {
    private String tenant_id;
    private String clientId;
    private String clientSecret;
}