package io.complyt.authentication.auth0_client;

import lombok.*;

@Getter
@With
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ClientMetadata {
    private String tenant_id;
    private String clientId;
    private String clientSecret;
}