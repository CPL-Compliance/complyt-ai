package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@With
@ToString
@EqualsAndHashCode
@Schema(name = "PhysicalNexusTracker")
public class PhysicalNexusTrackerDto {
    private boolean established;
    private LocalDateTime establishedDate;
}
