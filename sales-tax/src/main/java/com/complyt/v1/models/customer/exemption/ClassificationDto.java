package com.complyt.v1.models.customer.exemption;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Schema(name = "Classification")
public class ClassificationDto {

    @NotBlank(message = "code may not be blank")
    @Size(min = 1, max = 256, message = "code should be 1-256 characters maximum")
    private final String code;

    @NotBlank(message = "description may not be blank")
    @Size(min = 1, max = 256, message = "description should be 1-256 characters maximum")
    private final String description;
}