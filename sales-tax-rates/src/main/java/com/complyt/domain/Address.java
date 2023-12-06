package com.complyt.domain;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record Address(String city, String country, String county, String state, String street, String zip,
                      boolean isPartial) {
}
