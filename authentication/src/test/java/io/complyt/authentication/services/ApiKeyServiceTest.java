package io.complyt.authentication.services;

import io.complyt.authentication.security.ApiKeyGenerator;
import io.complyt.authentication.v1.models.ApiKey;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_utils.unit_tests.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
class ApiKeyServiceTest {

    @InjectMocks
    ApiKeyService apiKeyService;

    @Test
    void generate_returnsApiKeyInStringFormat() {
        try (MockedStatic<ApiKeyGenerator> utilities = Mockito.mockStatic(ApiKeyGenerator.class)) {
            String apiKey = TestUtilities.apiKeyStr;
            utilities.when(ApiKeyGenerator::generate).thenReturn(apiKey);
            assertEquals(apiKey, apiKeyService.generate());
        }
    }

    @Test
    void generatefromString_returnApiKey() {
        // Given
        String apiKeyStr = TestUtilities.apiKeyStr;
        ApiKey expectedApiKey = new ApiKey(apiKeyStr);

        ApiKey actualApiKey = apiKeyService.generatefromString(apiKeyStr);

        assertEquals(expectedApiKey, actualApiKey);
    }
}