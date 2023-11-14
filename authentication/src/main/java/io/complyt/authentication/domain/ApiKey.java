package io.complyt.authentication.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiKey {
    @NonNull
    String clientId;

    @NonNull
    String clientSecret;

    @NonNull
    public static final String UUID_REGEXP = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

    public ApiKey(@NonNull String clientId, @NonNull String clientSecret) {
        Pattern pattern = Pattern.compile(UUID_REGEXP);
        Matcher clientIdMatcher = pattern.matcher(clientId);
        Matcher clientSecretMatcher = pattern.matcher(clientSecret);

        if (!clientIdMatcher.matches() || !clientSecretMatcher.matches()) {
            throw new IllegalArgumentException("Invalid API key format");
        }

        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }
}
