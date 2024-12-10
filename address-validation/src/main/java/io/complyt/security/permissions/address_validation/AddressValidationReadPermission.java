package io.complyt.security.permissions.address_validation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('SCOPE_read:address_validation')")
public @interface AddressValidationReadPermission {
}