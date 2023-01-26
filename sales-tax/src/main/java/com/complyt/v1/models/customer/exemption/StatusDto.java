package com.complyt.v1.models.customer.exemption;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Schema(name = "Status")
public class StatusDto {
    private final String code;
    private final String name;
}
