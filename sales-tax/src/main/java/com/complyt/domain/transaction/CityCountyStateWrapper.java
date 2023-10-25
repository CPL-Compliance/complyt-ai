package com.complyt.domain.transaction;


import lombok.With;

@With
public record CityCountyStateWrapper(String city, String county, String state) {
}

