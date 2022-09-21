package com.complyt.security.permissions.exemption;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('exemption.read') OR hasAuthority('customer.exemption.read') OR hasAuthority('user.exemption.read')")
public @interface ExemptionReadPermission {
}
