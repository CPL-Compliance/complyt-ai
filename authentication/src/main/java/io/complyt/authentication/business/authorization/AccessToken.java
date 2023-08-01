package io.complyt.authentication.business.authorization;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AccessToken {
    String accessToken; String scope; int expiresIn; String tokenType;
}
