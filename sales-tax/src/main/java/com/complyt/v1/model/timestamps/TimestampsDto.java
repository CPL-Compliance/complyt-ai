package com.complyt.v1.model.timestamps;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode
@ToString
@With
@Getter
@Slf4j
@AllArgsConstructor
@Schema(name = "Timestamps")
public class TimestampsDto {
    private ComplytTimestampDto createdDate;
    private ComplytTimestampDto updatedDate;

}
