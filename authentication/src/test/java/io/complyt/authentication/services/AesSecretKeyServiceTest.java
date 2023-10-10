package io.complyt.authentication.services;

import io.complyt.authentication.security.AesSecretKeyUtils;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class AesSecretKeyServiceTest {
    @InjectMocks
    AesSecretKeyService aesSecretKeyService;

    @Test
    void generate256AesKey() {
        // Given
        SecretKey expectedSecretKey = AesSecretKeyUtils.generateAesKey(256);
        try (MockedStatic<AesSecretKeyUtils> utilities = Mockito.mockStatic(AesSecretKeyUtils.class)) {
            // When
            utilities.when(() -> AesSecretKeyUtils.generateAesKey(256)).thenReturn(expectedSecretKey);
            SecretKey actualSecretKey = aesSecretKeyService.generate256AesKey();

            // Then
            assertEquals(expectedSecretKey, actualSecretKey);
        }
    }

    @Test
    void convertSecretKeyToString() {
        // Given
        SecretKey expectedSecretKey = AesSecretKeyUtils.generateAesKey(256);
        String expectedSecretKeyStr = AesSecretKeyUtils.convertSecretKeyToString(expectedSecretKey);

        try (MockedStatic<AesSecretKeyUtils> utilities = Mockito.mockStatic(AesSecretKeyUtils.class)) {
            // When
            utilities.when(() -> AesSecretKeyUtils.convertSecretKeyToString(expectedSecretKey)).thenReturn(expectedSecretKeyStr);
            String actualSecretKey = aesSecretKeyService.convertSecretKeyToString(expectedSecretKey);

            // Then
            assertEquals(expectedSecretKeyStr, actualSecretKey);
        }
    }
}