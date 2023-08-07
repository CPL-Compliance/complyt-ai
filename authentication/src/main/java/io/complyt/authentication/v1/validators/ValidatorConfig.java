package io.complyt.authentication.v1.validators;

import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.CredentialsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Configuration
public class ValidatorConfig {
    @Bean
    ValidationHandler<CredentialsDto, SpringValidatorAdapter> credentialsDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(CredentialsDto.class, springValidatorAdapter);
    }

    @Bean
    ValidationHandler<ApiKeyDto, SpringValidatorAdapter> apiKeyDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(ApiKeyDto.class, springValidatorAdapter);
    }
}
