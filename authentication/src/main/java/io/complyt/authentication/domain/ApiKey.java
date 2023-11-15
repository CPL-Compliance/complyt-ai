package io.complyt.authentication.domain;

import lombok.NonNull;

public record ApiKey(@NonNull String clientId, @NonNull String clientSecret) {
    @NonNull
    public static final String UUID_REGEXP = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-4[0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

}
