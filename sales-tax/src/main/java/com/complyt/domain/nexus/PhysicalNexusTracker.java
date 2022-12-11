package com.complyt.domain.nexus;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@With
@ToString
@EqualsAndHashCode
public class PhysicalNexusTracker {
    private boolean established;
    private LocalDateTime establishedDate;
}
