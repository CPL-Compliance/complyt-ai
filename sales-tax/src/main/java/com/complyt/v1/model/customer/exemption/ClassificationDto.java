package com.complyt.v1.model.customer.exemption;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class ClassificationDto {
    private final String code;
    private final String description;
}