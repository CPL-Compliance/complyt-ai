package com.complyt.v1.models.customer.exemption;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "Classification")
public record ClassificationDto(
        @NotBlank(message = "Code may not be blank") @Size(min = 1, max = 256, message = "Code should be 1-256 characters maximum") String code,
        @NotBlank(message = "Description may not be blank") @Size(min = 1, max = 256, message = "Description should be 1-256 characters maximum") String description) {

}