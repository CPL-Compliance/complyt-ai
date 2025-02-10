package com.complyt.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Address(String city, String country, String county, String state, String street, String zip,
                      Boolean isPartial) implements TaxableLocation{
}
