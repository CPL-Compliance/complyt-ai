package io.complyt.authentication.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Document(collection = "token")
public class Token {

    @Id
    @NonNull
    String apiKey;

    @NonNull
    String accessToken;

    @NonNull
    String scope;

    @NonNull
    String expiresIn;

    @NonNull
    String tokenType;
}