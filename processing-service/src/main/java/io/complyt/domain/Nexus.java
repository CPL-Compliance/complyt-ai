package io.complyt.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@With
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Nexus {
    private LocalDateTime taxableDate;

    @JsonCreator
    public Nexus(@JsonProperty("taxableDate") LocalDateTime taxableDate) {
        this.taxableDate = taxableDate;
    }
}
