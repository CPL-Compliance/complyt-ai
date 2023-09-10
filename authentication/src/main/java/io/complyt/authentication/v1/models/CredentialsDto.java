package io.complyt.authentication.v1.models;

import io.complyt.authentication.v1.api_info.FieldsDescriptions;
import io.complyt.authentication.v1.config.error_messages.DtoErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "Credentials", description = FieldsDescriptions.CREDENTIALS)
public record CredentialsDto(@Schema(description = FieldsDescriptions.CLIENT_ID)
                             @NotNull(message = "Credentials.clientId " + DtoErrorMessages.NOT_NULL_ERROR)
                             @NotBlank(message = "Credentials.clientId " + DtoErrorMessages.NOT_NULL_BLANK)
                             String clientId,
                             @Schema(description = FieldsDescriptions.CLIENT_SECRET)
                             @NotNull(message = "Credentials.clientSecret " + DtoErrorMessages.NOT_NULL_ERROR)
                             @NotBlank(message = "Credentials.clientSecret " + DtoErrorMessages.NOT_NULL_BLANK)
                             String clientSecret) {
}
