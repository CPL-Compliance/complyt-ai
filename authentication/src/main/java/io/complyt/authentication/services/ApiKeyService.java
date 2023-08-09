package io.complyt.authentication.services;

import io.complyt.authentication.security.ApiKeyGenerator;
import io.complyt.authentication.v1.models.ApiKey;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
