package com.complyt.v1.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ApiModel("Address")
public class AddressDto {
    private String city;
    private String country;
    private String county;
    private String state;
    private String street;
    private String zip;
}
