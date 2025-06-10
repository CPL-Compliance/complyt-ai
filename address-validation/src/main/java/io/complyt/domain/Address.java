package io.complyt.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

@With
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
public record Address(String city, String country, String county, String state, String street, String zip,
                      String region,
                      Boolean isPartial) {
    public Address {
        if (isPartial == null) {
            isPartial = true;
        }
    }
}