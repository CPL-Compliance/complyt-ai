package com.complyt.domain.customer.exemption;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class Classification {
    private final String code;
    private final String description;
}
