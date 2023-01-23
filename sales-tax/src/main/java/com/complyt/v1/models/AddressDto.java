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
    @Size(max = 256, message = "City may be 256 characters maximum")
    private String city;

    @NonNull
    @NotBlank(message = "Country may not be blank")
    @Size(max = 256, message = "Country may be 256 characters maximum")
    private String country;

    @Size(max = 256, message = "County me be 256 characters maximum")
    private String county;

    @NonNull
    @NotBlank(message = "State may not be blank")
    @Size(max = 256, message = "State may be 256 characters maximum")
    private String state;

    @NonNull
    @NotBlank(message = "Street may not be blank")
    @Size(max = 256, message = "Street may be 256 characters maximum")
    private String street;

    @NonNull
    @NotBlank(message = "ZIP may not be blank")
    @Size(max = 10, message = "ZIP may be 10 characters maximum")
    private String zip;
}
