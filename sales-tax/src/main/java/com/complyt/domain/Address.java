package com.complyt.domain;

import lombok.With;

@With
public record Address(String city, String country, String county, String state, String street, String zip) {
}