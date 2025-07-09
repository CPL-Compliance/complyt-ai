package io.complyt.domain.nexus;

import java.time.LocalDateTime;

public record PhysicalNexusTracker(
        boolean established,
        LocalDateTime establishedDate
) {}
