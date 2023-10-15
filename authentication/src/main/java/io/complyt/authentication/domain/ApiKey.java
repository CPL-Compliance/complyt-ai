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
    String apiKeyRegex = "^((?:[^-]+-){4}[^-]+)-((?:[^-]+-){4}[^-]+)$";

    public ApiKey(@NonNull String apiKey) {
        Pattern pattern = Pattern.compile(apiKeyRegex);
        Matcher matcher = pattern.matcher(apiKey);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid API key format");
        }

        clientId = matcher.group(1);
        clientSecret = matcher.group(2);
    }
}
