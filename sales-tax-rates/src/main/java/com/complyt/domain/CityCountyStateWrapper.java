package com.complyt.domain;

import lombok.With;

@With
public record CityCountyStateWrapper(String city, String county, String state) {
}
