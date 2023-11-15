package io.complyt.authentication.security;

import io.complyt.authentication.domain.ApiKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ApiKeyGeneratorTest {

    @Test
    void generate_none_validApiKey() {
        ApiKey apiKey = ApiKeyGenerator.generate();
        System.out.println(apiKey);
        new ApiKey(apiKey.clientId(), apiKey.clientSecret());
    }

}