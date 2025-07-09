package io.complyt.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.With;

import java.util.Objects;

@With
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Excludes null fields in JSON response
public record Address(String city, String country, String county, String state, String street, String zip,
                      String region,
                      Boolean isPartial) {

    public Address {
        if (isPartial == null) {
            if (Objects.equals(country, "USA")) {
                isPartial = state == null || zip == null || city == null || street == null;
            } else {
                isPartial = country == null || region == null || zip == null;
            }
        }
    }
}