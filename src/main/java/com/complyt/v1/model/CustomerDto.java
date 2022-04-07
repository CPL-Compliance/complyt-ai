package com.complyt.v1.model;

import com.complyt.domain.Address;
import lombok.*;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@ToString
public class CustomerDto {
    private String id;
    private String externalId;
    private String name;
    private Address address;
}
