package io.complyt.authentication.security;

import io.complyt.authentication.domain.ApiKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ApiKeyGeneratorTest {
    @Test
    void generate_none_validApiKey() {
        String apiKey = ApiKeyGenerator.generate();
        System.out.println(apiKey);
        new ApiKey(apiKey);
    }

    @Test
    void generate_badApiKeyFormat_throwsIllegalArgumentException(){
        // When
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            new ApiKey("9a62acdf-cc85-4009-a57b-cf77c3eba1ec-3572db2e-486b-480a-995b-");
        });

        // Then
        assertEquals(illegalArgumentException.getMessage(), "Invalid API key format");
    }
}