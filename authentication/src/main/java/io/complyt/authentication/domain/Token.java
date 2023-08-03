package io.complyt.authentication.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Value
@Builder
@With
@AllArgsConstructor
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
}