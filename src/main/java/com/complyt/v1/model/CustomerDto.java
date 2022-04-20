package com.complyt.v1.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@With
@ApiModel("Customer")
public class CustomerDto {
    private String id;
    private String externalId;
    private String name;
    private AddressDto address;
}
