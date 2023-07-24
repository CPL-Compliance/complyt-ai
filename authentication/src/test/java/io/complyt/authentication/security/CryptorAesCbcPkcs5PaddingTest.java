package io.complyt.authentication.security;

import io.complyt.authentication.config.CryptorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CryptorAesCbcPkcs5PaddingTest {
    Cryptor cryptorAesCbcPkcs5Padding;

    @BeforeEach
    void setUp() throws NoSuchPaddingException, NoSuchAlgorithmException {
        String secretKeyStr = "cWrkCbX1JKCiWYFDx9DsHKqdn38QK5o3";
        cryptorAesCbcPkcs5Padding = (new CryptorConfig()).cryptorAesCbcPkcs5Padding(secretKeyStr);
    }

    @Test
    void encrypt__decrypt_someString_decryptedTextEqualsOriginalText() throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException {
        // Given
        String input = "This is an input text";

        // When
        EncryptedData encryptedData = cryptorAesCbcPkcs5Padding.encrypt(input);
        String decrypted = cryptorAesCbcPkcs5Padding.decrypt(encryptedData);

        // Then
        assertEquals(input, decrypted);
    }

    @Test
    void decrypt() {
    }
}