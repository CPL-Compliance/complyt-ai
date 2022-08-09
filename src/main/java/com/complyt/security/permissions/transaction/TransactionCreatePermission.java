package com.complyt.security.permissions.transaction;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('transaction.create') OR hasAuthority('customer.transaction.create')")
public @interface TransactionCreatePermission {
}