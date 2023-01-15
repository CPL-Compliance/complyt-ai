package com.complyt.v1.model.customer;

import com.complyt.annotations.Default;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@With
@Schema(name = "Customer")
public class CustomerDto{

    private final UUID complytId;
    private final String externalId;
    private final String source;
    private final String name;
    private final AddressDto address;
    private final CustomerTypeDto customerType;
    private final TimestampsDto internalTimestamps;
    private final TimestampsDto externalTimestamps;

}
