package com.complyt.v1.model.customer;

import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Schema(name = "Customer")
public class CustomerDto {

    @Max(256)
    String id;

    @NotEmpty
    @NonNull
    @Max(256)
    String externalId;

    @NotEmpty
    @NonNull
    @Max(256)
    String name;

    @NotEmpty
    @NonNull
    AddressDto address;

    @NotEmpty
    @NonNull
    CustomerTypeDto customerType;

    @NotEmpty
    @NonNull
    TimestampsDto internalTimestamps;

    @NotEmpty
    @NonNull
    TimestampsDto externalTimestamps;
}
