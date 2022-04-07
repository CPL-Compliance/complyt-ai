package com.complyt.v1.model;

import com.complyt.domain.Address;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class CustomerDto {
    private String externalId;
    private String name;
    private Address address;
}
