package com.complyt.v1.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@With
@EqualsAndHashCode
@ApiModel("Address")
public class AddressDto {
    private String city;
    private String country;
    private String county;
    private String state;
    private String street;
    private String zip;
}
