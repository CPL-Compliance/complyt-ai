package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.With;

@With
@Schema(name = "Address")
public record OptionalAddressDto(
        @Size(max = 100, message = "City should be 1-256 characters maximum") String city,
        @Size(max = 50, message = "Country should be 1-256 characters maximum") String country,
        @Size(max = 100, message = "County should be 1-256 characters maximum") String county,
        @Size(max = 100, message = "State should be 1-256 characters maximum") String state,
        @Size(max = 200, message = "Street should be 1-256 characters maximum") String street,
        @Size(max = 20, message = "ZIP should be 1-20 characters maximum") String zip) {

}