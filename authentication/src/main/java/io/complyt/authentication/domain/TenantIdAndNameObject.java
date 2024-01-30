package io.complyt.authentication.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TenantIdAndNameObject {
    private String tenantId;
    private String name;
}