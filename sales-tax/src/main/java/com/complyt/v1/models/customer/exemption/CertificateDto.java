package com.complyt.v1.models.customer.exemption;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@With
@Schema(name = "Certificate")
public record CertificateDto(
        @NotBlank(message = "Certificate Id may not be blank") @Size(min = 1, max = 256, message = "Certificate Id should be 1-256 characters maximum") String certificateId,
        @NotBlank(message = "Url may not be blank") @Size(min = 1, max = 256, message = "Url should be 1-256 characters maximum") String url,
        @NotBlank(message = "Name may not be blank") @Size(min = 1, max = 256, message = "Name should be 1-256 characters maximum") String name) {

}
