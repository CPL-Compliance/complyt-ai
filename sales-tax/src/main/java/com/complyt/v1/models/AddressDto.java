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

    @NonNull
    @NotBlank(message = "City may not be blank")
    @Size(min = 1, max = 256, message = "City should be 1-256 characters maximum")
    private String city;

    @NonNull
    @NotBlank(message = "Country may not be blank")
    @Size(min = 1, max = 256, message = "Country should be 1-256 characters maximum")
    private String country;

    @Size(min = 1, max = 256, message = "County should be 1-256 characters maximum")
    private String county;

    @NonNull
    @NotBlank(message = "State may not be blank")
    @Size(min = 1, max = 256, message = "State should be 1-256 characters maximum")
    private String state;

    @NonNull
    @NotBlank(message = "Street may not be blank")
    @Size(min = 1, max = 256, message = "Street should be 1-256 characters maximum")
    private String street;

    @NonNull
    @NotBlank(message = "ZIP may not be blank")
    @Size(min = 1, max = 10, message = "ZIP should be 1-256 characters maximum")
    private String zip;
}