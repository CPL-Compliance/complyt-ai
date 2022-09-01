package com.complyt.v1.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class TimeStampsDto {
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
