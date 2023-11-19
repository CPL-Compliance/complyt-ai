package io.complyt.authentication.security;

import io.complyt.authentication.domain.ApiKey;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ApiKeyGenerator {

    public static @NonNull ApiKey generate() {
        UUID clientId = UUID.randomUUID();
        UUID clientSecret = UUID.randomUUID();

        return new ApiKey(clientId.toString(), clientSecret.toString());
    }
}
