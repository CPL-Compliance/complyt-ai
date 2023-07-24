package io.complyt.authentication.security;

import lombok.NonNull;

public record EncryptedData(@NonNull String text, @NonNull String iv, @NonNull String cipherText) {

}
