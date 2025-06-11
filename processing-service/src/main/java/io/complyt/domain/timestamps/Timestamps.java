package io.complyt.domain.timestamps;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@ToString
@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Timestamps {

    @NonNull
    private final LocalDateTime createdDate;

    @NonNull
    private final LocalDateTime updatedDate;

    @JsonCreator
    public Timestamps(
            @JsonProperty("createdDate") @NonNull LocalDateTime createdDate,
            @JsonProperty("updatedDate") @NonNull LocalDateTime updatedDate
    ) {
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
}
