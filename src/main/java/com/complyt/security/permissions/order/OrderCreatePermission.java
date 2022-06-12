package com.complyt.security.permissions.order;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.create') OR hasAuthority('customer.order.create')")
public @interface OrderCreatePermission {
}