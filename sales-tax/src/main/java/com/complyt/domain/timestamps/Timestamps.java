package com.complyt.domain.timestamps;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class Timestamps {
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
