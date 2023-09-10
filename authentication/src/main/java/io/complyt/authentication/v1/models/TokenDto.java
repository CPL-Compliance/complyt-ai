package io.complyt.authentication.v1.models;

import io.complyt.authentication.v1.api_info.FieldsDescriptions;
import io.complyt.authentication.v1.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(name = "Token", description = FieldsDescriptions.TOKEN)
public record TokenDto(
        @NotNull(message = "Token.accessToken " + DtoErrorMessages.NOT_NULL_ERROR)
        String accessToken,
        @NotNull(message = "Token.scope " + DtoErrorMessages.NOT_NULL_ERROR)
        String scope,
        @Min(message = "Token.expiresIn " + DtoErrorMessages.NOT_NULL_ERROR, value = 1)
        int expiresIn,
        @NotNull(message = "Token.tokenType " + DtoErrorMessages.NOT_NULL_ERROR)
        String tokenType,
        @NotNull(message = "Token.createdAt " + DtoErrorMessages.NOT_NULL_ERROR)
        LocalDateTime createdAt,
        @NotNull(message = "Token.expireAt " + DtoErrorMessages.NOT_NULL_ERROR)
        LocalDateTime expireAt) {
}