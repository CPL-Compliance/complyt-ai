package io.complyt.authentication.v1.models;

import io.complyt.authentication.v1.api_info.FieldsDescriptions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
@Schema(name = "Token", description = FieldsDescriptions.TOKEN)
public record TokenDto(
        String accessToken,
        String scope,
        int expiresIn,
        String tokenType) {
}