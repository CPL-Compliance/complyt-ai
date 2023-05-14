package com.complyt.security.permissions.sales_tax_rates;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('SCOPE_read:sales_tax_rates')")
public @interface SalesTaxRatesReadPermission {
}