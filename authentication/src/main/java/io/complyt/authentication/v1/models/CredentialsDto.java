package io.complyt.authentication.v1.models;

import io.complyt.authentication.v1.api_info.FieldsDescriptions;
import io.complyt.authentication.v1.config.error_messages.DtoErrorMessages;
import io.complyt.authentication.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@Schema(name = "Credentials", description = FieldsDescriptions.CREDENTIALS)
public record CredentialsDto(@Schema(description = FieldsDescriptions.CLIENT_ID)
                             @NotNull(message = "Credentials.clientId " + DtoErrorMessages.NOT_NULL_ERROR)
                             @Size(min = 1, max = 256, message = "Credentials.clientId "
                                     + StringErrorMessages.MINMAX_256_ERROR)
                             String clientId,
                             @Schema(description = FieldsDescriptions.CLIENT_SECRET)
                             @NotNull(message = "Credentials.clientSecret " +
                                     DtoErrorMessages.NOT_NULL_ERROR)
                             @Size(min = 1, max = 256, message = "Credentials.clientSecret " +
                                     StringErrorMessages.MINMAX_256_ERROR)
                             String clientSecret) {
}
