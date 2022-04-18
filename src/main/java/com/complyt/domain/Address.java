package com.complyt.domain;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class Address {
    private String city;
    private String country;
    private String county;
    private String state;
    private String street;
    private String zip;
}