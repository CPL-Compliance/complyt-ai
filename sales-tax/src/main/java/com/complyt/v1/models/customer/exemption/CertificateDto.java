package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
@Schema(name = "Certificate")
public record CertificateDto(
        @NotNull(message = "Certificate.id" + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "Certificate.id" + StringErrorMessages.MINMAX_256_ERROR) String certificateId,
        @NotNull(message = "Certificate.url" + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "Certificate.url" + StringErrorMessages.MINMAX_256_ERROR) String url,
        @NotNull(message = "Certificate.name" + DtoErrorMessages.NOT_NULL_ERROR) @Size(min = 1, max = 256, message = "Certificate.name" + StringErrorMessages.MINMAX_256_ERROR) String name) {

}
