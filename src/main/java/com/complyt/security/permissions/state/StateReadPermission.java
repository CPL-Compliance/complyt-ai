package com.complyt.security.permissions.state;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('state.read') OR hasAuthority('customer.state.read') OR hasAuthority('user.state.read')")
public @interface StateReadPermission {
}