package com.complyt.domain.customer.exemption;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class Certificate {
    private final String certificateId;
    private final String url;
    private final String name;
}