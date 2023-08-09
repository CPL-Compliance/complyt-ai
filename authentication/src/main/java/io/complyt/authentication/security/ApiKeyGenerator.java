package io.complyt.authentication.security;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ApiKeyGenerator {

    public static String generate() {
        UUID clientId = UUID.randomUUID();
        UUID clientSecret = UUID.randomUUID();
        String delimiter = "-";

        return clientId + delimiter + clientSecret;
    }
}
