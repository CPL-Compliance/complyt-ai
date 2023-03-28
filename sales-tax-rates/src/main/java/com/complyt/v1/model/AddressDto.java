package com.complyt.v1.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressDto(
        @NotBlank(message = "City may not be blank") @Size(max = 100, message = "City should be 1-100 characters maximum") String city,
        @Size(min = 1, max = 100, message = "Country should be 1-100 characters maximum") String country,
        @Size(min = 1, max = 100, message = "County should be 1-100 characters maximum") String county,
        @NotBlank(message = "State may not be blank") @Size(max = 100, message = "State should be 1-100 characters maximum") String state,
        @NotBlank(message = "Street may not be blank") @Size(max = 200, message = "Street should be 1-200 characters maximum") String street,
        @NotBlank(message = "ZIP may not be blank") @Size(max = 20, message = "ZIP should be 1-20 characters maximum") String zip) {

}