package io.complyt.authentication.v1.models;

import jakarta.validation.constraints.Pattern;

public record ApiKeyDto(@Pattern(regexp = "^((?:[^-]+-){4}[^-]+)-((?:[^-]+-){4}[^-]+)$",
        message = "Invalid API key format") String apiKey) {
}
