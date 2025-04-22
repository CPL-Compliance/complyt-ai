package io.complyt.authentication.domain;

import io.complyt.authentication.domain.enums.TokenSource;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Value
@Builder
@With
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document(collection = "token")
public class Token {
    @Id
    String id;

    @NonNull
    String complytClientId;

    @NonNull
    String complytClientSecret;

    @NonNull
    String accessToken;

    String accessTokenIv;

    @NonNull
    String scope;

    String scopeIv;

    int expiresIn;

    @NonNull
    String tokenType;

    LocalDateTime createdAt;

    LocalDateTime expireAt;

    String partnerTenantId;

    String clientTenantId;

    TokenSource tokenSource;
}