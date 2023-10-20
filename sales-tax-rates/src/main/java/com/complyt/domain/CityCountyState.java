package com.complyt.domain;

import lombok.With;

@With
public record CityCountyState(String city, String county, String state) {
}
