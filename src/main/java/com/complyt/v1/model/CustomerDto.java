package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Schema(name = "Customer")
public class CustomerDto {
    private String id;
    private String externalId;
    private String name;
    private AddressDto address;
}
