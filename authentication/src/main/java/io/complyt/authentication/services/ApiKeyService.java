package io.complyt.authentication.services;

import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.security.ApiKeyGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ApiKeyService {
    public String generate(){
        return ApiKeyGenerator.generate();
    }

    public ApiKey generatefromString(String apiKey){
        return new ApiKey(apiKey);
    }
}
