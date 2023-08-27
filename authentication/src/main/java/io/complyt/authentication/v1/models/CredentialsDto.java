package io.complyt.authentication.v1.models;

import io.complyt.authentication.v1.api_info.FieldsDescriptions;
import io.complyt.authentication.v1.config.error_messages.DtoErrorMessages;
import io.complyt.authentication.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
@Schema(name = "Credentials", description = FieldsDescriptions.TOKEN)
public record CredentialsDto(@Schema(description = FieldsDescriptions.CLIENT_ID) @NotNull(message = "clientId " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "clientId " + StringErrorMessages.MINMAX_256_ERROR) String clientId,
                             @Schema(description = FieldsDescriptions.CLIENT_SECRET) @NotNull(message = "clientSecret " + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "clientSecret " + StringErrorMessages.MINMAX_256_ERROR) String clientSecret) {
}
