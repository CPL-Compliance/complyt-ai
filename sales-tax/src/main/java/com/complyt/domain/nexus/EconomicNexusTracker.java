package com.complyt.domain.nexus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@With
@ToString
public class EconomicNexusTracker {
    private boolean established;
    private LocalDateTime establishedDate;
}
