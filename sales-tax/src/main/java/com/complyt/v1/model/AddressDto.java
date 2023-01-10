package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
@With
@ToString
@EqualsAndHashCode
@Schema(name = "Address")
public class AddressDto {

    @NonNull
    @NotEmpty
    @Max(100)
    private String city;

    @NonNull
    @NotEmpty
    @Max(100)
    private String country;

    @Max(100)
    private String county;

    @NonNull
    @NotEmpty
    @Max(100)
    private String state;

    @NonNull
    @NotEmpty
    @Max(100)
    private String street;

    @NonNull
    @NotEmpty
    @Max(100)
    private String zip;
}
