package io.complyt.authentication.security;

import io.complyt.authentication.domain.ApiKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_utils.unit_tests.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ApiKeyGeneratorTest {
    @Test
    void generate_none_validApiKey() {
        ApiKey apiKey = ApiKeyGenerator.generate();
        System.out.println(apiKey);
        new ApiKey(apiKey.getClientId(), apiKey.getClientSecret());
    }

    @Test
    void generate_badClientIdFormat_throwsIllegalArgumentException() {
        // When
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            new ApiKey("cf77c3eba1ec-3572db2e-486b-480a-995bb", TestUtilities.apiKeyClientSecretStr);
        });

        // Then
        assertEquals(illegalArgumentException.getMessage(), "Invalid API key format");
    }

    @Test
    void generate_badClientSecretFormat_throwsIllegalArgumentException() {
        // When
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            new ApiKey(TestUtilities.apiKeyClientIdStr,"cf77c3eba1ec-3572db2e-486b-480a-995b-");
        });

        // Then
        assertEquals(illegalArgumentException.getMessage(), "Invalid API key format");
    }
}