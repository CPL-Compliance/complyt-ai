package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
@Schema(name = "Certificate", description = FieldsDescriptions.certificate)
public record CertificateDto(
        @NotNull(message = "Certificate.id" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "Certificate.id" + StringErrorMessages.minmax_256_error) String certificateId,
        @NotNull(message = "Certificate.url" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "Certificate.url" + StringErrorMessages.minmax_256_error) String url,
        @NotNull(message = "Certificate.name" + DtoErrorMessages.not_null_error) @Size(min = 1, max = 256, message = "Certificate.name" + StringErrorMessages.minmax_256_error) String name) {

}
