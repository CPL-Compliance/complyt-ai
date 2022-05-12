package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@With
@EqualsAndHashCode
@Schema(name = "Address")
public class AddressDto {
    private String city;
    private String country;
    private String county;
    private String state;
    private String street;
    private String zip;
}
