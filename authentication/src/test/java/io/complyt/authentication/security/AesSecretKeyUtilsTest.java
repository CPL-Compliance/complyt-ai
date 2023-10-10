package io.complyt.authentication.security;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

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
    void getKeyFromPassword_passwordIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            AesSecretKeyUtils.getKeyFromPassword(null, "salt");
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "password is marked non-null but is null");
    }

    @Test
    void getKeyFromPassword_saltIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            AesSecretKeyUtils.getKeyFromPassword("password", null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salt is marked non-null but is null");
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

    @Test
    void convertSecretKeyToString_secretKeyIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            AesSecretKeyUtils.convertSecretKeyToString(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "secretKey is marked non-null but is null");
    }

    @Test
    void convertStringToSecretKey_encodedKeyIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            AesSecretKeyUtils.convertStringToSecretKey(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "encodedKey is marked non-null but is null");
    }
}