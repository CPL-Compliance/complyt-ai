package io.complyt.authentication.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AesSecretKeyUtilsTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void generateKey() {
        SecretKey expectedKey = AesSecretKeyUtils.generateAesKey(256);

        assertEquals("AES", expectedKey.getAlgorithm());
    }

    @Test
    void getKeyFromPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKey expectedKey = AesSecretKeyUtils.getKeyFromPassword("password", "salt");

        assertEquals("AES", expectedKey.getAlgorithm());
    }

    @Test
    void convertSecretKeyToString() {
        String secretKeyStr = "test";

        // When
        SecretKey secretKeyFromString = AesSecretKeyUtils.convertStringToSecretKey(secretKeyStr);
        String stringFromSecretKey = AesSecretKeyUtils.convertSecretKeyToString(secretKeyFromString);

        assertEquals(stringFromSecretKey, secretKeyStr);
    }

    @Test
    void convertStringToSecretKey() {
        // Given
        byte[] expectedSecretKey = new byte[]{-75, -21, 45};
        String secretKeyStr = "test";

        // When
        SecretKey actualSecretKey = AesSecretKeyUtils.convertStringToSecretKey(secretKeyStr);

        // Then
        assertEquals(Arrays.equals(actualSecretKey.getEncoded(), expectedSecretKey), true);
    }
}