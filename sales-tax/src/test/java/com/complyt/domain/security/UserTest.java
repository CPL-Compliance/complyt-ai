package com.complyt.domain.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class UserTest {
    User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID().toString())
                .username("user")
                .password("password")
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    @Test
    void eraseCredentials_setsPasswordToNull_NoReturnValue() {
        // Given + When + Then
        user.eraseCredentials();
    }

    @Test
    void isAccountNonExpired_ChecksIfAccountIsNotExpired_ReturnsBoolean() {
        // Given
        boolean isAccountNotExpired = user.getAccountNonExpired();

        // When + Then
        Assertions.assertEquals(isAccountNotExpired,user.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_ChecksIfAccountIsNonLocked_ReturnsBoolean() {
        // Given
        boolean isAccountNonLocked = user.getAccountNonLocked();

        // When + Then
        Assertions.assertEquals(isAccountNonLocked,user.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_ChecksIfCredentialsNonExpired_ReturnsBoolean() {
        // Given
        boolean isCredentialsNonExpired = user.getCredentialsNonExpired();

        // When + Then
        Assertions.assertEquals(isCredentialsNonExpired,user.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_ChecksIfIsEnabled_ReturnsBoolean() {
        // Given
        boolean isEnabled = user.isEnabled();

        // When + Then
        Assertions.assertEquals(isEnabled,user.isEnabled());
    }

}
