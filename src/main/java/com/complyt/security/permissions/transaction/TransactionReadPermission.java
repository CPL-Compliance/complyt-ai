package com.complyt.security.permissions.transaction;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('transaction.read') OR hasAuthority('customer.transaction.read') OR hasAuthority('user.transaction.read')")
public @interface TransactionReadPermission {
}
