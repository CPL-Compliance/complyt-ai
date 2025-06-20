package io.complyt.domain.nexus;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.complyt.domain.nexus.enums.Definition;
import lombok.*;

import java.math.BigDecimal;

@Data
@With
@JsonIgnoreProperties(ignoreUnknown = true)
public class NexusThreshold {
    private BigDecimal amount;
    private int count;
    private Definition definition; // Specifying the way to check if threshold exceeded (e.g amount and count / amount or count)


        @JsonCreator
        public NexusThreshold(@JsonProperty("amount") BigDecimal amount,
                              @JsonProperty("count") int count,
                              @JsonProperty("definition") Definition definition) {
            this.amount = amount;
            this.count = count;
            this.definition = definition;
        }

}
