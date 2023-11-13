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
    public static final String API_KEY_REGEXP = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

    public ApiKey(@NonNull String clientId, @NonNull String clientSecret) {
        Pattern pattern = Pattern.compile(API_KEY_REGEXP);
        Matcher idMatcher = pattern.matcher(clientId);
        Matcher secretMatcher = pattern.matcher(clientSecret);

        if (!idMatcher.matches() || !secretMatcher.matches()) {
            throw new IllegalArgumentException("Invalid API key format");
        }

        this.clientId = idMatcher.group(0);
        this.clientSecret = secretMatcher.group(0);
    }
}
