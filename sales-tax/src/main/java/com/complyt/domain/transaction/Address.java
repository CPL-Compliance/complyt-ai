package com.complyt.domain.transaction;

import lombok.With;

@With
public record Address(String city, String country, String county, String state, String street, String zip, String region,
                      boolean isPartial) implements BaseAddress {
}