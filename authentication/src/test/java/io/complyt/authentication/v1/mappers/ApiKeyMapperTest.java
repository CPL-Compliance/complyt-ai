package io.complyt.authentication.v1.mappers;

import io.complyt.authentication.v1.models.ApiKey;
import io.complyt.authentication.v1.models.ApiKeyDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}