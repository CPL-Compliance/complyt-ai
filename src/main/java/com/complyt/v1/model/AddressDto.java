package com.complyt.v1.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressDto {
    private String city;
    private String country;
    private String state;
    private String street;
    private String zip;
}
