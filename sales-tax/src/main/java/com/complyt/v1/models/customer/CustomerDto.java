package com.complyt.v1.models.customer;

import com.complyt.v1.models.AddressDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Schema(name = "Customer")
public class CustomerDto {

    @Size(max = 256, message = "ID should be 256 characters maximum")
    String id;

    @NonNull
    @NotBlank(message = "External ID may not be blank")
    @Size(max = 256, message = "External ID should be 256 characters maximum")
    String externalId;

    @NonNull
    @NotBlank(message = "Name may not be blank")
    @Size(max = 256, message = "Name should be 256 characters maximum")
    String name;

    @NonNull
    @Valid
    @NotNull(message = "Address may not be null")
    AddressDto address;

    @NonNull
    @NotNull(message = "Customer type may not be null")
    CustomerTypeDto customerType;

    @NonNull
    @Valid
    @NotNull(message = "Internal timestamps may not be null")
    TimestampsDto internalTimestamps;

    @NonNull
    @Valid
    @NotNull(message = "External timestamps may not be null")
    TimestampsDto externalTimestamps;
}