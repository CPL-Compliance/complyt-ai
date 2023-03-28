package com.complyt.domain;

import lombok.Value;

@Value
public class Address {
    String city;
    String country;
    String county;
    String state;
    String street;
    String zip;
}
