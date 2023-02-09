package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@AllArgsConstructor
@With
@ToString
@EqualsAndHashCode
@Schema(name = "Address")
public class AddressDto {


    @NotBlank(message = "City may not be blank")
    @Size(min = 1, max = 256, message = "City should be 1-256 characters maximum")
    private String city;

    @NotBlank(message = "Country may not be blank")
    @Size(min = 1, max = 256, message = "Country should be 1-256 characters maximum")
    private String country;

    @Size(min = 1, max = 256, message = "County should be 1-256 characters maximum")
    private String county;

    @NotBlank(message = "State may not be blank")
    @Size(min = 1, max = 256, message = "State should be 1-256 characters maximum")
    private String state;

    @NotBlank(message = "Street may not be blank")
    @Size(min = 1, max = 256, message = "Street should be 1-256 characters maximum")
    private String street;

    @NotBlank(message = "ZIP may not be blank")
    @Size(min = 1, max = 10, message = "ZIP should be 1-256 characters maximum")
    private String zip;
}