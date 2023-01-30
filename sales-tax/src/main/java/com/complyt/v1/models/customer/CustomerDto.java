package com.complyt.v1.models.customer;

import com.complyt.v1.models.AddressDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;


@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Schema(name = "Customer")
public class CustomerDto {


    UUID complytId;

    @NonNull
    @NotBlank(message = "External ID may not be blank")
    @Size(min = 1, max = 256, message = "External ID length should be 1-256 characters maximum")
    String externalId;

    @NonNull
    @NotBlank(message = "External ID may not be blank")
    @Pattern(regexp = "[0-9]", message = "source should be a single digit")
    String source;

    @NonNull
    @NotBlank(message = "Name may not be blank")
    @Size(min = 1, max = 256, message = "Name should be 1-256 characters maximum")
    String name;

    @NonNull
    @Valid
    @NotNull(message = "Address may not be null")
    AddressDto address;

    @NonNull
    @NotNull(message = "Customer type may not be null")
    CustomerTypeDto customerType;

    @Valid
    TimestampsDto internalTimestamps;

    @NonNull
    @Valid
    @NotNull(message = "External timestamps may not be null")
    TimestampsDto externalTimestamps;
}