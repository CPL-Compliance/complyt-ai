package io.complyt.authentication.domain;

import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class ApiKeyTest {
    @Test
    void createApiKey_BadFormat_throwIllegalArgumentException(){
        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new ApiKey("");
        });

        // Then
        assertEquals("Invalid API key format", exception.getMessage());
    }
}