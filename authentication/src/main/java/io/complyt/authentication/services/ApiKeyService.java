package io.complyt.authentication.services;

import io.complyt.authentication.security.ApiKeyGenerator;
import io.complyt.authentication.v1.models.ApiKey;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    public String generate(){
        return ApiKeyGenerator.generate();
    }

    public ApiKey generatefromString(String apiKeyStr){
        return new ApiKey(apiKeyStr);
    }
}
