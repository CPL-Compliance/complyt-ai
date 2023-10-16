package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.v1.models.ApiKeyDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test_utils.unit_tests.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ApiKeyMapperTest {

    @Test
    void apiKeyDtoToApiKey() {
        // Given
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();
        ApiKey apiKey = TestUtilities.createApiKey();

        // Then
        ApiKey actualApiKey = ApiKeyMapper.INSTANCE.apiKeyDtoToApiKey(apiKeyDto);

        assertEquals(apiKey, actualApiKey);
    }

    @Test
    void apiKeyDtoToApiKey_apiKeyDtoIsNull_throwsNullException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            ApiKeyMapper.INSTANCE.apiKeyDtoToApiKey(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "apiKeyDto is marked non-null but is null");
    }
}