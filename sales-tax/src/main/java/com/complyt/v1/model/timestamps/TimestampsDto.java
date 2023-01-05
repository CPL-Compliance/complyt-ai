package com.complyt.v1.model.timestamps;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@EqualsAndHashCode
@ToString
@With
@Getter
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Schema(name = "Timestamps")
public class TimestampsDto {
    @CreatedDate
    @NonNull
    ComplytTimestampDto createdDate;
    @LastModifiedDate
    @NonNull
    ComplytTimestampDto updatedDate;

}
