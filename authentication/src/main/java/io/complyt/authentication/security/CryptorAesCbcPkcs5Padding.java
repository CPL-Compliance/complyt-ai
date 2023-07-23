package io.complyt.authentication.security;


import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CryptorAesCbcPkcs5Padding implements Cryptor {

    @NonNull
    String secretKey;

    public String encrypt() {
        return "hi from encryptor " + secretKey;
    }

    public String decrypt() {
        return "hi from decryptor " + secretKey;
    }
}
