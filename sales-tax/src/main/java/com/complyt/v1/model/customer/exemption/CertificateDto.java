package com.complyt.v1.model.customer.exemption;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class CertificateDto {
    private final String id;
    private final String url;
    private final String name;
}
