package io.complyt.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@With
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {
    private String abbreviation;
    private String code;
    private String name;

    @JsonCreator
    public State(@JsonProperty("abbreviation") String abbreviation,
                 @JsonProperty("code") String code,
                 @JsonProperty("name") String name)
    {
        this.abbreviation = abbreviation;
        this.code = code;
        this.name = name;
    }
}