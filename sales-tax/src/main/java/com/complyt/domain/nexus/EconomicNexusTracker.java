package com.complyt.domain.nexus;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@With
@ToString
@EqualsAndHashCode
public class EconomicNexusTracker {
    private boolean established;
    private LocalDateTime establishedDate;
}
