package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Schema(name = "ComplytEntity")
public abstract class ComplytEntityDto {

    protected UUID complytId;
}
