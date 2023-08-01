package io.complyt.authentication.v1.models;

import io.complyt.authentication.v1.api_info.FieldsDescriptions;
import io.complyt.authentication.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record ApiKeyDto(@Schema(description = FieldsDescriptions.API_KEY) @Size(min = 1, max = 256, message = "clientSecret " + StringErrorMessages.MINMAX_256_ERROR) String apiKey) {
}
