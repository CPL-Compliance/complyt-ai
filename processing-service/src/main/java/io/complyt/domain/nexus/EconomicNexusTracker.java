package io.complyt.domain.nexus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;
import java.time.Month;

@Data
@With
@JsonIgnoreProperties(ignoreUnknown = true)
public class EconomicNexusTracker {
    private boolean established;
    private LocalDateTime establishedDate;

    @JsonCreator
    public EconomicNexusTracker(@JsonProperty("established") boolean established, @JsonProperty("establishedDate") LocalDateTime establishedDate) {
        this.established = established;
        this.establishedDate = establishedDate;
    }

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
        return new EconomicNexusTracker(DEFAULT_ESTABLISHED,
                LocalDateTime.of(DEFAULT_YEAR_ESTABLISHED_DATE, DEFAULT_MONTH_ESTABLISHED_DATE, DEFAULT_DAY_ESTABLISHED_DATE, 0, 0, 0));
    }

}