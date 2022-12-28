package com.complyt.v1.model.timestamps;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode
@ToString
@With
@Getter
@RequiredArgsConstructor
@Slf4j
@Schema(name = "Timestamps")
public class TimestampsDto {
    @NonNull
    private final ComplytTimestampDto createdDate;
    @NonNull
    private final ComplytTimestampDto updatedDate;

}
