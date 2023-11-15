package io.complyt.authentication.security;

import io.complyt.authentication.config.CryptoConfig;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CryptoAesGcmNoPaddingTest {
    Crypto cryptoAesGcmNoPadding;

    @BeforeEach
    void setUp() {
        String secretKeyStr = "cWrkCbX1JKCiWYFDx9DsHKqdn38QK5o3";
        cryptoAesGcmNoPadding = (new CryptoConfig()).cryptoAesGcmNoPadding(secretKeyStr);
    }

    @Test
    void encrypt_decrypt_someString_decryptedTextEqualsOriginalText() throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException {
        // Given
        String input = "This is an input text";

        // When
        EncryptedData encryptedData = cryptoAesGcmNoPadding.encrypt(input);
        String decrypted = cryptoAesGcmNoPadding.decrypt(encryptedData);

        // Then
        assertEquals(input, decrypted);
    }

    @Test
    void encrypt_plainTextIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            cryptoAesGcmNoPadding.encrypt(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "plainText is marked non-null but is null");
    }

    @Test
    void decrypt_encryptedDataIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            cryptoAesGcmNoPadding.decrypt(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "encryptedData is marked non-null but is null");
    }
}