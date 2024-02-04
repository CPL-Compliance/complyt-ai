package io.complyt.authentication.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class TenantIdAndNameObject {
    private String tenantId;
    private String name;
}