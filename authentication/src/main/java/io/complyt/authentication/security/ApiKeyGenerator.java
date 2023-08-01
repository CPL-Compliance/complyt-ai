package io.complyt.authentication.security;

import java.util.UUID;

public class ApiKeyGenerator {

    public static String generate() {
        UUID partOne = UUID.randomUUID();
        UUID partTwo = UUID.randomUUID();
        String delimiter = "-";
        String apiKey = partOne + delimiter + partTwo;

        return apiKey;
    }
}
