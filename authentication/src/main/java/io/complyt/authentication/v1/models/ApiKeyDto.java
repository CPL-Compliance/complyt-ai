package io.complyt.authentication.v1.models;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.v1.api_info.FieldsDescriptions;
import io.complyt.authentication.v1.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(name = "ApiKey", description = FieldsDescriptions.API_KEY)
public record ApiKeyDto(@NotNull(message = "ApiKey.apiKey " + DtoErrorMessages.NOT_NULL_ERROR)
                        @NotBlank(message = "ApiKey.apiKey " + DtoErrorMessages.NOT_NULL_BLANK)
                        @Pattern(regexp = ApiKey.API_KEY_REGEXP,
                                message = "Invalid API key format") String apiKey) {
}