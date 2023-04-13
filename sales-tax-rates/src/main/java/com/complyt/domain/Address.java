package com.complyt.domain;

import lombok.Value;
import lombok.With;

@Value
@With
public class Address {
    String city;
    String country;
    String county;
    String state;
    String street;
    String zip;
}
