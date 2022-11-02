package com.complyt.security.permissions.exemption;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('SCOPE_update:exemption')")
public @interface ExemptionUpdatePermission {
}
