package io.complyt.authentication.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AesSecretKeyUtilsTest {
    @Test
    void createAesSecretKeyUtils() {
        AesSecretKeyUtils aesSecretKeyUtils = new AesSecretKeyUtils();
    }

    @Test
    void generateKey_validData_correctAlgorithm() {
        SecretKey expectedKey = AesSecretKeyUtils.generateAesKey(256);

        assertEquals("AES", expectedKey.getAlgorithm());
    }

    @Test
    void generateKey_getInstanceThrowsException_returnNull() {
        try (MockedStatic<KeyGenerator> utilities = Mockito.mockStatic(KeyGenerator.class)) {
            utilities.when(() -> KeyGenerator.getInstance(any())).thenThrow(new NoSuchAlgorithmException());
            assertNull(AesSecretKeyUtils.generateAesKey(256));
        }
    }

    @Test
    void getKeyFromPassword_validData_returnsCorrectAlgorithm() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKey expectedKey = AesSecretKeyUtils.getKeyFromPassword("password", "salt");

        assertEquals("AES", expectedKey.getAlgorithm());
    }

    @Test
    void convertSecretKeyToString_validData_returnsCorrectString() {
        String secretKeyStr = "test";

        // When
        SecretKey secretKeyFromString = AesSecretKeyUtils.convertStringToSecretKey(secretKeyStr);
        String stringFromSecretKey = AesSecretKeyUtils.convertSecretKeyToString(secretKeyFromString);

        assertEquals(stringFromSecretKey, secretKeyStr);
    }

    @Test
    void convertStringToSecretKey_validData_returnsCorrectSecretKey() {
        // Given
        byte[] expectedSecretKey = new byte[]{-75, -21, 45};
        String secretKeyStr = "test";

        // When
        SecretKey actualSecretKey = AesSecretKeyUtils.convertStringToSecretKey(secretKeyStr);

        // Then
        assertEquals(Arrays.equals(actualSecretKey.getEncoded(), expectedSecretKey), true);
    }
}