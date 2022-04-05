package com.complyt.v1.model;

import com.complyt.domain.Address;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class CustomerDto {
    private String externalId;
    private String name;
    private Address address;
}
