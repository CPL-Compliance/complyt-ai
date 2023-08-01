package io.complyt.authentication.business.authorization;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Auth0AccessToken {
    String access_token; String scope; int expires_in; String token_type;
}
