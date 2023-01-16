package com.complyt.v1.models.timestamps;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode
@ToString
@With
@Getter
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Schema(name = "Timestamps")
public class TimestampsDto {

    @NonNull
    @Valid
    @NotNull(message = "Created date may not be null")
    ComplytTimestampDto createdDate;

    @NonNull
    @Valid
    @NotNull(message = "Updated date may not be null")
    ComplytTimestampDto updatedDate;

}
