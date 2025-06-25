package io.complyt.domain.nexus;

import lombok.With;

import java.time.LocalDateTime;
import java.time.Month;

@With
public record EconomicNexusTracker(boolean established, LocalDateTime establishedDate) {

    private static final boolean DEFAULT_ESTABLISHED = false;
    private static final int DEFAULT_YEAR_ESTABLISHED_DATE = 2000;
    private static final int DEFAULT_MONTH_ESTABLISHED_DATE = 1;
    private static final int DEFAULT_DAY_ESTABLISHED_DATE = 1;

    public static final LocalDateTime DEFAULT_ESTABLISHED_DATE = LocalDateTime.of(
            DEFAULT_YEAR_ESTABLISHED_DATE,
            Month.of(DEFAULT_MONTH_ESTABLISHED_DATE),
            DEFAULT_DAY_ESTABLISHED_DATE,
            0, 0
    );

    public static EconomicNexusTracker build() {
        return new EconomicNexusTracker(
                DEFAULT_ESTABLISHED,
                DEFAULT_ESTABLISHED_DATE
        );
    }
}
