package io.complyt.authentication.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CryptorAesCbcPkcs5PaddingTest {
    Cryptor cryptor;
    @BeforeEach
    void setUp() {
        String secretKeyStr = "cWrkCbX1JKCiWYFDx9DsHKqdn38QK5o3";
        cryptor = new CryptorAesCbcPkcs5Padding(secretKeyStr);
    }

    @Test
    void encrypt() {
        cryptor.encrypt();
    }

    @Test
    void decrypt() {
        cryptor.decrypt();
    }
}