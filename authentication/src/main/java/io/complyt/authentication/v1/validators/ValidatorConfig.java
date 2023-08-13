package io.complyt.authentication.v1.validators;

import io.complyt.authentication.v1.models.ApiKey;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.CredentialsDto;
import io.complyt.authentication.v1.validators.query_params.QueryParamsExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.Map;

@Configuration
public class ValidatorConfig {
    @Bean
    ValidationHandler<CredentialsDto, SpringValidatorAdapter> credentialsDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                                                                              @Autowired QueryParamsExtractor credentialsDtoQueryParamsExtractor) {
        return new ValidationHandler<>(CredentialsDto.class, springValidatorAdapter,
                new DataConflictChecksProvider(null, Map.of()),
                credentialsDtoQueryParamsExtractor);
    }

    @Bean
    public ValidationHandler<ApiKey, SpringValidatorAdapter> apiKeyValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                                                                     @Autowired QueryParamsExtractor apiKeyQueryParamsExtractor) {
        return new ValidationHandler<>(ApiKey.class,
                springValidatorAdapter,
                new DataConflictChecksProvider(null, Map.of()),
                apiKeyQueryParamsExtractor);
    }
}
