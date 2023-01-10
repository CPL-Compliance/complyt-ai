package com.complyt.v1.model.customer;

import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Schema(name = "Customer")
public class CustomerDto {

    @Max(value = 256, message = "256 characters maximum")
    String id;

    @NonNull
    @NotBlank(message = "External ID may not be blank")
    @Max(value = 256, message = "256 characters maximum")
    String externalId;

    @NonNull
    @NotBlank(message = "Name may not be blank")
    @Max(value = 256, message = "256 characters maximum")
    String name;

    @NonNull
    @NotNull(message = "Address may not be null")
    AddressDto address;

    @NonNull
    @NotNull(message = "Customer type may not be null")
    CustomerTypeDto customerType;

    @NonNull
    @NotNull(message = "Internal timestamps may not be null")
    TimestampsDto internalTimestamps;

    @NonNull
    @NotNull(message = "External timestamps may not be null")
    TimestampsDto externalTimestamps;
}