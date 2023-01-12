package com.complyt.v1.models.customer.exemption;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Schema(name = "Classification")
public class ClassificationDto {
    private final String code;
    private final String description;
}