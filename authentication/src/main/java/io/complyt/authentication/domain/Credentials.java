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
@Document(collection = "credentials")
public class Credentials {
    @Id
    String apiKey;
    String clientId;
    String ClientSecret;
    String audience;
    String grantType;
    String ivClientId;
    String ivClientSecret;
}
