package com.complyt.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Schema(name = "TimeStamps")
public class TimeStamps {
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
