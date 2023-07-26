package io.complyt.authentication.v1.models;

import io.complyt.authentication.v1.api_info.FieldsDescriptions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
@Schema(name = "Token", description = FieldsDescriptions.TOKEN)
public record TokenDto(
        @Size(min = 30, max = 128, message = "apiKey should be between 30 and 129 characters.") @NotNull(message = "apiKey shouldn't be blank") String apiKey,
        String accessToken,
        String scope,
        int expiresIn,
        String tokenType) {
}