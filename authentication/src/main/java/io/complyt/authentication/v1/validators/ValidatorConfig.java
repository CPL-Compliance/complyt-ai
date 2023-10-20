package io.complyt.authentication.v1.validators;

import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.CredentialsDto;
import io.complyt.authentication.v1.validators.query_params.QueryParamsExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Configuration
public class ValidatorConfig {
    @Bean
    ValidationHandler<CredentialsDto, SpringValidatorAdapter>
    credentialsDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                    @Autowired QueryParamsExtractor<CredentialsDto> queryParamsExtractorEmpty) {
        return new ValidationHandler<>(CredentialsDto.class, springValidatorAdapter,
                queryParamsExtractorEmpty);
    }

    @Bean
    public ValidationHandler<ApiKeyDto, SpringValidatorAdapter>
    apiKeyDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                               @Autowired QueryParamsExtractor<ApiKeyDto> queryParamsExtractorEmpty) {
        return new ValidationHandler<>(ApiKeyDto.class, springValidatorAdapter,
                queryParamsExtractorEmpty);
    }
}
