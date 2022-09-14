package com.complyt.v1.model.customer.exemption;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class StateDto {
    private String abbreviation;
    private String code;
    private String name;
}