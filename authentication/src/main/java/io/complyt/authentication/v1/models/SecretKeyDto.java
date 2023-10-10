package io.complyt.authentication.v1.models;

import io.complyt.authentication.v1.api_info.FieldsDescriptions;
import io.complyt.authentication.v1.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "Credentials", description = FieldsDescriptions.CREDENTIALS)
public record SecretKeyDto(@NotNull(message = "SecretKey.secretKey " + DtoErrorMessages.NOT_NULL_ERROR)
                           String secretKey) {
}
