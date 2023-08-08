package io.complyt.authentication.security.permissions.api_key;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('SCOPE_create:secret_key')")
public @interface SecretKeyCreatePermission {
}
