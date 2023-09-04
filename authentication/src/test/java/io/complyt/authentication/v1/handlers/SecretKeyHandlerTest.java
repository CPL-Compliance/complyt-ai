package io.complyt.authentication.v1.handlers;

import io.complyt.authentication.services.AesSecretKeyService;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class SecretKeyHandlerTest {

    @InjectMocks
    SecretKeyHandler secretKeyHandler;

    @Mock
    AesSecretKeyService aesSecretKeyService;

    @Test
    void get_serverRequestIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            secretKeyHandler.get(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "serverRequest is marked non-null but is null");
    }
}