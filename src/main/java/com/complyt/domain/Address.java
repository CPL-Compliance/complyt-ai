package com.complyt.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Address {
    private String city;
    private String country;
    private String county;
    private String state;
    private String street;
    private String zip;
}