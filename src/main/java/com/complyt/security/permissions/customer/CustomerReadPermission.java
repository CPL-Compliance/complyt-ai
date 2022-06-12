package com.complyt.security.permissions.customer;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('customer.read') OR hasAuthority('customer.customer.read') OR hasAuthority('user.customer.read')")
public @interface CustomerReadPermission {
}
