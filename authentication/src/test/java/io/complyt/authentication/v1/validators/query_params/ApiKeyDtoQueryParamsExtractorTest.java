package io.complyt.authentication.v1.validators.query_params;

import io.complyt.authentication.v1.mappers.ApiKeyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ApiKeyDtoQueryParamsExtractorTest {
    ApiKeyDtoQueryParamsExtractor apiKeyDtoQueryParamsExtractor;
    @BeforeEach
    void setUp() {
        apiKeyDtoQueryParamsExtractor = new ApiKeyDtoQueryParamsExtractor();
    }

    @Test
    void extract() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            apiKeyDtoQueryParamsExtractor.extract(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "serverRequest is marked non-null but is null");
    }
}