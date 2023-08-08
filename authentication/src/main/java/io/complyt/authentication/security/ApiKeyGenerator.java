package io.complyt.authentication.security;

import java.util.UUID;

public class ApiKeyGenerator {

    public static String generate() {
        UUID clientId = UUID.randomUUID();
        UUID clientSecret = UUID.randomUUID();
        String delimiter = "-";

        return clientId + delimiter + clientSecret;
    }
}
