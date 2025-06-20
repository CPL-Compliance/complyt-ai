package io.complyt.domain.nexus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@With
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhysicalNexusTracker {
    private boolean established;
    private LocalDateTime establishedDate;

    @JsonCreator
    public PhysicalNexusTracker(@JsonProperty("established") boolean established, @JsonProperty("establishedDate") LocalDateTime establishedDate) {
        this.established = established;
        this.establishedDate = establishedDate;
    }
}
