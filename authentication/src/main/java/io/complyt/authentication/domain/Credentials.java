package io.complyt.authentication.domain;

import io.complyt.authentication.domain.enums.ApiKeyStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
@With
@Document(collection = "credentials")
public class Credentials {
    @Id
    String id;
    String complytClientId;
    String complytClientSecret;
    String clientId;
    String clientSecret;
    String clientIdIv;
    String clientSecretIv;
    String audience;
    String grantType;
    String tenantId;
    String name;
    ApiKeyStatus status;
}
