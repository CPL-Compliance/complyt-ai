package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@With
@ToString
@EqualsAndHashCode
@Schema(name = "Address")
public class AddressDto {

    @NonNull
    @NotBlank(message = "City may not be blank")
    @Max(value = 256, message = "256 characters maximum")
    private String city;

    @NonNull
    @NotBlank(message = "Country may not be blank")
    @Max(value = 256, message = "256 characters maximum")
    private String country;

    @Max(value = 256, message = "256 characters maximum")
    private String county;

    @NonNull
    @NotBlank(message = "State may not be blank")
    @Max(value = 256, message = "256 characters maximum")
    private String state;

    @NonNull
    @NotBlank(message = "Street may not be blank")
    @Max(value = 256, message = "256 characters maximum")
    private String street;

    @NonNull
    @NotBlank(message = "ZIP may not be blank")
    @Max(value = 256, message = "256 characters maximum")
    private String zip;
}
