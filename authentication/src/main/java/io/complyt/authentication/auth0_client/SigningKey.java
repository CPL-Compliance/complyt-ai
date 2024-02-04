package io.complyt.authentication.auth0_client;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class SigningKey {
    private String cert;
    private String pkcs7;
    private String subject;
}