package io.complyt.authentication.security;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BcryptPasswordEncoderWrapper {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String encode(final @NonNull String input) {
        return passwordEncoder.encode(input);
    }
}
