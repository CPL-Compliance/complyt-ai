package com.complyt.v1.models.customer.exemption;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@With
@Schema(name = "Status")
public record StatusDto(
        @NotBlank(message = "code may not be blank") @Size(min = 1, max = 256, message = "code should be 1-256 characters maximum") String code,
        @NotBlank(message = "name may not be blank") @Size(min = 1, max = 256, message = "name should be 1-256 characters maximum") String name) {

}
