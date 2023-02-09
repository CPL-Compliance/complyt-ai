package com.complyt.v1.models.timestamps;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;


@With
@Schema(name = "Timestamps")
public record TimestampsDto(@Valid @NotNull(message = "Created date may not be null") ComplytTimestampDto createdDate,
                            @Valid @NotNull(message = "Updated date may not be null") ComplytTimestampDto updatedDate) {

}
