package com.complyt.security.permissions.sales_tax_tracking;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('SCOPE_update:nexus')")
public @interface NexusUpdatePermission {
}